package com.photosi.assignment.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class QueuedImageEntity @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val fileName: String,
) {

    sealed class State {

        data object Ready : State()

        data object Uploading : State()

        data class Completed(val result: Result<String, Unit>) : State()
    }
}
