package com.photosi.assignment.section.upload

import com.photosi.assignment.domain.entity.QueuedImageEntity

@JvmInline
value class UploadImagesScreenState(
    val queue: List<QueuedImageEntity>?
)
