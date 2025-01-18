package com.photosi.assignment

import android.app.Application
import com.photosi.assignment.data.DataModule
import com.photosi.assignment.section.countries.SelectCountriesScreenModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PhotosiApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@PhotosiApplication)
            // Load modules
            modules(DataModule, SelectCountriesScreenModule)
        }
    }
}