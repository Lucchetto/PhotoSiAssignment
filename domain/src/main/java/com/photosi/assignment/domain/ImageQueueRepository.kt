package com.photosi.assignment.domain

import android.net.Uri
import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ImageQueueRepository {

    val queuedImagesFlow: Flow<List<QueuedImageEntity>>

    suspend fun addImages(uris: List<Uri>): List<QueuedImageEntity>

    fun getFileForQueuedImage(entity: QueuedImageEntity): File
}