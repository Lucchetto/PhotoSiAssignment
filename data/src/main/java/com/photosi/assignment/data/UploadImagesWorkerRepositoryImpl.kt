package com.photosi.assignment.data

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.photosi.assignment.data.worker.UploadImagesWorker
import com.photosi.assignment.domain.UploadImagesWorkerRepository

internal class UploadImagesWorkerRepositoryImpl(
    private val workManager: WorkManager
): UploadImagesWorkerRepository {

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