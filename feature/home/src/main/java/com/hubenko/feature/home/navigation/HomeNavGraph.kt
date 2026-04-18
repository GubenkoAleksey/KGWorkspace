package com.hubenko.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hubenko.feature.home.ui.HomeScreen

fun NavGraphBuilder.homeGraph(
    onNavigateToStatus: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToMyStatuses: (String) -> Unit,
    onThemeToggle: () -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToStatus = onNavigateToStatus,
            onNavigateToAdmin = onNavigateToAdmin,
            onNavigateToAuth = onNavigateToAuth,
            onNavigateToMyStatuses = onNavigateToMyStatuses,
            onThemeToggle = onThemeToggle
        )
    }
}
