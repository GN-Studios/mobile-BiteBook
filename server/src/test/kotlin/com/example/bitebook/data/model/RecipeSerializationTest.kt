package com.example.bitebook.data.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class RecipeSerializationTest {
    @Test
    fun `recipe request serializes expected fields`() {
        val request = CreateRecipeRequest(
            title = "Shakshuka",
            description = "Eggs in tomato sauce",
            prepTime = 10,
            cookTime = 15,
            servings = 2,
            userId = "user-1",
            imageUrl = ""
        )

        val json = Json.encodeToString(CreateRecipeRequest.serializer(), request)

        assertEquals(
            """{"title":"Shakshuka","description":"Eggs in tomato sauce","prepTime":10,"cookTime":15,"servings":2,"userId":"user-1"}""",
            json
        )
    }
}
