package com.photosi.assignment.data

import com.photosi.assignment.data.api.ApiModule
import com.photosi.assignment.db.AppDatabaseModule
import com.photosi.assignment.domain.CountriesRepository
import com.photosi.assignment.domain.ImageQueueRepository
import com.photosi.assignment.domain.PicturesRepository
import org.koin.dsl.module

val DataModule = module {
    includes(ApiModule, AppDatabaseModule)

    single<CountriesRepository> { CountriesRepositoryImpl(get()) }
    single<PicturesRepository> { PicturesRepositoryImpl(get()) }
    single<ImageQueueRepository> { ImageQueueRepositoryImpl(get(), get()) }
}