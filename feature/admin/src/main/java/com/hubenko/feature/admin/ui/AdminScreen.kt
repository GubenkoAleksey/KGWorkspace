package com.hubenko.feature.admin.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

/**
 * Stateful Composable для екрана адміністратора.
 * Відповідає за зв'язок між [AdminViewModel] та [AdminContent].
 * Обробляє одноразові ефекти (SideEffects), такі як навігація та тости.
 *
 * @param viewModel ViewModel екрана, ін'єктується через Hilt.
 * @param onNavigateBack Функція зворотного виклику для повернення на попередній екран.
 */
@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AdminEffect.NavigateBack -> onNavigateBack()
                is AdminEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AdminContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onNavigateBack
    )
}
