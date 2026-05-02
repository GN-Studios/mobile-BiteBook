package com.example.bitebook.data.local

import kotlinx.serialization.Serializable

@Serializable
data class LocalCacheStatus(
    val cachedRecipes: Int,
    val unsyncedRecipes: Int,
    val cachedImages: Int,
    val cacheDirectory: String
)
