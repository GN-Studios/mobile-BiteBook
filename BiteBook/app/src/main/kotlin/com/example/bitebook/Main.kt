package com.example.bitebook

import com.example.bitebook.config.Env
import com.example.bitebook.data.model.CreateRecipeRequest
import com.example.bitebook.data.model.Recipe
import com.example.bitebook.data.repository.RecipeRepository
import com.example.bitebook.firebase.FirebaseInitializer
import com.example.bitebook.utils.IdProvider
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    val recipeRepository by lazy { RecipeRepository(FirebaseInitializer.initialize()) }
    val port = Env.get("PORT")?.toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port) {
        install(CallLogging)
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }

        routing {
            get("/") {
                call.respondText(
                    """
                    BiteBook recipe server is running.

                    Available routes:
                    GET /health
                    GET /health/cache
                    GET /health/firebase
                    GET /health/firebase/details
                    GET /recipes
                    POST /recipes/test
                    POST /recipes
                    """.trimIndent()
                )
            }

            get("/health") {
                call.respond(mapOf("status" to "ok"))
            }

            get("/health/cache") {
                call.respond(recipeRepository.getCacheStatus())
            }

            get("/health/firebase") {
                val connected = runCatching { recipeRepository.checkConnection() }.getOrElse { error ->
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf(
                            "status" to "error",
                            "message" to (error.message ?: "Firebase connection failed")
                        )
                    )
                    return@get
                }

                call.respond(
                    mapOf(
                        "status" to "ok",
                        "firebase" to connected
                    )
                )
            }

            get("/health/firebase/details") {
                val projectId = Env.get("FIREBASE_PROJECT_ID")
                val databaseUrl = Env.get("FIREBASE_DATABASE_URL")
                val credentialsPath = Env.get("GOOGLE_APPLICATION_CREDENTIALS")

                val result = runCatching { recipeRepository.checkConnection() }

                if (result.isFailure) {
                    val error = result.exceptionOrNull()
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf(
                            "status" to "error",
                            "projectId" to projectId,
                            "databaseUrl" to databaseUrl,
                            "credentialsPath" to credentialsPath,
                            "message" to (error?.message ?: "Firebase connection failed"),
                            "errorType" to (error?.javaClass?.name ?: "unknown")
                        )
                    )
                    return@get
                }

                call.respond(
                    mapOf(
                        "status" to "ok",
                        "projectId" to projectId,
                        "databaseUrl" to databaseUrl,
                        "credentialsPath" to credentialsPath,
                        "message" to "Realtime Database write/read check succeeded"
                    )
                )
            }

            get("/recipes") {
                val recipes = runCatching { recipeRepository.getAllRecipes() }.getOrElse { error ->
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf(
                            "status" to "error",
                            "message" to (error.message ?: "Could not load recipes")
                        )
                    )
                    return@get
                }

                call.respond(recipes)
            }

            post("/recipes/test") {
                val recipe = Recipe(
                    id = IdProvider.newId(),
                    title = "Firebase Test Recipe",
                    description = "Created to verify Firebase sync and local Room cache",
                    prepTime = 10,
                    cookTime = 15,
                    servings = 2,
                    userId = "test-user",
                    imageUrl = "",
                    createdAt = System.currentTimeMillis()
                )

                runCatching { recipeRepository.addRecipe(recipe) }.getOrElse { error ->
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf(
                            "status" to "error",
                            "message" to (error.message ?: "Could not create test recipe")
                        )
                    )
                    return@post
                }

                call.respond(HttpStatusCode.Created, recipe)
            }

            post("/recipes") {
                val request = call.receive<CreateRecipeRequest>()
                val recipe = Recipe(
                    id = IdProvider.newId(),
                    title = request.title,
                    description = request.description,
                    prepTime = request.prepTime,
                    cookTime = request.cookTime,
                    servings = request.servings,
                    userId = request.userId,
                    imageUrl = request.imageUrl,
                    createdAt = System.currentTimeMillis()
                )

                runCatching { recipeRepository.addRecipe(recipe) }.getOrElse { error ->
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf(
                            "status" to "error",
                            "message" to (error.message ?: "Could not create recipe")
                        )
                    )
                    return@post
                }

                call.respond(HttpStatusCode.Created, recipe)
            }
        }
    }.start(wait = true)
}
