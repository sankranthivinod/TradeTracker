package com.sankranthivinod.tradetracker.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Dashboard", Icons.Default.Home)
    object Trends : Screen("trends", "Trends", Icons.AutoMirrored.Filled.ShowChart)
    object History : Screen("history", "History", Icons.AutoMirrored.Filled.ListAlt)
    object Admin : Screen("admin", "Admin Rules", Icons.Default.AdminPanelSettings)
}
