package com.hubenko.feature.admin.ui.employees

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EmployeesScreen(
    viewModel: EmployeesViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is EmployeesEffect.NavigateToRegisterEmployee -> onNavigateToRegister()
                is EmployeesEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    EmployeesContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    )
}
