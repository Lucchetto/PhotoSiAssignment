package com.photosi.assignment.navigation

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.photosi.assignment.domain.UploadImagesWorkerRepository
import com.photosi.assignment.domain.UploadImagesWorkerStatusEntity
import kotlinx.coroutines.flow.first

class AppNavHostViewModel(
    private val uploadImagesWorkerRepository: UploadImagesWorkerRepository
): ViewModel() {

    suspend fun skipCountrySelection(intent: Intent? = null): Boolean {
        val workerStatus = uploadImagesWorkerRepository.workerStatusFlow.first()

        return if (
            intent?.extras?.let(uploadImagesWorkerRepository::isFromWorkerCompletedIntent) == true
        ) {
            workerStatus is UploadImagesWorkerStatusEntity.Completed
        } else {
            workerStatus is UploadImagesWorkerStatusEntity.Running
        }
    }
}