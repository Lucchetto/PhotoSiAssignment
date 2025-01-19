package com.photosi.assignment.data

import com.photosi.assignment.data.api.ApiModule
import com.photosi.assignment.data.worker.WorkerModule
import com.photosi.assignment.db.AppDatabaseModule
import com.photosi.assignment.domain.CountriesRepository
import com.photosi.assignment.domain.ImageQueueRepository
import com.photosi.assignment.domain.RemoteImagesRepository
import com.photosi.assignment.domain.UploadImagesWorkerRepository
import org.koin.dsl.module

val DataModule = module {
    includes(ApiModule, AppDatabaseModule, WorkerModule)

    single<CountriesRepository> { CountriesRepositoryImpl(get()) }
    single<RemoteImagesRepository> { RemoteImagesRepositoryImpl(get()) }
    single<ImageQueueRepository> { ImageQueueRepositoryImpl(get(), get()) }
    single<UploadImagesWorkerRepository> { UploadImagesWorkerRepositoryImpl(get()) }
}