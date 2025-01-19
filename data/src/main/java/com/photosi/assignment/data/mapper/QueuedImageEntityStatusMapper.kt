package com.photosi.assignment.data.mapper

import com.photosi.assignment.db.QueuedImageStatus
import com.photosi.assignment.domain.entity.QueuedImageEntity
import com.photosi.assignment.domain.entity.Result

internal object QueuedImageEntityStatusMapper {

    fun mapFrom(status: QueuedImageEntity.Status): Pair<QueuedImageStatus, String?> = when (status) {
        is QueuedImageEntity.Status.Completed -> when (val it = status.result) {
            is Result.Failure -> QueuedImageStatus.Failure to null
            is Result.Success -> QueuedImageStatus.Success to it.value
        }
        QueuedImageEntity.Status.Ready -> QueuedImageStatus.Ready to null
        QueuedImageEntity.Status.Uploading -> QueuedImageStatus.Uploading to null
    }
}