package com.example.bitebook.api

import com.example.bitebook.data.*
import retrofit2.http.*

interface ApiService {
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
    suspend fun getRecipeById(@Path("id") id: String): RecipeResponse

    @GET("api/recipes/userRecipes/{userId}")
    suspend fun getRecipesByUserId(@Path("userId") userId: String): List<RecipeResponse>

    @POST("api/recipes")
    suspend fun createRecipe(@Body recipe: RecipeRequest): RecipeResponse

    @PUT("api/recipes/{id}")
    suspend fun updateRecipe(@Path("id") id: String, @Body recipe: RecipeRequest): RecipeResponse

    @DELETE("api/recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: String)

    @POST("api/comments")
    suspend fun createComment(@Body comment: Comment): Comment

    @GET("api/comments/recipe/{recipeId}")
    suspend fun getCommentsByRecipeId(@Path("recipeId") recipeId: String): List<Comment>

    @POST("api/likes")
    suspend fun createLike(@Body like: Like)

    @HTTP(method = "DELETE", path = "api/likes", hasBody = true)
    suspend fun deleteLike(@Body like: Like)

    @POST("api/chatgpt/suggest-recipes")
    suspend fun suggestRecipes(@Body request: ChatGptRequest): ChatGptResponse
}
