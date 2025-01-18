package com.photosi.assignment.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class QueuedImageEntity @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val fileName: String,
    val status: Status
) {

    sealed class Status {

        data object Ready : Status()

        data object Uploading : Status()

        data class Completed(val result: Result<String, Unit>) : Status()
    }
}
