package com.example.bitebook

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val time: String,
    val servings: Int,
    val ingredients: List<Pair<String, String>> = emptyList(),
    val instructions: List<String> = emptyList()
)

class BiteBookViewModel : ViewModel() {
    private val _recipes = MutableStateFlow(
        listOf(
            Recipe(
                "1",
                "Classic Margherita Pizza",
                "A traditional Italian pizza with fresh mozzarella, tomatoes, and basil on a crispy...",
                null,
                "45 min",
                4,
                ingredients = listOf("1" to "Pizza dough", "1/2 cup" to "Tomato sauce", "200g" to "Fresh mozzarella", "Handful" to "Fresh basil"),
                instructions = listOf("Preheat oven to 250°C", "Roll out the dough", "Spread sauce and add cheese", "Bake for 10-12 minutes")
            ),
            Recipe(
                "2",
                "Pasta Carbonara",
                "Creamy pasta with pancetta, egg, and parmesan cheese.",
                null,
                "30 min",
                2,
                ingredients = listOf("200g" to "Spaghetti", "100g" to "Pancetta", "2" to "Large eggs", "50g" to "Pecorino Romano"),
                instructions = listOf("Boil pasta", "Fry pancetta", "Mix eggs and cheese", "Combine all with a splash of pasta water")
            ),
            Recipe(
                "3",
                "Greek Salad",
                "Fresh cucumber, tomatoes, olives, and feta cheese with olive oil.",
                null,
                "15 min",
                1,
                ingredients = listOf("1" to "Cucumber", "2" to "Tomatoes", "50g" to "Feta cheese", "10" to "Kalamata olives"),
                instructions = listOf("Chop vegetables", "Combine in a bowl", "Add olives and feta", "Drizzle with olive oil")
            )
        )
    )
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _userRecipes = MutableStateFlow(
        listOf(
            Recipe(
                "2",
                "Pasta Carbonara",
                "Creamy pasta with pancetta, egg, and parmesan cheese.",
                null,
                "30 min",
                2,
                ingredients = listOf("200g" to "Spaghetti", "100g" to "Pancetta", "2" to "Large eggs", "50g" to "Pecorino Romano"),
                instructions = listOf("Boil pasta", "Fry pancetta", "Mix eggs and cheese", "Combine all with a splash of pasta water")
            ),
            Recipe(
                "3",
                "Greek Salad",
                "Fresh cucumber, tomatoes, olives, and feta cheese with olive oil.",
                null,
                "15 min",
                1,
                ingredients = listOf("1" to "Cucumber", "2" to "Tomatoes", "50g" to "Feta cheese", "10" to "Kalamata olives"),
                instructions = listOf("Chop vegetables", "Combine in a bowl", "Add olives and feta", "Drizzle with olive oil")
            )
        )
    )
    val userRecipes: StateFlow<List<Recipe>> = _userRecipes.asStateFlow()

    private val _userName = MutableStateFlow("John Doe")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("john.doe@example.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    fun updateProfile(name: String, imageUri: String?) {
        _userName.value = name
        _profileImageUri.value = imageUri
    }

    fun addRecipe(recipe: Recipe) {
        _userRecipes.value = _userRecipes.value + recipe
        _recipes.value = _recipes.value + recipe
    }

    fun updateRecipe(updatedRecipe: Recipe) {
        _userRecipes.value = _userRecipes.value.map { if (it.id == updatedRecipe.id) updatedRecipe else it }
        _recipes.value = _recipes.value.map { if (it.id == updatedRecipe.id) updatedRecipe else it }
    }

    fun deleteRecipe(recipeId: String) {
        _userRecipes.value = _userRecipes.value.filter { it.id != recipeId }
        _recipes.value = _recipes.value.filter { it.id != recipeId }
    }
}
