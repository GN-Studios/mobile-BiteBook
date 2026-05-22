package com.example.bitebook.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val password: String? = null
)

@Serializable
data class Ingredient(
    val amount: String,
    val name: String
)

@Serializable
data class RecipeResponse(
    val _id: String,
    val title: String,
    val description: String,
    val image: String? = null,
    val prepTime: Int,
    val cookTime: Int,
    val servings: Int,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val userId: String? = null,
    val author: User? = null, // Backend uses 'author' in aggregate
    val commentsCount: Int? = null,
    val likesCount: Int? = null
)

@Serializable
data class RecipeListResponse(
    val data: List<RecipeResponse>
)

@Serializable
data class SingleRecipeResponse(
    val data: RecipeResponse
)

@Serializable
data class RecipeRequest(
    val title: String,
    val description: String,
    val image: String? = null,
    val prepTime: Int,
    val cookTime: Int,
    val servings: Int,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val userId: String
)

@Serializable
data class Comment(
    val _id: String? = null,
    val text: String,
    val userId: String,
    val recipeId: String
)

@Serializable
data class Like(
    val userId: String,
    val recipeId: String
)

@Serializable
data class ChatGptRequest(
    val input: String
)

@Serializable
data class ChatGptResponse(
    val suggestion: String
)
