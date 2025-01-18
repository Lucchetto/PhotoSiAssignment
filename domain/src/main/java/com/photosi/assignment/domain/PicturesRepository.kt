package com.photosi.assignment.domain

import com.photosi.assignment.domain.entity.RepoApiResult
import java.io.File

interface PicturesRepository {

    suspend fun uploadPicture(file: File): RepoApiResult<String, Nothing>
}