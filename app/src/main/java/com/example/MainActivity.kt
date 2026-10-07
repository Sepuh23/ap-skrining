package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PsyVibeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PsyVibeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()

                // Back navigation handling
                BackHandler(enabled = uiState.currentScreen != AppScreen.DASHBOARD && uiState.currentScreen != AppScreen.LANDING) {
                    if (uiState.currentScreen == AppScreen.LOGIN) {
                        viewModel.navigateTo(AppScreen.LANDING)
                    } else {
                        viewModel.navigateTo(AppScreen.DASHBOARD)
                    }
                }

                val showBottomNav = uiState.currentScreen in listOf(
                    AppScreen.DASHBOARD,
                    AppScreen.CURHAT_SCAN,
                    AppScreen.GAME_STRESS,
                    AppScreen.DIRECTORY,
                    AppScreen.PROFILE
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNav) {
                            BottomNavBar(
                                currentScreen = uiState.currentScreen,
                                onNavigate = { screen -> viewModel.navigateTo(screen) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Crossfade(targetState = uiState.currentScreen, label = "screen_transition") { screen ->
                            when (screen) {
                                AppScreen.LANDING -> LandingScreen(viewModel = viewModel, uiState = uiState)
                                AppScreen.LOGIN -> LoginScreen(viewModel = viewModel)
                                AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel, uiState = uiState)
                                AppScreen.CURHAT_SCAN -> CurhatScanScreen(viewModel = viewModel, uiState = uiState)
                                AppScreen.GAME_STRESS -> GameStressScreen(viewModel = viewModel, uiState = uiState)
                                AppScreen.DIRECTORY -> DirectoryScreen(viewModel = viewModel, uiState = uiState)
                                AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel, uiState = uiState)
                            }
                        }
                    }
                }
            }
        }
    }
}
