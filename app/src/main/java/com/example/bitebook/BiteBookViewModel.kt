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

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

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

    private val _accountUsername = MutableStateFlow("")
    val accountUsername: StateFlow<String> = _accountUsername.asStateFlow()

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
            val userInfo = tokenManager.getUserInfo()
            _accountUsername.value = userInfo["username"] ?: ""
            val name = userInfo["name"] ?: ""
            val username = userInfo["username"] ?: ""
            _userName.value = if (!name.isNullOrBlank()) name else if (!username.isNullOrBlank()) username else "Guest"
            _userEmail.value = userInfo["email"] ?: ""
            _profileImageUri.value = userInfo["image"]
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun login(request: LoginRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _authError.value = null
        viewModelScope.launch {
            try {
                val response = apiService.login(request)
                if (response.token != null && response.user?._id != null) {
                    tokenManager.saveToken(response.token)
                    tokenManager.saveUserId(response.user._id)
                    tokenManager.saveUserInfo(
                        response.user.username,
                        response.user.name,
                        response.user.email,
                        response.user.image
                    )
                    _userId.value = response.user._id
                    _accountUsername.value = response.user.username
                    _userName.value = if (response.user.name.isNotBlank()) response.user.name else response.user.username
                    _userEmail.value = response.user.email
                    _profileImageUri.value = response.user.image
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    val message = response.message ?: "Login failed"
                    _authError.value = message
                    onError(message)
                }
            } catch (e: Exception) {
                val message = e.message ?: "Network error"
                _authError.value = message
                onError(message)
            }
        }
    }

    fun register(request: RegisterRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _authError.value = null
        viewModelScope.launch {
            try {
                val response = apiService.register(request)
                if (response.token != null && response.user?._id != null) {
                    tokenManager.saveToken(response.token)
                    tokenManager.saveUserId(response.user._id)
                    tokenManager.saveUserInfo(
                        response.user.username,
                        response.user.name,
                        response.user.email,
                        response.user.image
                    )
                    _userId.value = response.user._id
                    _accountUsername.value = response.user.username
                    _userName.value = if (response.user.name.isNotBlank()) response.user.name else response.user.username
                    _userEmail.value = response.user.email
                    _profileImageUri.value = response.user.image
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    val message = response.message ?: "Registration failed"
                    _authError.value = message
                    onError(message)
                }
            } catch (e: Exception) {
                val message = e.message ?: "Network error"
                _authError.value = message
                onError(message)
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
                    author = response.author ?: response.userId,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private val _isUpdatingProfile = MutableStateFlow(false)
    val isUpdatingProfile: StateFlow<Boolean> = _isUpdatingProfile.asStateFlow()

    fun updateProfile(
        name: String,
        email: String,
        imageUri: String?,
        password: String? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isUpdatingProfile.value = true
            try {
                val currentId = _userId.value
                if (currentId.isNotEmpty()) {
                    // Capture current state as fallback (Previous Data)
                    val prevUsername = _accountUsername.value
                    val prevImage = _profileImageUri.value

                    val userUpdate = User(
                        username = prevUsername,
                        name = name,
                        email = email,
                        image = imageUri,
                        password = password
                    )
                    
                    // Call API but don't rely on its return for updated fields
                    apiService.updateUser(currentId, userUpdate)
                    
                    // Use what was updated (name, email, imageUri) and previous data (prevUsername, prevImage)
                    val finalUsername = prevUsername
                    val finalName = name
                    val finalEmail = email
                    val finalImage = imageUri ?: prevImage

                    // Update local persistence
                    tokenManager.saveUserInfo(
                        finalUsername,
                        finalName,
                        finalEmail,
                        finalImage
                    )

                    // Update ViewModel state flows immediately to refresh the UI
                    _accountUsername.value = finalUsername
                    _userName.value = if (finalName.isNotBlank()) finalName else finalUsername
                    _userEmail.value = finalEmail
                    _profileImageUri.value = finalImage

                    onSuccess()
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update profile")
            } finally {
                _isUpdatingProfile.value = false
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        _userId.value = ""
        _accountUsername.value = ""
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
