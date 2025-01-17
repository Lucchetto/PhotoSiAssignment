package com.photosi.assignment

import android.app.Application
import com.photosi.assignment.data.DataModule
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
            modules(DataModule)
        }
    }
}