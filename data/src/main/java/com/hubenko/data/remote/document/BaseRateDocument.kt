package com.hubenko.data.remote.document

import com.google.firebase.firestore.PropertyName

data class BaseRateDocument(
    val id: String = "",
    val label: String = "",
    val value: Double = 0.0,
    @get:PropertyName("isSystem") @set:PropertyName("isSystem") var isSystem: Boolean = false
)
