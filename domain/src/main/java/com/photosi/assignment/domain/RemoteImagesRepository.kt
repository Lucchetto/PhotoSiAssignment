package com.photosi.assignment.domain

import com.photosi.assignment.domain.entity.RepoApiResult
import java.io.File

interface RemoteImagesRepository {

    suspend fun upload(file: File, customFileName: String? = null): RepoApiResult<String, Nothing>
}