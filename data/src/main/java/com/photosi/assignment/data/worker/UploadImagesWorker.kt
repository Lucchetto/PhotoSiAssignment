package com.photosi.assignment.data.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.photosi.assignment.data.R
import com.photosi.assignment.data.util.TimeHelper
import com.photosi.assignment.domain.ImageQueueRepository
import com.photosi.assignment.domain.RemoteImagesRepository
import com.photosi.assignment.domain.entity.QueuedImageEntity
import com.photosi.assignment.time.TimeFormatters
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi
import com.photosi.assignment.domain.entity.Result as DomainResult

internal class UploadImagesWorker(
    appContext: Context,
    params: WorkerParameters,
    workManager: WorkManager,
    private val imagesQueueRepository: Lazy<ImageQueueRepository>,
    private val remoteImagesRepository: Lazy<RemoteImagesRepository>
) : CoroutineWorker(appContext, params) {

    private val notificationManager = NotificationManagerCompat.from(appContext)
    private val progressNotificationBuilder = NotificationCompat.Builder(appContext, PROGRESS_NOTIFICATION_CHANNEL_ID)
        .setOngoing(true)
        .setSmallIcon(R.drawable.outline_cloud_upload_24)
        .setTitleAndTicker(appContext.getString(R.string.upload_progress_notification_title))
        .launchLauncherActivityOnClick(appContext)
        .addAction(
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_delete,
                appContext.getString(R.string.cancel_label),
                workManager.createCancelPendingIntent(id)
            ).build()
        )

    override suspend fun doWork(): Result {
        // Prevent upload from being restarted on failure
        if (runAttemptCount > 1) {
            return Result.failure()
        }

        createProgressNotificationChannel()
        markAsForeground()

        val readyImages = imagesQueueRepository.value.listReadyImages()
        val successCount = uploadImages(readyImages)

        currentCoroutineContext().ensureActive()

        val failedCount = readyImages.size - successCount
        createResultNotificationChannel()
        notificationManager.notify(
            RESULT_NOTIFICATION_ID,
            buildResultNotification(successCount, failedCount)
        )

        return if (failedCount > 0) Result.failure() else Result.success()
    }

    private suspend fun markAsForeground() {
        val notification = progressNotificationBuilder.buildIndeterminateProgress()
        val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            ForegroundInfo(PROGRESS_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(PROGRESS_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(PROGRESS_NOTIFICATION_ID, notification)
        }

        setForeground(foregroundInfo)
    }

    private fun createProgressNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(
            PROGRESS_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(applicationContext.getString(R.string.upload_progress_notification_channel_name)).build()

        notificationManager.createNotificationChannel(channel)
    }

    private fun createResultNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(
            RESULT_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(applicationContext.getString(R.string.upload_result_notification_channel_name)).build()

        notificationManager.createNotificationChannel(channel)
    }

    private fun buildResultNotification(successCount: Int, failedCount: Int): Notification = with(applicationContext) {
        val description = if (failedCount > 0) {
            getString(R.string.upload_result_notification_failed_desc, successCount, failedCount)
        } else {
            getString(R.string.upload_result_notification_success_desc, successCount)
        }

        NotificationCompat.Builder(this, RESULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_cloud_upload_24)
            .setTitleAndTicker(getString(R.string.upload_result_notification_title))
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setContentText(description)
            .launchLauncherActivityOnClick(this)
            .setAutoCancel(true)
            .build()
    }

    /**
     * @return how many images were successfully uploaded
     */
    private suspend fun uploadImages(readyImages: List<QueuedImageEntity>): Int = withContext(Dispatchers.IO) {
        var successImages = 0
        val readyImagesAndFiles = readyImages.map {
            it to imagesQueueRepository.value.getFileForQueuedImage(it)
        }
        var uploadSizeLeft = readyImagesAndFiles.sumOf { it.second.length() }
        var estimatedRemainingTime: Long? = null

        readyImagesAndFiles.forEachIndexed { index, (image, file) ->
            ensureActive()
            notificationManager.notify(
                PROGRESS_NOTIFICATION_ID,
                progressNotificationBuilder
                    .buildWithProgress(applicationContext, index, readyImages.size, estimatedRemainingTime)
            )

            val startTime = SystemClock.elapsedRealtime()
            if (uploadImage(image, file)) {
                val fileSize = file.length()
                uploadSizeLeft -= fileSize
                val uploadTime = SystemClock.elapsedRealtime() - startTime
                estimatedRemainingTime =
                    TimeHelper.calculateRemainingTime(fileSize, uploadTime, uploadSizeLeft)

                successImages++
            }
        }

        return@withContext successImages
    }

    /**
     * @return whether the image was successfully uploaded
     */
    @OptIn(ExperimentalUuidApi::class)
    private suspend fun uploadImage(
        image: QueuedImageEntity,
        file: File
    ): Boolean = with(imagesQueueRepository.value) {
        if (!file.isFile) {
            updateImageStatus(
                image.id,
                QueuedImageEntity.Status.Completed(DomainResult.Failure(Unit))
            )
            return@with false
        }

        val finalStatus: DomainResult<String, Unit>
        val success: Boolean

        try {
            updateImageStatus(image.id, QueuedImageEntity.Status.Uploading)

            when (val it = remoteImagesRepository.value.upload(file)) {
                is DomainResult.Failure -> {
                    finalStatus = DomainResult.Failure(Unit)
                    success = false
                }
                is DomainResult.Success -> {
                    finalStatus = DomainResult.Success(it.value)
                    success = true
                }
            }
        } catch (e: CancellationException) {
            // Restore original status if work is being cancelled
            updateImageStatus(image.id, QueuedImageEntity.Status.Ready)
            throw e
        }

        updateImageStatus(image.id, QueuedImageEntity.Status.Completed(finalStatus))

        // Return
        success
    }

    private companion object {

        private fun NotificationCompat.Builder.buildIndeterminateProgress(): Notification =
            setProgress(0, 0, true).build()

        private fun NotificationCompat.Builder.buildWithProgress(
            context: Context,
            currentItem: Int,
            totalCount: Int,
            estimatedRemainingTime: Long?
        ): Notification {
            val progress = ((currentItem.toFloat()) / totalCount) * 100
            val content = buildString {
                append(context.getString(R.string.upload_progress_notification_desc_image_count, currentItem, totalCount))
                estimatedRemainingTime?.let {
                    append("\n")
                    append(
                        context.getString(
                            R.string.upload_progress_notification_desc_estimated_time,
                            TimeFormatters.formatCountdown(context, it)
                        )
                    )
                }
            }

            setProgress(100, progress.roundToInt(), false).setBigContent(content)

            return build()
        }

        const val PROGRESS_NOTIFICATION_CHANNEL_ID = "progress"
        const val PROGRESS_NOTIFICATION_ID = 1
        const val RESULT_NOTIFICATION_CHANNEL_ID = "result"
        const val RESULT_NOTIFICATION_ID = 69
    }
}