package com.hubenko.data.remote.document

import com.google.firebase.firestore.DocumentId

data class RoleDocument(
    @DocumentId val id: String = "",
    val label: String = ""
)
