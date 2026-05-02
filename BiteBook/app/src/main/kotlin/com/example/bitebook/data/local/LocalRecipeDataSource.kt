package com.example.bitebook.data.local

import com.example.bitebook.data.local.dao.RecipeDao
import com.example.bitebook.data.local.entity.RecipeEntity
import com.example.bitebook.data.model.Recipe

class LocalRecipeDataSource(
    private val recipeDao: RecipeDao
) {
    suspend fun getAllRecipes(): List<Recipe> =
        recipeDao.getAll().map { it.toModel() }

    suspend fun getUnsyncedRecipes(): List<RecipeEntity> =
        recipeDao.getUnsynced()

    suspend fun countAll(): Int =
        recipeDao.countAll()

    suspend fun countUnsynced(): Int =
        recipeDao.countUnsynced()

    suspend fun upsertRecipe(recipe: Recipe, synced: Boolean, cachedImagePath: String?) {
        recipeDao.upsert(RecipeEntity.fromModel(recipe, synced, cachedImagePath))
    }

    suspend fun replaceAll(recipes: List<RecipeEntity>) {
        recipeDao.replaceAll(recipes)
    }

    suspend fun markSynced(recipeId: String, synced: Boolean) {
        recipeDao.updateSyncState(recipeId, synced, System.currentTimeMillis())
    }

    suspend fun deleteRecipe(recipeId: String) {
        recipeDao.deleteById(recipeId)
    }
}
