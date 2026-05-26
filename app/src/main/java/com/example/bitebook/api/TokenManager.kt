package com.example.bitebook.api

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun saveUserId(userId: String) {
        prefs.edit().putString("user_id", userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString("user_id", null)
    }

    fun saveUserInfo(username: String, name: String, email: String, image: String?) {
        prefs.edit()
            .putString("username", username)
            .putString("name", name)
            .putString("email", email)
            .putString("image", image)
            .apply()
    }

    fun getUserInfo(): Map<String, String?> {
        return mapOf(
            "username" to prefs.getString("username", ""),
            "name" to prefs.getString("name", ""),
            "email" to prefs.getString("email", ""),
            "image" to prefs.getString("image", null)
        )
    }

    fun clearToken() {
        prefs.edit().clear().apply()
    }
}
