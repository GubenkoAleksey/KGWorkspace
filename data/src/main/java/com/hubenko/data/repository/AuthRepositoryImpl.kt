package com.hubenko.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val employeeDao: EmployeeDao
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val doc = firestore.collection("users").document(user.uid).get().await()
                val entity = EmployeeEntity(
                    id = user.uid,
                    lastName = doc.getString("lastName") ?: "",
                    firstName = doc.getString("firstName") ?: "",
                    middleName = doc.getString("middleName") ?: "",
                    phoneNumber = doc.getString("phoneNumber") ?: "",
                    role = doc.getString("role") ?: "USER",
                    email = doc.getString("email") ?: user.email ?: "",
                    password = doc.getString("password") ?: ""
                )
                employeeDao.insertEmployee(entity)
                Result.success(user.uid)
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        lastName: String,
        firstName: String,
        middleName: String,
        phoneNumber: String,
        role: String
    ): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val userMap = hashMapOf(
                    "uid" to user.uid,
                    "id" to user.uid,
                    "email" to email,
                    "password" to password, // Зберігаємо пароль для відображення адміну
                    "lastName" to lastName,
                    "firstName" to firstName,
                    "middleName" to middleName,
                    "phoneNumber" to phoneNumber,
                    "role" to role
                )
                firestore.collection("users").document(user.uid).set(userMap).await()
                
                val entity = EmployeeEntity(
                    id = user.uid,
                    lastName = lastName,
                    firstName = firstName,
                    middleName = middleName,
                    phoneNumber = phoneNumber,
                    role = role,
                    email = email,
                    password = password
                )
                employeeDao.insertEmployee(entity)
                Result.success(user.uid)
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserRole(uid: String): String {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.getString("role") ?: "USER"
        } catch (_: Exception) {
            "USER"
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
