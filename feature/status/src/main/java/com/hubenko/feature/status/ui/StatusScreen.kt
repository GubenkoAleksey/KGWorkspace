package com.hubenko.feature.status.ui

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.feature.status.ui.components.StatusConfirmationDialog
import com.hubenko.feature.status.ui.components.SubmitConfirmDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StatusScreen(
    viewModel: StatusViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is StatusEffect.NavigateBack -> onNavigateBack()
                is StatusEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
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
        onIntent = viewModel::onIntent
    )
}
