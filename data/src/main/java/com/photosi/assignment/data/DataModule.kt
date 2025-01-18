package com.photosi.assignment.data

import com.photosi.assignment.data.api.ApiModule
import com.photosi.assignment.domain.CountriesRepository
import com.photosi.assignment.domain.PicturesRepository
import org.koin.dsl.module

val DataModule = module {
    includes(ApiModule)

    single<CountriesRepository> { CountriesRepositoryImpl(get()) }
    single<PicturesRepository> { PicturesRepositoryImpl(get()) }
}