package com.photosi.assignment.data.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.photosi.assignment.data.R
import com.photosi.assignment.domain.ImageQueueRepository
import com.photosi.assignment.domain.RemoteImagesRepository
import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi
import com.photosi.assignment.domain.entity.Result as DomainResult

internal class UploadImagesWorker(
    appContext: Context,
    params: WorkerParameters,
    private val imagesQueueRepository: Lazy<ImageQueueRepository>,
    private val remoteImagesRepository: Lazy<RemoteImagesRepository>
) : CoroutineWorker(appContext, params) {

    private val notificationManager = NotificationManagerCompat.from(appContext)
    private val progressNotificationBuilder = NotificationCompat.Builder(appContext, PROGRESS_NOTIFICATION_CHANNEL_ID)
        .setOngoing(true)
        .setSmallIcon(R.drawable.outline_cloud_upload_24)
        .setContentTitle(appContext.getString(R.string.upload_progress_notification_title))
        .setTicker(appContext.getString(R.string.upload_progress_notification_title))

    override suspend fun doWork(): Result {
        // Prevent upload from being restarted on failure
        if (runAttemptCount > 1) {
            return Result.failure()
        }

        createProgressNotificationChannel()
        markAsForeground()
        uploadImages()

        return Result.success()
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

    private suspend fun uploadImages() = withContext(Dispatchers.IO) {
        val readyImages = imagesQueueRepository.value.listReadyImages()

        readyImages.forEachIndexed { index, image ->
            notificationManager.notify(
                PROGRESS_NOTIFICATION_ID,
                progressNotificationBuilder.buildWithProgress(index, readyImages.size)
            )
            uploadImage(image)
        }
    }

    /**
     * @return whether the image was successfully uploaded
     */
    @OptIn(ExperimentalUuidApi::class)
    private suspend fun uploadImage(entity: QueuedImageEntity): Unit = with(imagesQueueRepository.value) {
        val file = getFileForQueuedImage(entity)
        if (!file.isFile) updateImageStatus(
            entity.id,
            QueuedImageEntity.Status.Completed(DomainResult.Failure(Unit))
        )

        updateImageStatus(entity.id, QueuedImageEntity.Status.Uploading)

        val finalStatus = when (val it = remoteImagesRepository.value.upload(file)) {
            is com.photosi.assignment.domain.entity.Result.Failure ->
                QueuedImageEntity.Status.Completed(DomainResult.Failure(Unit))
            is com.photosi.assignment.domain.entity.Result.Success ->
                QueuedImageEntity.Status.Completed(DomainResult.Success(it.value))
        }

        updateImageStatus(entity.id, finalStatus)
    }

    private companion object {

        private fun NotificationCompat.Builder.buildIndeterminateProgress(): Notification =
            setProgress(0, 0, true).build()

        private fun NotificationCompat.Builder.buildWithProgress(
            currentItem: Int,
            totalCount: Int
        ): Notification {
            val progress = ((currentItem.toFloat()) / totalCount) * 100

            setProgress(100, progress.roundToInt(), false)

            return build()
        }

        const val PROGRESS_NOTIFICATION_CHANNEL_ID = "progress"
        const val PROGRESS_NOTIFICATION_ID = 1
    }
}