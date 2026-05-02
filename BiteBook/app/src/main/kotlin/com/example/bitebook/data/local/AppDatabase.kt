package com.example.bitebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bitebook.data.local.dao.ImageCacheDao
import com.example.bitebook.data.local.dao.RecipeDao
import com.example.bitebook.data.local.entity.ImageCacheEntity
import com.example.bitebook.data.local.entity.RecipeEntity

@Database(
    entities = [RecipeEntity::class, ImageCacheEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun imageCacheDao(): ImageCacheDao
}
