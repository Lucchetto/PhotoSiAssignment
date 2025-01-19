package com.photosi.assignment.domain

import com.photosi.assignment.domain.entity.Result

sealed class UploadImagesWorkerStatusEntity {

    data object Queued : UploadImagesWorkerStatusEntity()

    data class Running(
        val currentItem: Int,
        val totalCount: Int,
        val estimatedRemainingTime: Long?
    ) : UploadImagesWorkerStatusEntity()

    data class Completed(val result: Result<Unit, Unit>) : UploadImagesWorkerStatusEntity()
}