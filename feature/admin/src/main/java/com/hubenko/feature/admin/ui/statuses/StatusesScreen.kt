package com.hubenko.feature.admin.ui.statuses

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

/**
 * Stateful Composable для екрана перегляду статусів.
 *
 * @param viewModel ViewModel екрана, ін'єктується через Hilt.
 * @param onBackClick Callback для повернення на Dashboard.
 */
@Composable
fun StatusesScreen(
    viewModel: StatusesViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is StatusesEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is StatusesEffect.ShareFile -> {
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

    StatusesContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick
    )
}

