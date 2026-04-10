package com.hubenko.data.remote.document

import com.google.firebase.firestore.DocumentId

data class EmployeeDocument(
    @DocumentId val id: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val phoneNumber: String = "",
    val role: String = "USER",
    val email: String = ""
)
