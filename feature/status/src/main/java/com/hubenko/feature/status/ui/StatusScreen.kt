package com.hubenko.feature.status.ui

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.feature.status.ui.components.ConfirmationDialog
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
        ConfirmationDialog(
            onDismiss = { viewModel.onIntent(StatusIntent.DismissDialog) }
        )
    }

    StatusContent(
        isLoading = state.isLoading,
        onStatusSubmit = { status -> viewModel.onIntent(StatusIntent.SubmitStatus(status)) },
        onBackClick = onNavigateBack
    )
}
