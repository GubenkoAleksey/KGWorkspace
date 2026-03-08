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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuScreen(
    onNavigateToStatus: () -> Unit,
    isAdmin: Boolean = true
) {
    val context = LocalContext.current
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

            Button(
                onClick = onNavigateToStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusOfficeLight),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Відправити статус", fontSize = 18.sp, color = Color.White)
            }

            if (isAdmin) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        Toast.makeText(context, "Панель адміністратора в розробці...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusOfficeLight),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Панель адміністратора", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}
