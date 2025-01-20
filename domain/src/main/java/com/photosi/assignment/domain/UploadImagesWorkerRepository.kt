package com.photosi.assignment.domain

import kotlinx.coroutines.flow.Flow

interface UploadImagesWorkerRepository {

    val workerStatusFlow: Flow<UploadImagesWorkerStatusEntity?>

    suspend fun start()

    fun cancel()

    suspend fun clearCompletedWorkerStatus()
}