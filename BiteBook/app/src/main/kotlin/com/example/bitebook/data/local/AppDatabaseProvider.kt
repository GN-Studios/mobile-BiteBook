package com.example.bitebook.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File
import kotlinx.coroutines.Dispatchers

object AppDatabaseProvider {
    private val appDir: File by lazy {
        File(System.getProperty("user.dir"), ".bitebook").apply { mkdirs() }
    }

    val imageCacheDir: File by lazy {
        File(appDir, "image-cache").apply { mkdirs() }
    }

    val database: AppDatabase by lazy {
        val dbFile = File(appDir, "bitebook-cache.db")

        Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
            factory = { AppDatabase_Impl() }
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
