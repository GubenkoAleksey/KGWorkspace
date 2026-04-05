package com.hubenko.feature.status.ui

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.feature.status.ui.components.StatusConfirmationDialog
import com.hubenko.feature.status.ui.components.SubmitConfirmDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StatusScreen(
    viewModel: StatusViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is StatusEffect.NavigateBack -> onNavigateBack()
                is StatusEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    if (state.isSuccess) {
        StatusConfirmationDialog(
            onDismiss = { viewModel.onIntent(StatusIntent.DismissDialog) }
        )
    }

    if (state.showConfirmDialog && state.pendingStatus != null) {
        SubmitConfirmDialog(
            status = state.pendingStatus!!,
            onConfirm = { viewModel.onIntent(StatusIntent.ConfirmSubmit) },
            onDismiss = { viewModel.onIntent(StatusIntent.DismissConfirmDialog) }
        )
    }

    StatusContent(
        state = state,
        onIntent = viewModel::onIntent,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    )
}
