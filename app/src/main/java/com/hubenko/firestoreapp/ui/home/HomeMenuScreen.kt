package com.hubenko.firestoreapp.ui.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeMenuScreen(
    onNavigateToStatus: () -> Unit,
    isAdmin: Boolean = true
) {
    val context = LocalContext.current

    HomeMenuContent(
        isAdmin = isAdmin,
        onNavigateToStatus = onNavigateToStatus,
        onAdminPanelClick = {
            Toast.makeText(context, "Панель адміністратора в розробці...", Toast.LENGTH_SHORT).show()
        }
    )
}
