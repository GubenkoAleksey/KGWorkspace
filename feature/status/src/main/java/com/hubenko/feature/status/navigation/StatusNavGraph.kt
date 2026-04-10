package com.hubenko.feature.status.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hubenko.feature.status.ui.StatusScreen

fun NavGraphBuilder.statusGraph(
    onNavigateBack: () -> Unit
) {
    composable<StatusRoute> {
        StatusScreen(onNavigateBack = onNavigateBack)
    }
}
