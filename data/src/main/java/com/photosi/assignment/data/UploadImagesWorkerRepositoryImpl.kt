package com.photosi.assignment.data

import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.photosi.assignment.data.mapper.UploadImagesWorkerStatusEntityMapper
import com.photosi.assignment.data.util.IntentHelper
import com.photosi.assignment.data.worker.UploadImagesWorker
import com.photosi.assignment.domain.UploadImagesWorkerRepository
import com.photosi.assignment.domain.UploadImagesWorkerStatusEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

internal class UploadImagesWorkerRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val workManager: WorkManager
): UploadImagesWorkerRepository {

    private val completedWorkerIdPrefKey = InternalPreferenceKeys.COMPLETED_WORKER_ID

    @OptIn(ExperimentalUuidApi::class)
    override val workerStatusFlow: Flow<UploadImagesWorkerStatusEntity?>
        get() = combine(
            workManager.getWorkInfosForUniqueWorkFlow(UPLOAD_IMAGES_WORKER_NAME),
            dataStore.data.map { it[completedWorkerIdPrefKey] }
        ) { infos, completedWorkerId ->
            infos.firstOrNull()?.let { workInfo ->
                val mapped = workInfo.let(UploadImagesWorkerStatusEntityMapper::mapTo)

                mapped.takeIf {
                    it !is UploadImagesWorkerStatusEntity.Completed
                            // We should expose completed worker status only if it's being tracked
                            || workInfo.id.toKotlinUuid().toByteArray().contentEquals(completedWorkerId)
                }
            }
        }

    override suspend fun start() {
        val request = OneTimeWorkRequestBuilder<UploadImagesWorker>()
            .build()

        workManager
            .enqueueUniqueWork(UPLOAD_IMAGES_WORKER_NAME, ExistingWorkPolicy.KEEP, request)
            .await()
    }

    override fun cancel() {
        workManager.cancelUniqueWork(UPLOAD_IMAGES_WORKER_NAME)
    }

    override suspend fun clearCompletedWorkerStatus() {
        dataStore.edit { it.remove(completedWorkerIdPrefKey) }
    }

    override fun isFromWorkerCompletedIntent(extras: Bundle) =
        IntentHelper.isWorkerCompletedMarker(extras)

    private companion object {

        const val UPLOAD_IMAGES_WORKER_NAME = "upload_images_worker"
    }
}