package com.example.bitebook

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bitebook.api.RetrofitInstance
import com.example.bitebook.api.TokenManager
import com.example.bitebook.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class BiteBookViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitInstance.api
    private val tokenManager = TokenManager(application)

    private val _refreshTrigger = MutableStateFlow(System.currentTimeMillis())

    val recipes: Flow<PagingData<RecipeResponse>> = _refreshTrigger.flatMapLatest {
        Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RecipePagingSource(apiService) }
        ).flow
    }.cachedIn(viewModelScope)

    private val _userId = MutableStateFlow(tokenManager.getUserId() ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(tokenManager.getToken() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    val userRecipes: Flow<PagingData<RecipeResponse>> = combine(_refreshTrigger, _userId) { _, id -> id }
        .flatMapLatest { id ->
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    initialLoadSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { RecipePagingSource(apiService, id) }
            ).flow
        }.cachedIn(viewModelScope)

    private val _userName = MutableStateFlow("Guest")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri.asStateFlow()

    private val _selectedRecipe = MutableStateFlow<RecipeResponse?>(null)
    val selectedRecipe: StateFlow<RecipeResponse?> = _selectedRecipe.asStateFlow()

    init {
        // Initialize Retrofit with context if not already done
        RetrofitInstance.init(application)
        
        // Load user data if logged in
        if (_isLoggedIn.value) {
            // Ideally we'd have a 'me' endpoint, but for now we rely on stored data
            // or fetch users list (not efficient)
        }
    }

    fun login(request: LoginRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.login(request)
                if (response.token != null && response.user?._id != null) {
                    tokenManager.saveToken(response.token)
                    tokenManager.saveUserId(response.user._id)
                    _userId.value = response.user._id
                    _userName.value = response.user.username
                    _userEmail.value = response.user.email
                    _profileImageUri.value = response.user.image
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    onError(response.message ?: "Login failed")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Network error")
            }
        }
    }

    fun register(request: RegisterRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.register(request)
                if (response.token != null && response.user?._id != null) {
                    tokenManager.saveToken(response.token)
                    tokenManager.saveUserId(response.user._id)
                    _userId.value = response.user._id
                    _userName.value = response.user.username
                    _userEmail.value = response.user.email
                    _profileImageUri.value = response.user.image
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    onError(response.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Network error")
            }
        }
    }

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

    fun updateProfile(name: String, imageUri: String?) {
        _userName.value = name
        _profileImageUri.value = imageUri
        // TODO: Call API to update user on server
    }

    fun logout() {
        tokenManager.clearToken()
        _userId.value = ""
        _userName.value = "Guest"
        _userEmail.value = ""
        _profileImageUri.value = null
        _isLoggedIn.value = false
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
