package org.example.com.example.bitebook.data.model

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val prepTime: Int,
    val cookTime: Int,
    val servings: Int,
    val userId: String,
    val imageUrl: String,
    val createdAt: Long
)