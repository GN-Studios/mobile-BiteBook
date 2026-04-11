package com.example.bitebook.data.local

import androidx.room.*
import com.example.bitebook.data.model.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAll(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE userId = :userId")
    fun getByUser(userId: String): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recipe: Recipe)

    @Update
    fun update(user: Recipe)

    @Delete
    fun delete(recipe: Recipe)
}