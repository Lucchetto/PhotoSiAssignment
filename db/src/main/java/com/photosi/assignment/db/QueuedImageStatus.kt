package com.photosi.assignment.db

import app.cash.sqldelight.ColumnAdapter

enum class QueuedImageStatus(internal val id: Long) {
    Ready(0),
    Uploading(1),
    Success(2),
    Failure(3);

    internal companion object {

        val ColumnAdapter = object : ColumnAdapter<QueuedImageStatus, Long> {
            override fun decode(databaseValue: Long) =
                QueuedImageStatus.entries.first { it.id == databaseValue }

            override fun encode(value: QueuedImageStatus) = value.id
        }
    }
}