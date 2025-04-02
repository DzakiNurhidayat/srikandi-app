package org.example.project.ui.screens.ketua

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.project.ui.components.CustomButton
import org.example.project.ui.theme.Divider
import org.example.project.ui.theme.TextVerifikasi
import org.example.project.utils.shadow

@Composable
fun VerifikasiScreen(id: Int, navController: NavHostController) {
    val textSize = 16.sp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Informasi Kejadian",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(BorderStroke(1.5.dp, color = Color.Black), shape = CircleShape)
                        .clickable { navController.popBackStack() }
                        .hoverable(remember { MutableInteractionSource() }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.Black,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            HorizontalDivider(thickness = 2.dp, color = Divider)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top // Ensure the Row aligns its children to the top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start, // Align Column content to the start
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text("Pelapor", color = TextVerifikasi, fontSize = textSize)
                    Text("NIM/NIP", color = TextVerifikasi, fontSize = textSize)
                    Text("No Telp", color = TextVerifikasi, fontSize = textSize)
                    Text("Jurusan", color = TextVerifikasi, fontSize = textSize)
                    Text("Status Pelapor", color = TextVerifikasi, fontSize = textSize)
                    Text("Tempat Kejadian", color = TextVerifikasi, fontSize = textSize)
                    Text("Waktu Kejadian", color = TextVerifikasi, fontSize = textSize)
                    Text("Bentuk Kekerasan", color = TextVerifikasi, fontSize = textSize)
                    Text("Deskripsi Kejadian", color = TextVerifikasi, fontSize = textSize)
                }
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start, // Align Column content to the start
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text("Pelapor", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                    Text("NIM/NIP", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                    Text("No Telp", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                    Text("Jurusan", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                    Text("Status Pelapor", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                    Text("Tempat Kejadian", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                    Text("Waktu Kejadian", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                    Text("Bentuk Kekerasan", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                }
            }
            TextField(
                value = "babi",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(2.dp, Divider, shape = MaterialTheme.shapes.small)
                    .background(Color.White, shape = MaterialTheme.shapes.small),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.Gray
                )
            )
            Text(
                "Bukti Pendukung",
                color = TextVerifikasi,
                fontSize = textSize,
                modifier = Modifier.padding(vertical = 15.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Clickable image
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Gray)
                        .clickable { /* Handle image click */ }
                        .hoverable(remember { MutableInteractionSource() }),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image", color = Color.White)
                }
                // Clickable video
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Gray)
                        .clickable { /* Handle video click */ }
                        .hoverable(remember { MutableInteractionSource() }),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Video", color = Color.White)
                }
                // Clickable mp3
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Gray)
                        .clickable { /* Handle mp3 click */ }
                        .hoverable(remember { MutableInteractionSource() }),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MP3", color = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(thickness = 2.dp, color = Divider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "Tolak",
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                contentColor = Color.Red
            )
            CustomButton(
                text = "Terima",
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        }
    }
}
