package org.example.project.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    Log.d("OnboardingScreen", "Rendering OnboardingScreen")

    // State for current page
    var currentPage by remember { mutableStateOf(0) }

    // Data untuk setiap halaman onboarding
    val onboardingData = listOf(
        Triple(
            R.drawable.onboarding1,
            "Selamat Datang di Srikandi App",
            "Aplikasi ini akan membantu Anda dalam berbagai kebutuhan sehari-hari."
        ),
        Triple(
            R.drawable.onboarding2,
            "Fitur Lengkap",
            "Nikmati berbagai fitur menarik yang dirancang untuk memudahkan hidup Anda."
        ),
        Triple(
            R.drawable.onboarding3,
            "Mulai Sekarang",
            "Ayo mulai perjalanan Anda bersama Srikandi App!"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header dengan tombol Back dan Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol Back (hanya muncul jika bukan halaman pertama)
            if (currentPage > 0) {
                TextButton(
                    onClick = {
                        currentPage -= 1
                    }
                ) {
                    Text(
                        text = "Back",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp)) // Placeholder
            }

            // Tombol Skip (hanya tulisan tanpa kotak)
            TextButton(
                onClick = { onFinish() }
            ) {
                Text(
                    text = "Skip",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Konten utama dengan animasi
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                if (targetState > initialState) {
                    // Forward navigation (next) - slide from right to left
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Slide in from right
                        animationSpec = tween(durationMillis = 500) // Slower animation (500ms)
                    ) with slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth }, // Slide out to left
                        animationSpec = tween(durationMillis = 500) // Slower animation (500ms)
                    )
                } else {
                    // Backward navigation (back) - slide from left to right
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth }, // Slide in from left
                        animationSpec = tween(durationMillis = 500) // Slower animation (500ms)
                    ) with slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Slide out to right
                        animationSpec = tween(durationMillis = 500) // Slower animation (500ms)
                    )
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            // Content for current page
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val imageId = onboardingData[page].first
                Log.d("OnboardingScreen", "Loading image: $imageId for page: $page")

                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = "Onboarding Image",
                    modifier = Modifier
                        .size(500.dp)
                        .padding(18.dp)
                )

                Text(
                    text = onboardingData[page].second,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = onboardingData[page].third,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Indikator navigasi (titik-titik)
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in onboardingData.indices) {
                val isSelected = i == currentPage
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (isSelected) 12.dp else 8.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Gray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }

        // Tombol Lanjutkan/Selesai
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (currentPage < onboardingData.size - 1) {
                        currentPage += 1
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .height(56.dp)
                    .width(if (currentPage == onboardingData.size - 1) 160.dp else 120.dp),
                shape = RoundedCornerShape(28.dp)  // Make it oval
            ) {
                if (currentPage == onboardingData.size - 1) {
                    // Teks "Selesai" untuk halaman terakhir
                    Text(
                        text = "Selesai",
                        fontSize = 16.sp
                    )
                } else {
                    // Icon panah ke kanan untuk halaman lainnya dengan teks
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Lanjut",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Next"
                        )
                    }
                }
            }
        }
    }
}