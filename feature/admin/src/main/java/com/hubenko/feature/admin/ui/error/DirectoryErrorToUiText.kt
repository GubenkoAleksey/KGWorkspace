package com.hubenko.feature.admin.ui.error

import com.hubenko.core.presentation.UiText
import com.hubenko.core.presentation.toUiText
import com.hubenko.domain.error.DirectoryError

fun DirectoryError.toUiText(): UiText = when (this) {
    DirectoryError.IsProtected ->
        UiText.DynamicString("Системний запис — видалення заборонено")
    is DirectoryError.Firestore ->
        cause.toUiText()
}
