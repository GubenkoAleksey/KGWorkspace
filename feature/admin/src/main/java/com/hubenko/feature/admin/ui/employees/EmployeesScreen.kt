package com.hubenko.feature.admin.ui.employees

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

/**
 * Stateful Composable для екрана керування співробітниками.
 *
 * @param viewModel ViewModel екрана, ін'єктується через Hilt.
 * @param onNavigateToRegister Callback навігації до реєстрації нового співробітника.
 * @param onBackClick Callback для повернення на Dashboard.
 */
@Composable
fun EmployeesScreen(
    viewModel: EmployeesViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is EmployeesEffect.NavigateToRegisterEmployee -> onNavigateToRegister()
                is EmployeesEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    EmployeesContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick
    )
}

