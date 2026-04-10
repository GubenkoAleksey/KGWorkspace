package com.hubenko.domain.error

import com.hubenko.domain.util.Error

sealed interface DataError : Error {

    enum class Firestore : DataError {
        NOT_FOUND,
        PERMISSION_DENIED,
        UNAVAILABLE,
        UNAUTHENTICATED,
        ALREADY_EXISTS,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN
    }
}

