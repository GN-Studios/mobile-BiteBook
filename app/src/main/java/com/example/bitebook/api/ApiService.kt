package com.example.bitebook.api

import com.example.bitebook.data.*
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/users")
    suspend fun getUsers(): List<User>

    @POST("api/users")
    suspend fun createUser(@Body user: User): User

    @GET("api/recipes")
    suspend fun getRecipes(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): RecipeListResponse

    @GET("api/recipes/with-details")
    suspend fun getRecipesWithDetails(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): RecipeListResponse

    @GET("api/recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: String): SingleRecipeResponse

    @GET("api/recipes/userRecipes/{userId}")
    suspend fun getRecipesByUserId(@Path("userId") userId: String): List<RecipeResponse>

    @POST("api/recipes")
    suspend fun createRecipe(@Body recipe: RecipeRequest): SingleRecipeResponse

    @PUT("api/recipes/{id}")
    suspend fun updateRecipe(@Path("id") id: String, @Body recipe: RecipeRequest): SingleRecipeResponse

    @DELETE("api/recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: String)

    @POST("api/chatgpt/suggest-recipes")
    suspend fun suggestRecipes(@Body request: ChatGptRequest): ChatGptResponse
}
