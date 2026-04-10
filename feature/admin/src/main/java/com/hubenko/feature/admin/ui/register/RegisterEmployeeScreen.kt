package com.hubenko.feature.admin.ui.register

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
fun RegisterEmployeeScreen(
    viewModel: RegisterEmployeeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is RegisterEmployeeEffect.NavigateBack -> onNavigateBack()
            is RegisterEmployeeEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.asString(context))
        }
    }

    RegisterEmployeeContent(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    )
}
