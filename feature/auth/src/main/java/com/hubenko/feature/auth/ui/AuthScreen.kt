package com.hubenko.feature.auth.ui

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hubenko.core.presentation.ObserveAsEvents
import com.hubenko.core.presentation.asString

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is AuthEffect.NavigateToHome -> onNavigateToHome()
            is AuthEffect.ShowError -> snackbarHostState.showSnackbar(effect.message.asString(context))
        }
    }

    AuthContent(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    )
}
