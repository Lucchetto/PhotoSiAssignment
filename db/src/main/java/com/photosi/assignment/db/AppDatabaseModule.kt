package com.photosi.assignment.db

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.photosi.assignment.db.db.AppDatabase
import org.koin.dsl.module

val AppDatabaseModule = module {
    factory<AppDatabase> {
        AppDatabase(
            AndroidSqliteDriver(AppDatabase.Schema, get(), "app.db", useNoBackupDirectory = true),
            QueuedImage.Adapter(QueuedImageStatus.ColumnAdapter)
        )
    }
}