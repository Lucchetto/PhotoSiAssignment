package com.photosi.assignment.domain

import android.net.Uri

interface ImageQueueRepository {

    suspend fun addImages(uris: List<Uri>)
}