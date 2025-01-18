package com.photosi.assignment.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class QueuedImageEntity @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val fileName: String,
)
