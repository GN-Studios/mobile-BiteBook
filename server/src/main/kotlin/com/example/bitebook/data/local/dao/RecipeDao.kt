package com.example.bitebook.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.bitebook.data.local.entity.RecipeEntity

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    suspend fun getAll(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE synced = 0 ORDER BY updatedAt ASC")
    suspend fun getUnsynced(): List<RecipeEntity>

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM recipes WHERE synced = 0")
    suspend fun countUnsynced(): Int

    @Upsert
    suspend fun upsert(recipe: RecipeEntity)

    @Upsert
    suspend fun upsertAll(recipes: List<RecipeEntity>)

    @Query("UPDATE recipes SET synced = :synced, updatedAt = :updatedAt WHERE id = :recipeId")
    suspend fun updateSyncState(recipeId: String, synced: Boolean, updatedAt: Long)

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteById(recipeId: String)

    @Query("DELETE FROM recipes")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(recipes: List<RecipeEntity>) {
        clearAll()
        upsertAll(recipes)
    }
}
