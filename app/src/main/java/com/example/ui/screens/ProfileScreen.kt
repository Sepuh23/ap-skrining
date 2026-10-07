package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CrisisHotlineBanner
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PsyVibeUiState
import com.example.viewmodel.PsyVibeViewModel

@Composable
fun ProfileScreen(
    viewModel: PsyVibeViewModel,
    uiState: PsyVibeUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var biometricScanEnabled by remember { mutableStateOf(true) }
    var audioProsodyEnabled by remember { mutableStateOf(true) }
    var anonymousDataVaultEnabled by remember { mutableStateOf(true) }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PsyBackground)
            .statusBarsPadding()
            .padding(bottom = 64.dp)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profil & Pengaturan Privasi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PsyOnSurface
            )
            IconButton(onClick = { viewModel.navigateTo(AppScreen.LANDING) }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Tentang",
                    tint = PsyPrimary
                )
            }
        }

        // Profile Identity Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.size(72.dp)) {
                    Surface(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        shape = CircleShape,
                        color = PsyPrimaryContainer.copy(alpha = 0.2f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.student_avatar_1791383103038),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(PsyPrimaryContainer, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }

                Text(
                    text = uiState.userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PsyOnSurface
                )

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = PsySurfaceContainerLow
                ) {
                    Text(
                        text = if (uiState.isAnonymousGuest) "🔒 Mode Tamu 100% Anonim" else "🎒 ${uiState.userSchool} • ${uiState.userNisn}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PsyPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Real Hardware Sensors Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Status Sensor Perangkat HP Real",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PsyOnSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sensor Langkah (Pedometer HP)", fontSize = 12.sp, color = PsyOnSurfaceVariant)
                    Text("${uiState.realDeviceSteps} langkah (Aktif)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyGreenSafe)
                }
                HorizontalDivider(color = PsySurfaceContainerHighest)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sensor Lokasi GPS", fontSize = 12.sp, color = PsyOnSurfaceVariant)
                    Text("Satelit GPS Aktif", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyGreenSafe)
                }
                HorizontalDivider(color = PsySurfaceContainerHighest)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kamera Depan FACS & Mic", fontSize = 12.sp, color = PsyOnSurfaceVariant)
                    Text("MediaPipe Active", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
                }
            }
        }

        // Biometric & Sensor Privacy Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Pengaturan Privasi Sensor",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PsyOnSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MediaPipe Scan Lensa Wajah",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PsyOnSurface
                        )
                        Text(
                            text = "Deteksi mikro-ekspresi & senyum palsu",
                            fontSize = 11.sp,
                            color = PsyOnSurfaceVariant
                        )
                    }
                    Switch(
                        checked = biometricScanEnabled,
                        onCheckedChange = { biometricScanEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PsyPrimary, checkedTrackColor = PsyPrimaryContainer)
                    )
                }

                HorizontalDivider(color = PsySurfaceContainerHighest)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Analisis Nada & Prosodi Suara",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PsyOnSurface
                        )
                        Text(
                            text = "Deteksi biomarker stres via intonasi",
                            fontSize = 11.sp,
                            color = PsyOnSurfaceVariant
                        )
                    }
                    Switch(
                        checked = audioProsodyEnabled,
                        onCheckedChange = { audioProsodyEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PsyPrimary, checkedTrackColor = PsyPrimaryContainer)
                    )
                }

                HorizontalDivider(color = PsySurfaceContainerHighest)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enkripsi Data Room Database",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PsyOnSurface
                        )
                        Text(
                            text = "Penyimpanan lokal di memori HP",
                            fontSize = 11.sp,
                            color = PsyOnSurfaceVariant
                        )
                    }
                    Switch(
                        checked = anonymousDataVaultEnabled,
                        onCheckedChange = { anonymousDataVaultEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PsyPrimary, checkedTrackColor = PsyPrimaryContainer)
                    )
                }
            }
        }

        // Reset Data Button (Zero Baseline)
        Button(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PsySurfaceContainerHigh)
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = PsyPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset Data & Grafik ke 0 (Murni HP)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
        }

        // Logout or Switch Mode Button
        OutlinedButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PsyError)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = PsyError,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Keluar / Ganti Akun",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = PsyError
            )
        }

        CrisisHotlineBanner()
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Semua Data ke 0?", fontWeight = FontWeight.Bold) },
            text = { Text("Semua riwayat mood di database HP dan langkah akan di-reset murni ke 0.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllDataToZero()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PsyPrimary)
                ) {
                    Text("Ya, Reset ke 0", color = PsyOnPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
