package com.example.bitebook.firebase

import com.example.bitebook.config.Env
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import java.io.FileInputStream

object FirebaseInitializer {
    fun initialize(): FirebaseDatabase {
        if (FirebaseApp.getApps().isEmpty()) {
            val credentialsPath = Env.filePath("GOOGLE_APPLICATION_CREDENTIALS")
                ?: error("Set GOOGLE_APPLICATION_CREDENTIALS in .env or your shell environment")
            val projectId = Env.getRequired("FIREBASE_PROJECT_ID")
            val databaseUrl = Env.getRequired("FIREBASE_DATABASE_URL")
            val credentials = FileInputStream(credentialsPath).use { input ->
                GoogleCredentials.fromStream(input)
            }

            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .setDatabaseUrl(databaseUrl)
                .build()

            FirebaseApp.initializeApp(options)
        }

        return FirebaseDatabase.getInstance()
    }
}
