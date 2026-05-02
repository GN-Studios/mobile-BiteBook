package com.example.bitebook.data.remote

import com.example.bitebook.data.model.Recipe
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class FirebaseRecipeDataSource(
    private val database: FirebaseDatabase
) {
    private val recipesRef = database.getReference("recipes")
    private val healthRef = database.getReference("_health")

    fun addRecipe(recipe: Recipe) {
        recipesRef.child(recipe.id).setValueAsync(recipe).get(10, TimeUnit.SECONDS)
    }

    fun updateRecipe(recipe: Recipe) {
        recipesRef.child(recipe.id).setValueAsync(recipe).get(10, TimeUnit.SECONDS)
    }

    fun deleteRecipe(recipeId: String) {
        recipesRef.child(recipeId).removeValueAsync().get(10, TimeUnit.SECONDS)
    }

    fun getAllRecipes(): List<Recipe> {
        val snapshot = readSnapshot()
        val type = object : GenericTypeIndicator<Map<String, Recipe>>() {}
        val recipesMap = snapshot.getValue(type).orEmpty()
        return recipesMap.values.sortedByDescending { it.createdAt }
    }

    fun checkConnection(): Boolean {
        val payload = mapOf(
            "status" to "ok",
            "checkedAt" to System.currentTimeMillis()
        )

        healthRef.child("ping").setValueAsync(payload).get(10, TimeUnit.SECONDS)
        readSnapshot(healthRef.child("ping"))
        return true
    }

    private fun readSnapshot(reference: com.google.firebase.database.DatabaseReference = recipesRef): DataSnapshot {
        val future = CompletableFuture<DataSnapshot>()

        reference.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    future.complete(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    future.completeExceptionally(error.toException())
                }
            }
        )

        return future.get(10, TimeUnit.SECONDS)
    }
}
