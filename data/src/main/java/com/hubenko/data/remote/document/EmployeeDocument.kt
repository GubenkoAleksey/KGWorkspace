package com.hubenko.data.remote.document

data class EmployeeDocument(
    val id: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val phoneNumber: String = "",
    val role: String = "USER",
    val email: String = ""
)
