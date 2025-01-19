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

    val canUpload: Boolean = queue?.any { it.status == QueuedImageEntity.Status.Ready } == true
}
