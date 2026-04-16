package com.hubenko.data.remote.document

import com.google.firebase.firestore.PropertyName

data class StatusTypeDocument(
    val type: String = "",
    val label: String = "",
    @get:PropertyName("isSystem") @set:PropertyName("isSystem") var isSystem: Boolean = false
)
