package com.hubenko.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hubenko.feature.auth.ui.AuthScreen

fun NavGraphBuilder.authGraph(
    onNavigateToHome: () -> Unit
) {
    composable<AuthRoute> {
        AuthScreen(onNavigateToHome = onNavigateToHome)
    }
}
