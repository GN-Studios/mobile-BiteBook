package com.example.bitebook.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.bitebook.data.local.entity.ImageCacheEntity

@Dao
interface ImageCacheDao {
    @Query("SELECT * FROM image_cache WHERE remoteUrl = :remoteUrl LIMIT 1")
    suspend fun getByUrl(remoteUrl: String): ImageCacheEntity?

    @Query("SELECT COUNT(*) FROM image_cache")
    suspend fun countAll(): Int

    @Upsert
    suspend fun upsert(entry: ImageCacheEntity)
}
