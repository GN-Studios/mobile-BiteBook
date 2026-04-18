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
    val likes: Int
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
                41
            ),
            Recipe(
                "2",
                "Pasta Carbonara",
                "Creamy pasta with pancetta, egg, and parmesan cheese.",
                null,
                "30 min",
                2,
                25
            ),
            Recipe(
                "3",
                "Greek Salad",
                "Fresh cucumber, tomatoes, olives, and feta cheese with olive oil.",
                null,
                "15 min",
                1,
                12
            )
        )
    )
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _userRecipes = MutableStateFlow(listOf("Pasta Carbonara", "Homemade Pizza", "Greek Salad", "Chocolate Brownies"))
    val userRecipes: StateFlow<List<String>> = _userRecipes.asStateFlow()

    private val _userName = MutableStateFlow("John Doe")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("john.doe@example.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    fun addRecipe(recipe: String) {
        _userRecipes.value = _userRecipes.value + recipe
    }
}
