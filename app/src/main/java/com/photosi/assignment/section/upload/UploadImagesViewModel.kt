package com.photosi.assignment.section.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photosi.assignment.domain.ImageQueueRepository
import com.photosi.assignment.domain.entity.QueuedImageEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UploadImagesViewModel(
    private val imageQueueRepository: ImageQueueRepository
): ViewModel() {

    val uiState: StateFlow<UploadImagesScreenState> get() = imageQueueRepository.queuedImagesFlow.map {
        UploadImagesScreenState(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UploadImagesScreenState(null))

    fun addImages(uris: List<Uri>) {
        viewModelScope.launch {
            imageQueueRepository.addImages(uris)
        }
    }

    fun getFileForQueuedImage(entity: QueuedImageEntity) =
        imageQueueRepository.getFileForQueuedImage(entity)
}