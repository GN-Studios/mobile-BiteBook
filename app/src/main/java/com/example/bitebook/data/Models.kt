package com.example.bitebook.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class User(
    val id: String? = null,
    val name: String = "",
    val email: String = "",
    val password: String? = null
)

@Serializable
data class Ingredient(
    val amount: String = "",
    val name: String = "",
    val _id: String? = null
)

@Serializable
data class RecipeResponse(
    val _id: String = "",
    val title: String = "",
    val description: String = "",
    val image: String? = null,
    val prepTime: Int = 0,
    val cookTime: Int = 0,
    val servings: Int = 0,
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val userId: String? = null,
    val author: User? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class RecipeListResponse(
    val data: List<RecipeResponse> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class SingleRecipeResponse(
    val message: String? = null,
    @JsonNames("data", "recipe")
    val recipe: RecipeResponse? = null,
    val _id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val image: String? = null,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val servings: Int? = null,
    val ingredients: List<Ingredient>? = null,
    val instructions: List<String>? = null,
    val userId: String? = null,
    val author: User? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val __v: Int? = null
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
data class ChatGptRequest(
    val input: String
)

@Serializable
data class ChatGptResponse(
    val suggestion: String
)
