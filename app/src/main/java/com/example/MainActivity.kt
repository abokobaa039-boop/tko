package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.screens.admin.AdminLoginDialog
import com.example.ui.screens.admin.AdminPanelScreen
import com.example.ui.screens.admin.AdminWebPortalScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.games.GameHubScreen
import com.example.ui.screens.table.GameTableScreen
import com.example.ui.screens.winners.WinnersScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                var showAdminLoginDialog by remember { mutableStateOf(false) }

                if (showAdminLoginDialog) {
                    AdminLoginDialog(
                        viewModel = viewModel,
                        onDismiss = { showAdminLoginDialog = false }
                    )
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (currentScreen) {
                            AppScreen.AUTH -> AuthScreen(
                                viewModel = viewModel,
                                onAdminClick = { showAdminLoginDialog = true }
                            )
                            AppScreen.GAME_HUB -> GameHubScreen(
                                viewModel = viewModel,
                                onAdminClick = { showAdminLoginDialog = true }
                            )
                            AppScreen.GAME_TABLE -> GameTableScreen(
                                viewModel = viewModel
                            )
                            AppScreen.WINNERS -> WinnersScreen(
                                viewModel = viewModel
                            )
                            AppScreen.ADMIN_PANEL -> AdminPanelScreen(
                                viewModel = viewModel
                            )
                            AppScreen.ADMIN_WEB_PORTAL -> AdminWebPortalScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}


