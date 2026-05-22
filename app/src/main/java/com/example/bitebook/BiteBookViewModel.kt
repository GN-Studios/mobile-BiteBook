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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class BiteBookViewModel : ViewModel() {
    private val apiService = RetrofitInstance.api

    private val _refreshTrigger = MutableStateFlow(System.currentTimeMillis())

    val recipes: Flow<PagingData<RecipeResponse>> = _refreshTrigger.flatMapLatest {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10, // Ensure initial load matches page size to avoid duplicates
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RecipePagingSource(apiService) }
        ).flow
    }.cachedIn(viewModelScope)

    private val _userId = MutableStateFlow("698fc782633cc499a80d94c3") // Hardcoded for now
    val userId: StateFlow<String> = _userId.asStateFlow()

    val userRecipes: Flow<PagingData<RecipeResponse>> = _refreshTrigger.flatMapLatest {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RecipePagingSource(apiService, _userId.value) }
        ).flow
    }.cachedIn(viewModelScope)

    private val _userName = MutableStateFlow("John Doe")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("john.doe@example.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    private val _selectedRecipe = MutableStateFlow<RecipeResponse?>(null)
    val selectedRecipe: StateFlow<RecipeResponse?> = _selectedRecipe.asStateFlow()

    fun triggerRefresh() {
        _refreshTrigger.value = System.currentTimeMillis()
    }

    fun getRecipeById(id: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRecipeById(id)
                _selectedRecipe.value = response.recipe ?: RecipeResponse(
                    _id = response._id ?: "",
                    title = response.title ?: "",
                    description = response.description ?: "",
                    image = response.image,
                    prepTime = response.prepTime ?: 0,
                    cookTime = response.cookTime ?: 0,
                    servings = response.servings ?: 0,
                    ingredients = response.ingredients ?: emptyList(),
                    instructions = response.instructions ?: emptyList(),
                    userId = response.userId,
                    author = response.author,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt
                )
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
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit = {}
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
                triggerRefresh()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
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
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit = {}
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
                triggerRefresh()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                apiService.deleteRecipe(recipeId)
                triggerRefresh()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
