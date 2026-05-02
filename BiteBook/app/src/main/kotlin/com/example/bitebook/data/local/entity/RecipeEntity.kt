package com.example.bitebook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bitebook.data.model.Recipe

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val prepTime: Int,
    val cookTime: Int,
    val servings: Int,
    val userId: String,
    val imageUrl: String,
    val createdAt: Long,
    val cachedImagePath: String?,
    val synced: Boolean,
    val updatedAt: Long
) {
    fun toModel(): Recipe = Recipe(
        id = id,
        title = title,
        description = description,
        prepTime = prepTime,
        cookTime = cookTime,
        servings = servings,
        userId = userId,
        imageUrl = imageUrl,
        createdAt = createdAt
    )

    companion object {
        fun fromModel(
            recipe: Recipe,
            synced: Boolean,
            cachedImagePath: String?,
            updatedAt: Long = System.currentTimeMillis()
        ): RecipeEntity = RecipeEntity(
            id = recipe.id,
            title = recipe.title,
            description = recipe.description,
            prepTime = recipe.prepTime,
            cookTime = recipe.cookTime,
            servings = recipe.servings,
            userId = recipe.userId,
            imageUrl = recipe.imageUrl,
            createdAt = recipe.createdAt,
            cachedImagePath = cachedImagePath,
            synced = synced,
            updatedAt = updatedAt
        )
    }
}
