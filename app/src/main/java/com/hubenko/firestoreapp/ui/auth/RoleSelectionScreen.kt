package com.hubenko.firestoreapp.ui.auth

import androidx.compose.runtime.Composable

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (isAdmin: Boolean) -> Unit
) {
    RoleSelectionContent(onRoleSelected = onRoleSelected)
}
