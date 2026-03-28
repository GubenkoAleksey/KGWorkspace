package com.hubenko.feature.admin.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
 * Обробляє одноразові ефекти (SideEffects), такі як навігація, тости та поширення файлів.
 *
 * @param viewModel ViewModel екрана, ін'єктується через Hilt.
 * @param onNavigateBack Функція зворотного виклику для повернення на попередній екран.
 * @param onNavigateToReminderSettings Функція для переходу до налаштувань розкладу співробітника.
 */
@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReminderSettings: (String) -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler {
        viewModel.onIntent(AdminIntent.OnBackClick)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AdminEffect.NavigateBack -> onNavigateBack()
                is AdminEffect.NavigateToReminderSettings -> onNavigateToReminderSettings(effect.employeeId)
                is AdminEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is AdminEffect.ShareFile -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, effect.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Поширити CSV"))
                }
            }
        }
    }

    AdminContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}
