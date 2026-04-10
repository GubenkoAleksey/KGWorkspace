package com.hubenko.data.remote.document

import com.google.firebase.firestore.DocumentId

data class EmployeeStatusDocument(
    @DocumentId val id: String = "",
    val employeeId: String = "",
    val status: String = "",
    val note: String? = null,
    val startTime: Long = 0L,
    val endTime: Long? = null
)
