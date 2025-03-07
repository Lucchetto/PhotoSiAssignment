package com.photosi.assignment.data.api

import com.photosi.assignment.data.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

internal val ApiModule = module {
    single {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-api-key", BuildConfig.PHOTOFORSE_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.PHOTOFORSE_BASE_URL)
            .client(httpClient)
            .addConverterFactory(
                Json.asConverterFactory(
                    requireNotNull(MediaType.parse("application/json; charset=utf-8")))
            )
            .build()
            .create(PhotoforseApi::class.java)
    }
    single {
        val httpClient = OkHttpClient.Builder()
            // Relax IO timeouts
            .writeTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.CATBOX_BASE_URL)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(CatboxApi::class.java)
    }
}