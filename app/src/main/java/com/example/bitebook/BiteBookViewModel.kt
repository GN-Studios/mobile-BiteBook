package com.example.bitebook

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BiteBookViewModel : ViewModel() {
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
