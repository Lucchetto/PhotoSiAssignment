package com.photosi.assignment

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import com.photosi.assignment.data.DataModule
import com.photosi.assignment.navigation.AppNavHostModule
import com.photosi.assignment.section.countries.SelectCountriesScreenModule
import com.photosi.assignment.section.upload.UploadImagesScreenModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class PhotosiApplication: Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@PhotosiApplication)
            // setup a WorkManager instance
            workManagerFactory()
            // Load modules
            modules(DataModule, SelectCountriesScreenModule, UploadImagesScreenModule, AppNavHostModule)
        }
    }

    override fun newImageLoader(context: Context) = ImageLoader.Builder(context)
        .components { SvgDecoder.Factory() }
        .logger(
            if (BuildConfig.DEBUG) DebugLogger() else null
        )
        .build()
}