package com.example.bitebook.data.repository

import com.example.bitebook.data.local.UserDao
import com.example.bitebook.data.model.User
import com.example.bitebook.data.remote.FirebaseUserDataSource

class UserRepository(
    private val userDao: UserDao,
    private val remote: FirebaseUserDataSource
) {
    fun getUser(): User? = userDao.getCurrentUser()

    fun createUser(user: User) {
        userDao.insert(user)
        remote.saveUser(user)
    }

    fun updateUser(user: User) {
        userDao.update(user)
        remote.updateUser(user)
    }
}