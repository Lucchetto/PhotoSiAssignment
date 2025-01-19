package com.photosi.assignment.section.upload

import androidx.compose.runtime.Immutable
import com.photosi.assignment.domain.UploadImagesWorkerStatusEntity
import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class UploadImagesScreenState(
    val queue: ImmutableList<QueuedImageEntity>?,
    val workerStatus: UploadImagesWorkerStatusEntity?
) {

    val fabAction: FabAction? = when {
        workerStatus == UploadImagesWorkerStatusEntity.Queued
                || workerStatus is UploadImagesWorkerStatusEntity.Running -> FabAction.CancelUpload
        queue?.any { it.status is QueuedImageEntity.Status.Ready } == true -> FabAction.Upload
        else -> null
    }

    enum class FabAction {
        Upload,
        CancelUpload
    }
}
