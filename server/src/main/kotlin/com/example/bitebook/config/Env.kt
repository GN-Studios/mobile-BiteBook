package com.example.bitebook.config

import java.io.File

object Env {
    private val fileValues: Map<String, String> by lazy {
        val envFile = File(".env")
        if (!envFile.exists()) {
            emptyMap()
        } else {
            envFile.readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains("=") }
                .associate { line ->
                    val separatorIndex = line.indexOf('=')
                    val key = line.substring(0, separatorIndex).trim()
                    val value = line.substring(separatorIndex + 1).trim().removeSurrounding("\"")
                    key to value
                }
        }
    }

    fun get(name: String): String? = System.getenv(name) ?: fileValues[name]

    fun getRequired(name: String): String =
        get(name) ?: error("Missing required config: $name. Add it to .env or your shell environment.")

    fun filePath(name: String): File? =
        get(name)?.let { value ->
            val file = File(value)
            if (file.isAbsolute) file else File(System.getProperty("user.dir"), value)
        }
}
