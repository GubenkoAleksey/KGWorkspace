package com.hubenko.firestoreapp.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hubenko.firestoreapp.ui.theme.StatusOfficeLight

@Composable
fun HomeMenuScreen(
    onNavigateToStatus: () -> Unit,
    isAdmin: Boolean = true
) {
    val context = LocalContext.current

    HomeMenuContent(
        isAdmin = isAdmin,
        onNavigateToStatus = onNavigateToStatus,
        onAdminPanelClick = {
            Toast.makeText(context, "Панель адміністратора в розробці...", Toast.LENGTH_SHORT).show()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuContent(
    isAdmin: Boolean,
    onNavigateToStatus: () -> Unit,
    onAdminPanelClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Головне меню", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.125f))

            MenuButton(
                text = "Відправити статус",
                onClick = onNavigateToStatus
            )

            if (isAdmin) {
                Spacer(modifier = Modifier.height(24.dp))
                MenuButton(
                    text = "Панель адміністратора",
                    onClick = onAdminPanelClick
                )
            }
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusOfficeLight),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, fontSize = 18.sp, color = Color.White)
    }
}
