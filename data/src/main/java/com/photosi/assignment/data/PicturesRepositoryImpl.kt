package com.photosi.assignment.data

import com.photosi.assignment.data.api.CatboxApi
import com.photosi.assignment.data.mapper.StubCustomErrorMapper
import com.photosi.assignment.data.util.RepositoryHelper.handleApiCall
import com.photosi.assignment.domain.PicturesRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

internal class PicturesRepositoryImpl(
    private val catboxApi: CatboxApi
): PicturesRepository {

    override suspend fun uploadPicture(file: File) = handleApiCall(StubCustomErrorMapper) {
        /**
         * See documentation at [https://catbox.moe/tools.php](https://catbox.moe/tools.php)
         */
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("reqtype", "fileupload")
            .addFormDataPart(
                "fileToUpload",
                file.name,
                RequestBody.create(null, file)
            )
            .build()

        catboxApi.uploadImage(body)
    }
}