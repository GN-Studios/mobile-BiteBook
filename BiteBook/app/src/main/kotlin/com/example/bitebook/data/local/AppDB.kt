package com.example.bitebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bitebook.data.model.User
import com.example.bitebook.data.model.Recipe

@Database(
    entities = [User::class, Recipe::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
}