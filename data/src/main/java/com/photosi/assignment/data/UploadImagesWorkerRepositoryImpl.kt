package com.photosi.assignment.data

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.photosi.assignment.data.mapper.UploadImagesWorkerStatusEntityMapper
import com.photosi.assignment.data.worker.UploadImagesWorker
import com.photosi.assignment.domain.UploadImagesWorkerRepository
import com.photosi.assignment.domain.UploadImagesWorkerStatusEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class UploadImagesWorkerRepositoryImpl(
    private val workManager: WorkManager
): UploadImagesWorkerRepository {

    override val workerStatusFlow: Flow<UploadImagesWorkerStatusEntity?>
        get() = workManager.getWorkInfosForUniqueWorkFlow(UPLOAD_IMAGES_WORKER_NAME).map { infos ->
            infos.firstOrNull()?.let(UploadImagesWorkerStatusEntityMapper::mapTo)
        }

    override suspend fun start() {
        val request = OneTimeWorkRequestBuilder<UploadImagesWorker>()
            .build()

        workManager
            .enqueueUniqueWork(UPLOAD_IMAGES_WORKER_NAME, ExistingWorkPolicy.KEEP, request)
            .await()
    }

    private companion object {

        const val UPLOAD_IMAGES_WORKER_NAME = "upload_images_worker"
    }
}