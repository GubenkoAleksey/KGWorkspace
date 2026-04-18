package com.hubenko.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.mapper.toDocument
import com.hubenko.data.mapper.toEmployeeEntity
import com.hubenko.data.remote.document.EmployeeDocument
import com.hubenko.data.util.firestoreSafeCall
import com.hubenko.domain.error.DataError
import com.hubenko.domain.model.Employee
import com.hubenko.domain.repository.AuthDataSource
import com.hubenko.domain.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val PREFS_NAME = "auth_prefs"
private const val KEY_EMPLOYEE_ID = "employee_id"

class FirebaseAuthDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val employeeDao: EmployeeDao
) : AuthDataSource {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun signIn(email: String, password: String): Result<String, DataError.Firestore> =
        firestoreSafeCall {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User is null after sign-in")
            val doc = firestore.collection("users").document(user.uid).get().await()
            val document = doc.toObject(EmployeeDocument::class.java)
            val employeeId = document?.id?.takeIf { it.isNotEmpty() } ?: user.uid
            if (document != null) {
                employeeDao.insertEmployee(document.toEmployeeEntity(employeeId))
            }
            saveEmployeeId(employeeId)
            user.uid
        }

    override suspend fun signUp(
        email: String,
        password: String,
        lastName: String,
        firstName: String,
        middleName: String,
        phoneNumber: String,
        role: String
    ): Result<String, DataError.Firestore> = firestoreSafeCall {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw Exception("User is null after sign-up")
        val employee = Employee(
            id = user.uid,
            email = email,
            lastName = lastName,
            firstName = firstName,
            middleName = middleName,
            phoneNumber = phoneNumber,
            role = role
        )
        firestore.collection("users").document(user.uid).set(employee.toDocument()).await()
        employeeDao.insertEmployee(employee.toDocument().toEmployeeEntity(user.uid))
        saveEmployeeId(user.uid)
        user.uid
    }

    override suspend fun getUserRole(uid: String): Result<String, DataError.Firestore> =
        firestoreSafeCall {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getString("role") ?: "USER"
        }

    override fun signOut() {
        prefs.edit().remove(KEY_EMPLOYEE_ID).apply()
        auth.signOut()
    }

    override fun isUserLoggedIn() = auth.currentUser != null

    override fun getCurrentUserId(): String? =
        prefs.getString(KEY_EMPLOYEE_ID, null) ?: auth.currentUser?.uid

    private fun saveEmployeeId(id: String) {
        prefs.edit().putString(KEY_EMPLOYEE_ID, id).apply()
    }
}
