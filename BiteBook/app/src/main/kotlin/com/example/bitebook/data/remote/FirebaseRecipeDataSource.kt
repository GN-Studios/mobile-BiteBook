package com.example.bitebook.data.remote

import com.example.bitebook.data.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRecipeDataSource {
    private val recipesRef =
        FirebaseFirestore.getInstance().collection("recipes")

    fun addRecipe(recipe: Recipe) {
        recipesRef.document(recipe.id).set(recipe)
    }

    fun updateRecipe(recipe: Recipe) {
        recipesRef.document(recipe.id).set(recipe)
    }

    fun deleteRecipe(recipeId: String) {
        recipesRef.document(recipeId).delete()
    }
}