package com.hubenko.domain.repository

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<String> // returns uid
    suspend fun signUp(
        email: String,
        password: String,
        lastName: String,
        firstName: String,
        middleName: String,
        phoneNumber: String,
        role: String = "USER"
    ): Result<String>
    suspend fun getUserRole(uid: String): String
    fun signOut()
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
}
