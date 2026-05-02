package com.example.bitebook.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val prepTime: Int = 0,
    val cookTime: Int = 0,
    val servings: Int = 0,
    val userId: String = "",
    val imageUrl: String = "",
    val createdAt: Long = 0
)

@Serializable
data class CreateRecipeRequest(
    val title: String,
    val description: String,
    val prepTime: Int,
    val cookTime: Int,
    val servings: Int,
    val userId: String,
    val imageUrl: String = ""
)
