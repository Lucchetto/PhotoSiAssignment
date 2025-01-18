package com.photosi.assignment.section.upload

import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlinx.collections.immutable.ImmutableList

@JvmInline
value class UploadImagesScreenState(
    val queue: ImmutableList<QueuedImageEntity>?
)
