package com.photosi.assignment.data

import android.app.Application
import android.content.Context
import android.net.Uri
import com.photosi.assignment.domain.ImageQueueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import java.io.File
import java.util.UUID

internal class ImageQueueRepositoryImpl(
    private val application: Application
): ImageQueueRepository {

    private val imagesDir = application.createImagesDir()

    override suspend fun addImages(uris: List<Uri>) = withContext(Dispatchers.IO) {
        uris.forEach {
            val id = UUID.randomUUID().toString()
            val imageFile = imagesDir.resolve(id)
            val copySuccess = application.contentResolver.openInputStream(it)?.use { iStream ->
                imageFile.outputStream().use(iStream::copyTo)

                true
            } ?: false
        }
    }

    @VisibleForTesting
    companion object {

        private const val IMAGE_QUEUE_DIR_NAME = "image_queue"

        fun Context.createImagesDir(): File {
            val dir = noBackupFilesDir.resolve(IMAGE_QUEUE_DIR_NAME)

            if (dir.isFile) {
                dir.delete()
            }
            if (!dir.exists()) {
                dir.mkdirs()
            }

            return dir
        }
    }
}