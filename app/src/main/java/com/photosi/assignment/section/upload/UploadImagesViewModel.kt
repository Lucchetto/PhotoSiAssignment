package com.photosi.assignment.section.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photosi.assignment.domain.ImageQueueRepository
import com.photosi.assignment.domain.UploadImagesWorkerRepository
import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class UploadImagesViewModel(
    private val imageQueueRepository: ImageQueueRepository,
    private val uploadImagesWorkerRepository: UploadImagesWorkerRepository,
): ViewModel() {

    val uiState: StateFlow<UploadImagesScreenState> get() = combine(
        imageQueueRepository.queuedImagesFlow,
        uploadImagesWorkerRepository.workerStatusFlow
    ) { queue, workerStatus ->
        UploadImagesScreenState(queue, workerStatus)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        UploadImagesScreenState(null, null)
    )

    fun addImages(uris: List<Uri>) {
        viewModelScope.launch {
            imageQueueRepository.addImages(uris)
        }
    }

    fun handleFabClick(action: UploadImagesScreenState.FabAction) {
        when (action) {
            UploadImagesScreenState.FabAction.Upload -> viewModelScope.launch {
                uploadImagesWorkerRepository.start()
            }
            UploadImagesScreenState.FabAction.CancelUpload -> uploadImagesWorkerRepository.cancel()
        }
    }

    fun getFileForQueuedImage(entity: QueuedImageEntity) =
        imageQueueRepository.getFileForQueuedImage(entity)

    @OptIn(ExperimentalUuidApi::class)
    fun deleteImage(entity: QueuedImageEntity) {
        viewModelScope.launch {
            imageQueueRepository.deleteImageById(entity.id)
        }
    }
}