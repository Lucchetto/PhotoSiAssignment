package com.photosi.assignment.data.mapper

import com.photosi.assignment.db.QueuedImage
import com.photosi.assignment.db.QueuedImageStatus
import com.photosi.assignment.domain.entity.QueuedImageEntity
import com.photosi.assignment.domain.entity.Result
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal object QueuedImageMapper {

    @OptIn(ExperimentalUuidApi::class)
    fun mapFrom(from: QueuedImage) = QueuedImageEntity(
        id = Uuid.fromByteArray(from.id),
        fileName = from.fileName,
        status = when (from.status) {
            QueuedImageStatus.Ready -> QueuedImageEntity.Status.Ready
            QueuedImageStatus.Uploading -> QueuedImageEntity.Status.Uploading
            QueuedImageStatus.Success -> QueuedImageEntity.Status.Completed(
                Result.Success(
                    requireNotNull(from.resultUrl) {
                        "Invalid success QueueImage record, no resultUrl found!"
                    }
                )
            )

            QueuedImageStatus.Failure -> QueuedImageEntity.Status.Completed(
                Result.Failure(Unit)
            )
        }
    )
}