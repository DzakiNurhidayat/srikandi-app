package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.example.project.R
import org.example.project.ui.navigation.BottomBarDestination
import org.example.project.ui.theme.background

@Composable
fun TopNavigationBar(
    profilePhoto: Int = R.drawable.default_profile,
    userName: String = "Dzaki Nurhidayat"
) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .clip(CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = profilePhoto),
            contentDescription = "Profile Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )
        Text(
            text = "Hello, $userName!",
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_bell),
            contentDescription = "Notifications",
            tint = Color(0xFF666666),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun TopNavigationBarB(
    text: String,
    navController: NavHostController,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.navigateUp() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Start
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Delete",
                tint = Color.Red,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showDeleteDialog = true }
            )
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Hapus",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus laporan ini?",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick() // Panggil callback untuk menghapus
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Hapus",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        "Batal",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .height(54.dp)
            .shadow(8.dp)
    ) {
        BottomBarDestination.values().forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            if (screen.isCenter) {
                // Tombol ADD Tengah Custom
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1976D2))
                        .clickable {
                            if (!isSelected) {
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) screen.selectedIcon ?: screen.icon else screen.icon,
                            contentDescription = screen.title,
                            modifier = Modifier.size(20.dp),
                            tint = if (isSelected) Color(0xFF1976D2) else Color(0xFFAAAAAA)
                        )
                    },
                    label = screen.title?.let {
                        {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color(0xFF1976D2) else Color(0xFFAAAAAA)
                            )
                        }
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.surface,
                        selectedIconColor = Color(0xFFAAAAAA),
                        selectedTextColor = Color(0xFF1976D2)
                    )
                )
            }
        }
    }
}