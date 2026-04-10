package com.hubenko.domain.repository

import com.hubenko.domain.error.DataError
import com.hubenko.domain.util.Result

interface AuthDataSource {
    suspend fun signIn(email: String, password: String): Result<String, DataError.Firestore>
    suspend fun signUp(
        email: String,
        password: String,
        lastName: String,
        firstName: String,
        middleName: String,
        phoneNumber: String,
        role: String = "USER"
    ): Result<String, DataError.Firestore>
    suspend fun getUserRole(uid: String): Result<String, DataError.Firestore>
    fun signOut()
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
}
