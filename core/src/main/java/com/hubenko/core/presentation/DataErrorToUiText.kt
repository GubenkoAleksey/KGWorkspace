package com.hubenko.core.presentation

import com.hubenko.core.R
import com.hubenko.domain.error.DataError

fun DataError.toUiText(): UiText {
    return when (this) {
        DataError.Firestore.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
        DataError.Firestore.PERMISSION_DENIED -> UiText.StringResource(R.string.error_permission_denied)
        DataError.Firestore.UNAVAILABLE -> UiText.StringResource(R.string.error_unavailable)
        DataError.Firestore.UNAUTHENTICATED -> UiText.StringResource(R.string.error_unauthenticated)
        DataError.Firestore.ALREADY_EXISTS -> UiText.StringResource(R.string.error_already_exists)
        DataError.Firestore.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        DataError.Local.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
        DataError.Local.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
    }
}
