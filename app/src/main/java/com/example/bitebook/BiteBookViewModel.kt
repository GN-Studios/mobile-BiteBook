package com.example.bitebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bitebook.api.RetrofitInstance
import com.example.bitebook.data.Ingredient
import com.example.bitebook.data.RecipeRequest
import com.example.bitebook.data.RecipeResponse
import com.example.bitebook.data.RecipePagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BiteBookViewModel : ViewModel() {
    private val apiService = RetrofitInstance.api

    val recipes: Flow<PagingData<RecipeResponse>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { RecipePagingSource(apiService) }
    ).flow.cachedIn(viewModelScope)

    private val _userId = MutableStateFlow("698fc782633cc499a80d94c3") // Hardcoded for now
    val userId: StateFlow<String> = _userId.asStateFlow()

    val userRecipes: Flow<PagingData<RecipeResponse>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { RecipePagingSource(apiService, _userId.value) }
    ).flow.cachedIn(viewModelScope)

    private val _userName = MutableStateFlow("John Doe")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("john.doe@example.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    private val _selectedRecipe = MutableStateFlow<RecipeResponse?>(null)
    val selectedRecipe: StateFlow<RecipeResponse?> = _selectedRecipe.asStateFlow()

    init {
        // loadUserRecipes() no longer needed as userRecipes is now a Paging Flow
    }

    fun getRecipeById(id: String) {
        viewModelScope.launch {
            try {
                _selectedRecipe.value = apiService.getRecipeById(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadUserRecipes() {
        // Re-triggering of paging data can be handled by UI or by refreshing the flow if needed
    }

    fun updateProfile(name: String, imageUri: String?) {
        _userName.value = name
        _profileImageUri.value = imageUri
    }

    fun logout() {
        // TODO: Implement actual logout (clear tokens, navigate to login)
        _userId.value = ""
        _userName.value = ""
        _userEmail.value = ""
        _profileImageUri.value = null
    }

    fun addRecipe(
        title: String,
        description: String,
        image: String?,
        prepTime: Int,
        cookTime: Int,
        servings: Int,
        ingredients: List<Ingredient>,
        instructions: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = RecipeRequest(
                    title = title,
                    description = description,
                    image = image,
                    prepTime = prepTime,
                    cookTime = cookTime,
                    servings = servings,
                    ingredients = ingredients,
                    instructions = instructions,
                    userId = _userId.value
                )
                apiService.createRecipe(request)
                loadUserRecipes()
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateRecipe(
        id: String,
        title: String,
        description: String,
        image: String?,
        prepTime: Int,
        cookTime: Int,
        servings: Int,
        ingredients: List<Ingredient>,
        instructions: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = RecipeRequest(
                    title = title,
                    description = description,
                    image = image,
                    prepTime = prepTime,
                    cookTime = cookTime,
                    servings = servings,
                    ingredients = ingredients,
                    instructions = instructions,
                    userId = _userId.value
                )
                apiService.updateRecipe(id, request)
                loadUserRecipes()
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                apiService.deleteRecipe(recipeId)
                loadUserRecipes()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
