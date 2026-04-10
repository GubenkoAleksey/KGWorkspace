package com.hubenko.feature.admin.ui.statuses

import android.content.Intent
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
fun StatusesScreen(
    viewModel: StatusesViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is StatusesEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.asString(context))
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

    StatusesContent(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    )
}
