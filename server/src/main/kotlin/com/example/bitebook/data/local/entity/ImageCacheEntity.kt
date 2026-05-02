package com.example.bitebook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_cache")
data class ImageCacheEntity(
    @PrimaryKey val remoteUrl: String,
    val localPath: String,
    val lastFetchedAt: Long
)
