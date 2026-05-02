package com.example.bitebook.data.repository

import com.example.bitebook.data.local.AppDatabaseProvider
import com.example.bitebook.data.local.ImageCacheStore
import com.example.bitebook.data.local.LocalCacheStatus
import com.example.bitebook.data.local.LocalRecipeDataSource
import com.example.bitebook.data.local.entity.RecipeEntity
import com.example.bitebook.data.model.Recipe
import com.example.bitebook.data.remote.FirebaseRecipeDataSource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeRepository(
    database: FirebaseDatabase
) {
    private val local = LocalRecipeDataSource(AppDatabaseProvider.database.recipeDao())
    private val imageCache = ImageCacheStore(
        imageCacheDao = AppDatabaseProvider.database.imageCacheDao(),
        cacheDir = AppDatabaseProvider.imageCacheDir
    )
    private val remote = FirebaseRecipeDataSource(database)
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun getAllRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        val cachedRecipes = local.getAllRecipes()

        if (cachedRecipes.isNotEmpty()) {
            syncScope.launch {
                runCatching { refreshFromRemote() }
            }
            return@withContext cachedRecipes
        }

        refreshFromRemote()
    }

    suspend fun addRecipe(recipe: Recipe): Unit = withContext(Dispatchers.IO) {
        val cachedImagePath = runCatching { imageCache.cacheImage(recipe.imageUrl) }.getOrNull()
        local.upsertRecipe(recipe, synced = false, cachedImagePath = cachedImagePath)

        runCatching {
            remote.addRecipe(recipe)
            local.markSynced(recipe.id, true)
        }.getOrThrow()
    }

    suspend fun updateRecipe(recipe: Recipe): Unit = withContext(Dispatchers.IO) {
        val cachedImagePath = runCatching { imageCache.cacheImage(recipe.imageUrl) }.getOrNull()
        local.upsertRecipe(recipe, synced = false, cachedImagePath = cachedImagePath)

        runCatching {
            remote.updateRecipe(recipe)
            local.markSynced(recipe.id, true)
        }.getOrThrow()
    }

    suspend fun deleteRecipe(recipeId: String): Unit = withContext(Dispatchers.IO) {
        local.deleteRecipe(recipeId)
        runCatching { remote.deleteRecipe(recipeId) }.getOrThrow()
    }

    suspend fun checkConnection(): Boolean = withContext(Dispatchers.IO) {
        remote.checkConnection()
    }

    suspend fun getCacheStatus(): LocalCacheStatus = withContext(Dispatchers.IO) {
        LocalCacheStatus(
            cachedRecipes = local.countAll(),
            unsyncedRecipes = local.countUnsynced(),
            cachedImages = AppDatabaseProvider.database.imageCacheDao().countAll(),
            cacheDirectory = AppDatabaseProvider.imageCacheDir.absolutePath
        )
    }

    private suspend fun refreshFromRemote(): List<Recipe> = withContext(Dispatchers.IO) {
        syncPendingRecipes()

        val remoteRecipes = remote.getAllRecipes()
        val localEntities = remoteRecipes.map { recipe ->
            val cachedImagePath = runCatching { imageCache.cacheImage(recipe.imageUrl) }.getOrNull()
            RecipeEntity.fromModel(recipe, synced = true, cachedImagePath = cachedImagePath)
        }

        local.replaceAll(localEntities)
        local.getAllRecipes()
    }

    private suspend fun syncPendingRecipes() {
        local.getUnsyncedRecipes().forEach { entity ->
            val recipe = entity.toModel()
            remote.addRecipe(recipe)
            local.markSynced(recipe.id, true)
        }
    }
}
