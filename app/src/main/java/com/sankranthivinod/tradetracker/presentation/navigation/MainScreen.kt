package com.sankranthivinod.tradetracker.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sankranthivinod.tradetracker.presentation.TradeViewModel
import com.sankranthivinod.tradetracker.presentation.components.AboutDialog
import com.sankranthivinod.tradetracker.presentation.components.AdminPasswordDialog
import com.sankranthivinod.tradetracker.presentation.components.ClearConfirmDialog
import com.sankranthivinod.tradetracker.presentation.screens.AdminScreen
import com.sankranthivinod.tradetracker.presentation.screens.AnalyticsScreen
import com.sankranthivinod.tradetracker.presentation.screens.HistoryScreen
import com.sankranthivinod.tradetracker.presentation.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TradeViewModel
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val screens = remember { listOf(Screen.Home, Screen.Trends, Screen.History) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (currentRoute) {
                        Screen.Home.route -> "Dashboard"
                        Screen.Trends.route -> "Trends & Analytics"
                        Screen.History.route -> "Trade History"
                        Screen.Admin.route -> "Admin Risk Rules"
                        else -> "Trade Tracker"
                    }
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Overflow Menu",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Admin Rules (Password)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                if (uiState.isAdminAuthenticated) {
                                    navController.navigate(Screen.Admin.route)
                                } else {
                                    viewModel.setShowAdminPasswordDialog(true)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About App") },
                            onClick = {
                                menuExpanded = false
                                viewModel.setShowAboutDialog(true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export Data (CSV)") },
                            onClick = {
                                menuExpanded = false
                                viewModel.exportCsv(context)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Clear All Trades",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                viewModel.setShowClearConfirmDialog(true)
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                screens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(uiState = uiState, viewModel = viewModel)
            }
            composable(Screen.Trends.route) {
                AnalyticsScreen(uiState = uiState, viewModel = viewModel)
            }
            composable(Screen.History.route) {
                HistoryScreen(uiState = uiState, viewModel = viewModel)
            }
            composable(Screen.Admin.route) {
                AdminScreen(uiState = uiState, viewModel = viewModel)
            }
        }
    }

    // Dialogs
    if (uiState.showAdminPasswordDialog) {
        AdminPasswordDialog(
            uiState = uiState,
            viewModel = viewModel,
            onSuccess = {
                navController.navigate(Screen.Admin.route)
            },
            onDismiss = {
                viewModel.setShowAdminPasswordDialog(false)
            }
        )
    }

    if (uiState.showAboutDialog) {
        AboutDialog(onDismiss = { viewModel.setShowAboutDialog(false) })
    }

    if (uiState.showClearConfirmDialog) {
        ClearConfirmDialog(
            onConfirm = { viewModel.clearAllTrades() },
            onDismiss = { viewModel.setShowClearConfirmDialog(false) }
        )
    }
}
