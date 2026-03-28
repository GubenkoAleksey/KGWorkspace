package com.hubenko.feature.admin.ui.register

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

/**
 * Stateful Composable для екрана реєстрації нового співробітника.
 *
 * @param viewModel ViewModel екрана, ін'єктується через Hilt.
 * @param onNavigateBack Функція для повернення на попередній екран.
 */
@Composable
fun RegisterEmployeeScreen(
    viewModel: RegisterEmployeeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterEmployeeEffect.NavigateBack -> onNavigateBack()
                is RegisterEmployeeEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    RegisterEmployeeContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

