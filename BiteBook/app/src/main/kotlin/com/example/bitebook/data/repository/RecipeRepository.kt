package com.example.bitebook.data.repository

import com.example.bitebook.data.local.ReciperecipeDao
import com.example.bitebook.data.model.Recipe
import com.example.bitebook.data.remote.FirebaseRecipeDataSource

class RecipeRepository(
    private val recipeDao: ReciperecipeDao,
    private val remote: FirebaseRecipeDataSource
) {
    fun getAllRecipes(): List<Recipe> = recipeDao.getAll()

    fun getUserRecipes(userId: String): List<Recipe> =
        recipeDao.getByUser(userId)

    fun addRecipe(recipe: Recipe) {
        recipeDao.insert(recipe)
        remote.addRecipe(recipe)
    }

    fun updateRecipe(recipe: Recipe) {
        recipeDao.update(recipe)
        remote.updateRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe) {
        recipeDao.delete(recipe)
        remote.deleteRecipe(recipe.id)
    }
}