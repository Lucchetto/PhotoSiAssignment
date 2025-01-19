package com.photosi.assignment.domain

interface UploadImagesWorkerRepository {

    suspend fun start()
}