package com.photosi.assignment.data.worker

import androidx.work.WorkManager
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

internal val WorkerModule = module {
    single<WorkManager> { WorkManager.getInstance(get()) }

    worker { UploadImagesWorker(get(), get(), get(), get(), inject(), inject()) }
}