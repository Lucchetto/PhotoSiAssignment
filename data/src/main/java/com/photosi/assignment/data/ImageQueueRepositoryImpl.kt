package com.photosi.assignment.data

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.photosi.assignment.data.mapper.QueuedImageEntityStatusMapper
import com.photosi.assignment.data.mapper.QueuedImageMapper
import com.photosi.assignment.db.QueuedImage
import com.photosi.assignment.db.QueuedImageStatus
import com.photosi.assignment.db.db.AppDatabase
import com.photosi.assignment.domain.ImageQueueRepository
import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class ImageQueueRepositoryImpl(
    private val application: Application,
    private val appDatabase: AppDatabase
): ImageQueueRepository {

    private val imagesDir = application.createImagesDir()

    override val queuedImagesFlow: Flow<ImmutableList<QueuedImageEntity>>
        get() = appDatabase.queuedImageQueries.selectAll().asFlow().mapToList(Dispatchers.IO).map {
            it.map(QueuedImageMapper::mapFrom).toImmutableList()
        }

    override suspend fun addImages(uris: List<Uri>) = withContext(Dispatchers.IO) {
        uris.forEach {
            val fileName = DocumentFile.fromSingleUri(application, it)?.name ?: return@forEach
            val id = Uuid.random()

            if (!copyImageFile(id, it)) return@forEach
            addToQueuedImages(id, fileName)
        }
    }

    private fun addToQueuedImages(id: Uuid, fileName: String) {
        val queuedImage = QueuedImage(id.toByteArray(), fileName, QueuedImageStatus.Ready, null)

        appDatabase.queuedImageQueries.insert(queuedImage)
    }

    /**
     * Copy image from given [Uri] to [imagesDir].
     */
    private fun copyImageFile(id: Uuid, uri: Uri): Boolean {
        val imageFile = imagesDir.resolve(id.toString())
        return application.contentResolver.openInputStream(uri)?.use { iStream ->
            imageFile.outputStream().use(iStream::copyTo)

            true
        } ?: false
    }

    override fun getFileForQueuedImage(entity: QueuedImageEntity): File =
        imagesDir.resolve(entity.id.toString())

    override suspend fun listReadyImages() = appDatabase.queuedImageQueries
        .selectAllReady()
        .executeAsList()
        .map(QueuedImageMapper::mapFrom)

    override fun updateImageStatus(id: Uuid, status: QueuedImageEntity.Status) {
        val (dbStatus, resultUrl) = QueuedImageEntityStatusMapper.mapFrom(status)

        appDatabase.queuedImageQueries.updateStatus(dbStatus, resultUrl, id.toByteArray())
    }

    override suspend fun deleteImageById(id: Uuid) = withContext(Dispatchers.IO) {
        appDatabase.queuedImageQueries.deleteById(id.toByteArray())
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