package org.example.project.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomBarDestination(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val title: String? = null,
    val isCenter: Boolean = false
) {
    HOME("dashboard_user", Icons.Default.Home, Icons.Filled.Home, "Home"),
    CALENDAR("under_development", Icons.Default.DateRange, Icons.Filled.DateRange, "Kalendar"),
    ADD("kirim-laporan", Icons.Default.Add, null, null, isCenter = true),
    LIST("under_development", Icons.Default.List, Icons.Filled.List, "Artikel"),
    KETUA("dashboard_ketua", Icons.Default.Person, Icons.Filled.Person, "Ketua")
}