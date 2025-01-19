package com.photosi.assignment.domain

import android.net.Uri
import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ImageQueueRepository {

    val queuedImagesFlow: Flow<ImmutableList<QueuedImageEntity>>

    suspend fun addImages(uris: List<Uri>)

    fun getFileForQueuedImage(entity: QueuedImageEntity): File

    suspend fun listReadyImages(): List<QueuedImageEntity>

    @OptIn(ExperimentalUuidApi::class)
    fun updateImageStatus(id: Uuid, status: QueuedImageEntity.Status)

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteImageById(id: Uuid)
}