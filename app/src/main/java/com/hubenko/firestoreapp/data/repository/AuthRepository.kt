package com.hubenko.firestoreapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.firestoreapp.data.local.EmployeeDao
import com.hubenko.firestoreapp.data.local.EmployeeEntity
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val employeeDao: EmployeeDao // Added DAO to save locally
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                // Fetch full profile from Firestore and save to Room
                val doc = firestore.collection("users").document(user.uid).get().await()
                val entity = EmployeeEntity(
                    id = user.uid,
                    lastName = doc.getString("lastName") ?: "",
                    firstName = doc.getString("firstName") ?: "",
                    middleName = doc.getString("middleName") ?: "",
                    phoneNumber = doc.getString("phoneNumber") ?: "",
                    role = doc.getString("role") ?: "USER"
                )
                employeeDao.insertEmployee(entity)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(
        email: String, 
        password: String, 
        lastName: String,
        firstName: String,
        middleName: String,
        phoneNumber: String,
        role: String = "USER"
    ): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val userMap = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "lastName" to lastName,
                    "firstName" to firstName,
                    "middleName" to middleName,
                    "phoneNumber" to phoneNumber,
                    "role" to role
                )
                // 1. Save to Firestore
                firestore.collection("users").document(user.uid).set(userMap).await()
                
                // 2. Save to Local Room DB
                val entity = EmployeeEntity(
                    id = user.uid,
                    lastName = lastName,
                    firstName = firstName,
                    middleName = middleName,
                    phoneNumber = phoneNumber,
                    role = role
                )
                employeeDao.insertEmployee(entity)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(uid: String): String {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.getString("role") ?: "USER"
        } catch (e: Exception) {
            "USER"
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
