package com.hubenko.data.remote.document

import com.google.firebase.firestore.PropertyName

data class RoleDocument(
    val id: String = "",
    val label: String = "",
    @get:PropertyName("isSystem") @set:PropertyName("isSystem") var isSystem: Boolean = false
)
