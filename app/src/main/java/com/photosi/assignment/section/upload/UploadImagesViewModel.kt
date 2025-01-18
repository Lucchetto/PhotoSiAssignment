package com.photosi.assignment.section.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photosi.assignment.domain.ImageQueueRepository
import kotlinx.coroutines.launch

class UploadImagesViewModel(
    private val imageQueueRepository: ImageQueueRepository
): ViewModel() {

    fun addImages(uris: List<Uri>) {
        viewModelScope.launch {
            imageQueueRepository.addImages(uris)
        }
    }
}