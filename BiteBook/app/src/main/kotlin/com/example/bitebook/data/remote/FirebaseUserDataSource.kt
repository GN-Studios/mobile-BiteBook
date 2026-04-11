package com.example.bitebook.data.remote

import com.example.bitebook.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseUserDataSource {
    private val usersRef =
        FirebaseFirestore.getInstance().collection("users")

    fun saveUser(user: User) {
        usersRef.document(user.id).set(user)
    }

    fun updateUser(user: User) {
        usersRef.document(user.id).set(user)
    }
}