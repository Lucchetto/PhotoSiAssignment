package com.photosi.assignment.data.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface CatboxApi {

    @POST("user/api.php")
    suspend fun uploadImage(@Body body: MultipartBody): String
}