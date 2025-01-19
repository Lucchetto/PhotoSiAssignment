package com.photosi.assignment.data.mapper

import androidx.work.WorkInfo
import com.photosi.assignment.domain.UploadImagesWorkerStatusEntity
import com.photosi.assignment.domain.entity.Result

internal object UploadImagesWorkerStatusEntityMapper {

    fun mapTo(workInfo: WorkInfo): UploadImagesWorkerStatusEntity? = when (workInfo.state) {
        WorkInfo.State.BLOCKED,
        WorkInfo.State.ENQUEUED -> UploadImagesWorkerStatusEntity.Queued
        WorkInfo.State.RUNNING -> UploadImagesWorkerStatusEntityRunningMapper.mapTo(
            workInfo.progress
        ) ?: UploadImagesWorkerStatusEntity.Queued
        WorkInfo.State.SUCCEEDED -> UploadImagesWorkerStatusEntity.Completed(Result.Success(Unit))
        WorkInfo.State.FAILED -> UploadImagesWorkerStatusEntity.Completed(Result.Failure(Unit))
        WorkInfo.State.CANCELLED -> null
    }
}