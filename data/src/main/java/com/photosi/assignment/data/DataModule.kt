package com.photosi.assignment.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.photosi.assignment.data.api.ApiModule
import com.photosi.assignment.data.worker.WorkerModule
import com.photosi.assignment.db.AppDatabaseModule
import com.photosi.assignment.domain.CountriesRepository
import com.photosi.assignment.domain.ImageQueueRepository
import com.photosi.assignment.domain.RemoteImagesRepository
import com.photosi.assignment.domain.UploadImagesWorkerRepository
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val DataModule = module {
    includes(ApiModule, AppDatabaseModule, WorkerModule)

    single<DataStore<Preferences>> {
        preferencesDataStore("settings")
        get<Application>().dataStore
    }
    single<CountriesRepository> { CountriesRepositoryImpl(get()) }
    single<RemoteImagesRepository> { RemoteImagesRepositoryImpl(get()) }
    single<ImageQueueRepository> { ImageQueueRepositoryImpl(get(), get()) }
    single<UploadImagesWorkerRepository> { UploadImagesWorkerRepositoryImpl(get(), get()) }
}