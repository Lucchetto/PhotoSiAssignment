package com.photosi.assignment.navigation

import androidx.lifecycle.ViewModel
import com.photosi.assignment.domain.UploadImagesWorkerRepository
import com.photosi.assignment.domain.UploadImagesWorkerStatusEntity
import kotlinx.coroutines.flow.first

class AppNavHostViewModel(
    private val uploadImagesWorkerRepository: UploadImagesWorkerRepository
): ViewModel() {

    suspend fun skipCountrySelection(): Boolean {
        return uploadImagesWorkerRepository.workerStatusFlow.first() is UploadImagesWorkerStatusEntity.Running
    }
}