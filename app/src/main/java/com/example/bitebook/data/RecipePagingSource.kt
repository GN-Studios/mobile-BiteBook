package com.example.bitebook.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bitebook.api.ApiService

class RecipePagingSource(
    private val apiService: ApiService,
    private val userId: String? = null
) : PagingSource<Int, RecipeResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeResponse> {
        return try {
            val nextPageNumber = params.key ?: 1
            
            val recipes = if (userId != null) {
                // If userId is provided, fetch recipes specifically for that user
                // Note: The backend getRecipesByUserId currently doesn't support pagination in the same way,
                // it returns a List<RecipeResponse> directly.
                apiService.getRecipesByUserId(userId)
            } else {
                // Otherwise fetch all recipes paginated
                val response = apiService.getRecipesWithDetails(page = nextPageNumber, limit = params.loadSize)
                response.data
            }
            
            LoadResult.Page(
                data = recipes,
                prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                // Stop paging if we received fewer items than requested (standard end of list check)
                // or if we're fetching user-specific recipes which aren't paginated yet.
                nextKey = if (userId != null || recipes.isEmpty() || recipes.size < params.loadSize) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RecipeResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
