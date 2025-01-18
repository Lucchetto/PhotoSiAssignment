package com.photosi.assignment.data.mapper

import com.photosi.assignment.db.QueuedImage
import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal object QueuedImageMapper {

    @OptIn(ExperimentalUuidApi::class)
    fun mapFrom(from: QueuedImage) = QueuedImageEntity(
        Uuid.fromByteArray(from.id),
        from.fileName
    )
}