package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.components.CrisisHotlineBanner
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PsyVibeUiState
import com.example.viewmodel.PsyVibeViewModel

@Composable
fun LandingScreen(
    viewModel: PsyVibeViewModel,
    uiState: PsyVibeUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PsyBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: PSY-VIBE Logo + Menu + Masuk & Daftar Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(PsyPrimaryContainer.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "PSY-VIBE Logo",
                        tint = PsyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "PSY",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnSurface
                    )
                    Text(
                        text = "-VIBE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PsyPrimaryContainer
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = { viewModel.openLoginScreen() }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = PsyOnSurface)
                }
                OutlinedButton(
                    onClick = { viewModel.openLoginScreen() },
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PsyPrimary)
                ) {
                    Text("Masuk", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = { viewModel.openRegistrationScreen() },
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                ) {
                    Text("Daftar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyOnPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Interactive Mascot Aura
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size((170 * auraScale).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PsyPrimaryContainer.copy(alpha = 0.35f),
                                PsySurfaceContainerHigh.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )

            Surface(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .border(3.dp, PsySurfaceContainerLowest, CircleShape),
                shape = CircleShape,
                shadowElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vibebot_mascot_1791383044160),
                    contentDescription = "VibeBot Companion",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 8.dp),
                shape = RoundedCornerShape(100.dp),
                color = PsySurfaceContainerLowest,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = PsyPrimaryContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "VibeBot Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tagline Pill
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = PsySurfaceContainerLow
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "✨", fontSize = 12.sp)
                Text(
                    text = "Youth Sanctuary • Ruang Aman Remaja",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PsyPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Headline
        Text(
            text = "Ruang Aman Curhat AI &\nSkrining Emosi Tanpa Stigma",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PsyOnSurface,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Bukan sekadar chat biasa. AI membaca ekspresi wajah, nada suara, dan tingkat stresmu secara objektif & 100% rahasia.",
            fontSize = 13.sp,
            color = PsyOnSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Card: ⚡ Coba Quick Tes Ekspresi
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLow),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = PsySurfaceContainerHighest
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(PsyPrimaryContainer, CircleShape)
                            )
                            Text(
                                text = "Scan Lensa Instan • Tanpa Daftar",
                                fontSize = 11.sp,
                                color = PsyOnTertiaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Private",
                            tint = PsyPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Private",
                            fontSize = 11.sp,
                            color = PsyOnSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.teen_scan_feed_1791383080614),
                            contentDescription = "Face scan preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PsyPrimaryContainer.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCameraFront,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ Coba Quick Tes Ekspresi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        Text(
                            text = "Deteksi Smiling Depression & micro-stress dalam 5 detik.",
                            fontSize = 12.sp,
                            color = PsyOnSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.startQuickTest() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PsySurfaceContainerLowest)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = PsyPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Coba Skrining Langsung",
                        color = PsyPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = PsyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main CTA
        Button(
            onClick = { viewModel.navigateTo(AppScreen.CURHAT_SCAN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer),
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Text(
                text = "Mulai Skrining Sekarang",
                color = PsyOnPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = PsyOnPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { viewModel.navigateTo(AppScreen.LOGIN) }
        ) {
            Text(
                text = "Sudah Punya Akun? ",
                color = PsyOnSurfaceVariant,
                fontSize = 13.sp
            )
            Text(
                text = "Masuk Di Sini",
                color = PsyPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Trust Badges Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = PsySurfaceContainerLowest,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TrustBadgeItem(
                    icon = Icons.Default.Lock,
                    title = "100% Anonim",
                    subtitle = "Data disamarkan"
                )
                TrustBadgeItem(
                    icon = Icons.Default.Bolt,
                    title = "Skrining Cepat",
                    subtitle = "Hasil 3 menit"
                )
                TrustBadgeItem(
                    icon = Icons.Default.Security,
                    title = "E2E Terenkripsi",
                    subtitle = "Privasi mutlak"
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2-Column Bottom Cards: Vibe Arena & Faskes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { viewModel.navigateTo(AppScreen.GAME_STRESS) },
                shape = RoundedCornerShape(20.dp),
                color = PsySurfaceContainerLowest,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PsyPrimaryContainer.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = PsyPrimary, modifier = Modifier.size(20.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = PsyPrimaryContainer.copy(alpha = 0.2f)
                        ) {
                            Text("Level Up", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PsyPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Text("Vibe Arena", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PsyOnSurface)
                    Text("Game relaksasi XOX & Tetris...", fontSize = 11.sp, color = PsyOnSurfaceVariant)
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { viewModel.navigateTo(AppScreen.DIRECTORY) },
                shape = RoundedCornerShape(20.dp),
                color = PsySurfaceContainerLowest,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PsyTertiaryContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = PsyPrimary, modifier = Modifier.size(20.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = PsyGreenSafe.copy(alpha = 0.15f)
                        ) {
                            Text("Terdekat", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PsyGreenSafe, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Text("Faskes & Psikolog", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PsyOnSurface)
                    Text("Rujukan resmi ramah remaja...", fontSize = 11.sp, color = PsyOnSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Crisis Alert Banner
        CrisisHotlineBanner()

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Modal Dialog: 5-Second Expression Test
    if (uiState.showQuickTestModal) {
        Dialog(onDismissRequest = { viewModel.dismissQuickTestModal() }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = PsySurfaceContainerLowest,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ Quick Tes Ekspresi 5 Detik",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        IconButton(onClick = { viewModel.dismissQuickTestModal() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    if (uiState.isQuickTesting) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(20.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.teen_scan_feed_1791383080614),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            CircularProgressIndicator(
                                progress = { uiState.quickTestProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = PsyPrimaryContainer,
                                strokeWidth = 6.dp
                            )
                        }
                        Text(
                            text = "Menganalisis AU4 Dahi & AU12 Otot Senyum...",
                            fontSize = 13.sp,
                            color = PsyOnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        LinearProgressIndicator(
                            progress = { uiState.quickTestProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = PsyPrimaryContainer
                        )
                    } else if (uiState.quickTestResult != null) {
                        val res = uiState.quickTestResult
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PsySurfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = PsyPrimary
                                    )
                                    Text(
                                        text = res.statusSummary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = PsyOnSurface
                                    )
                                }
                                Text(
                                    text = res.recommendation,
                                    fontSize = 12.sp,
                                    color = PsyOnSurfaceVariant,
                                    lineHeight = 17.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "AU4 Dahi: ${res.tensionScore}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PsySecondary
                                    )
                                    Text(
                                        text = "AU12 Senyum: ${res.smileAuthenticity}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PsyPrimary
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.dismissQuickTestModal()
                                viewModel.navigateTo(AppScreen.CURHAT_SCAN)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                        ) {
                            Text(
                                text = "Lanjut Curhat di Ruang AI",
                                fontWeight = FontWeight.Bold,
                                color = PsyOnPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrustBadgeItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .background(PsySurfaceContainerLow, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PsyPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PsyOnSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            fontSize = 9.sp,
            color = PsyOnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
