package com.example.googleroom


import android.app.Application

class UserRepository(application: Application) {

    private val userDao: UserDao = LoginDatabase.getDatabase(application).userDao()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUser(id: String): User? {
        return userDao.getUser(id)
    }
}
