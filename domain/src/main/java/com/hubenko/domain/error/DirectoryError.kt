package com.hubenko.domain.error

import com.hubenko.domain.util.Error

sealed interface DirectoryError : Error {
    data object IsProtected : DirectoryError
    data class Firestore(val cause: DataError.Firestore) : DirectoryError
}
