package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun DashboardScreen(
    viewModel: PsyVibeViewModel,
    uiState: PsyVibeUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val moodScrollState = rememberScrollState()
    val context = LocalContext.current
    var showRelaxModal by remember { mutableStateOf(false) }
    var showJournalModal by remember { mutableStateOf(false) }
    var journalText by remember { mutableStateOf("") }
    var customMoodChoice by remember { mutableStateOf("Tenang") }

    val targetSteps = 8000
    val stepProgress = (uiState.realDeviceSteps.toFloat() / targetSteps).coerceIn(0f, 1f)
    val screenMinutes = uiState.realActiveMinutes
    val screenHoursStr = "${screenMinutes / 60}j ${screenMinutes % 60}m"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PsyBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Greeting & Live Status Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.size(50.dp)) {
                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        shape = CircleShape,
                        color = PsyPrimaryContainer.copy(alpha = 0.3f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.student_avatar_1791383103038),
                            contentDescription = "User profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(PsyPrimaryContainer, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }

                Column {
                    Text(
                        text = "Halo ${uiState.userName}! ⛅",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(PsyPrimaryContainer, CircleShape)
                        )
                        Text(
                            text = if (uiState.isAnonymousGuest) "Mode Tamu • Terenkripsi" else "${uiState.userSchool}",
                            fontSize = 11.sp,
                            color = PsyTertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(PsySurfaceContainerLow, CircleShape)
                    .clickable { viewModel.navigateTo(AppScreen.PROFILE) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    tint = PsyTertiary,
                    modifier = Modifier.size(22.dp)
                )
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(PsyPrimaryContainer, CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = (-6).dp, y = 6.dp)
                )
            }
        }

        // Mood Selector Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BAGAIMANA PERASAANMU SEKARANG?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PsyOnSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "+ Catat Jurnal",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PsyPrimary,
                    modifier = Modifier.clickable { showJournalModal = true }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(moodScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val moods = listOf(
                    "Senang" to "😊",
                    "Tenang" to "😌",
                    "Cemas" to "😟",
                    "Lelah" to "🥱",
                    "Sedih" to "🌧️"
                )
                moods.forEach { (name, emoji) ->
                    val isSelected = uiState.selectedMood == name
                    Surface(
                        modifier = Modifier.clickable { 
                            customMoodChoice = name
                            viewModel.selectMood(name) 
                        },
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) PsyPrimaryContainer else PsySurfaceContainerLow,
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = emoji, fontSize = 16.sp)
                            Text(
                                text = name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PsyOnPrimary else PsyOnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Featured Dual-Sensing Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(AppScreen.CURHAT_SCAN) },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLow),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PsyPrimaryContainer.copy(alpha = 0.25f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = PsySurfaceContainerLowest,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PsyPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "FITUR UNGGULAN AI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PsyPrimary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = PsySurfaceContainerLowest.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = "3 Menit Sesi",
                                fontSize = 11.sp,
                                color = PsyOnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Ruang Curhat AI & Skrining Wajah/Suara",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        Text(
                            text = "Bicara bebas 3 menit. AI membaca nada suara dan mikro-ekspresimu secara aman, etis, dan rahasia.",
                            fontSize = 12.sp,
                            color = PsyOnSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Live Telemetry Snippets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = PsySurfaceContainerLowest.copy(alpha = 0.95f),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(PsySurfaceContainerHigh, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = PsyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(text = "Audio Tone", fontSize = 10.sp, color = PsySecondary)
                                    Text(
                                        text = "Tenang ${uiState.liveTelemetry.pitchCadence}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PsyOnSurface
                                    )
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = PsySurfaceContainerLowest.copy(alpha = 0.95f),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(PsySurfaceContainerHigh, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = null,
                                        tint = PsyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(text = "Mimik Wajah", fontSize = 10.sp, color = PsySecondary)
                                    Text(
                                        text = if (uiState.liveTelemetry.microTensionIndex > 30) "Sedikit Cemas" else "Sedikit Lelah",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PsyOnSurface
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.CURHAT_SCAN) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                    ) {
                        Text(
                            text = "Mulai Curhat Sekarang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PsyOnPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = PsyOnPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Digital Wellbeing & Real Device Hardware Activity Widget
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLow),
            elevation = CardDefaults.cardElevation(1.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonitorHeart,
                            contentDescription = null,
                            tint = PsyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Kesehatan Digital Perangkat",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = PsySurfaceContainerHighest
                    ) {
                        Text(
                            text = "Sensor HP Real-Time",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = PsyPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Metric 1: Real Screen Time
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = PsySurfaceContainerLowest,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Smartphone,
                                    contentDescription = null,
                                    tint = PsyTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Layar Aktif 🟢",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PsyOnSurface
                                )
                            }
                            Text(
                                text = screenHoursStr,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PsyOnSurface
                            )
                            Text(
                                text = "Waktu Sesi Hari Ini",
                                fontSize = 10.sp,
                                color = PsyOnSurfaceVariant
                            )
                            LinearProgressIndicator(
                                progress = { (screenMinutes / 240f).coerceIn(0.05f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = PsyPrimaryContainer,
                                trackColor = PsySurfaceContainerHigh
                            )
                        }
                    }

                    // Metric 2: Real Hardware Pedometer Steps
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = PsySurfaceContainerLowest,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { stepProgress },
                                    modifier = Modifier.fillMaxSize(),
                                    color = PsyPrimaryContainer,
                                    trackColor = PsySurfaceContainerHigh,
                                    strokeWidth = 4.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                Text(
                                    text = "${(stepProgress * 100).toInt()}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PsyOnSurface
                                )
                            }

                            Column {
                                Text(
                                    text = "${uiState.realDeviceSteps}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PsyOnSurface
                                )
                                Text(
                                    text = "/ 8.000 langkah",
                                    fontSize = 10.sp,
                                    color = PsyOnSurfaceVariant
                                )
                                Text(
                                    text = if (uiState.realDeviceSteps == 0) "Mulai Berjalan" else "Langkah HP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PsyPrimary
                                )
                            }
                        }
                    }
                }

                // AI Tip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = PsySurfaceContainerHighest
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "💡", fontSize = 14.sp)
                        Text(
                            text = "Tip AI: Gerakkan badan 5-10 menit saat belajar untuk melancarkan sirkulasi dan meredakan ketegangan mata.",
                            fontSize = 11.sp,
                            color = PsyOnSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Real Room DB Mood History List / Graph Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = PsyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Riwayat & Grafik Emosi Real",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                    }
                    Text(
                        text = "${uiState.moodHistory.size} Catatan",
                        fontSize = 11.sp,
                        color = PsyOnSurfaceVariant
                    )
                }

                if (uiState.moodHistory.isEmpty()) {
                    // Clean Initial Zero State
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = PsySurfaceContainerLow
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🌱", fontSize = 24.sp)
                            Text(
                                text = "Belum Ada Catatan Emosi (Murni 0)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PsyOnSurface
                            )
                            Text(
                                text = "Pilih salah satu mood di atas atau klik '+ Catat Jurnal' untuk merekam perasaan pertamamu.",
                                fontSize = 11.sp,
                                color = PsyOnSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                    }
                } else {
                    // List of real user recorded moods
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.moodHistory.take(4).forEach { entry ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = PsySurfaceContainerLow
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(entry.emoji, fontSize = 20.sp)
                                        Column {
                                            Text(
                                                text = entry.moodName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PsyOnSurface
                                            )
                                            Text(
                                                text = entry.note,
                                                fontSize = 11.sp,
                                                color = PsyOnSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    Text(
                                        text = entry.timestamp,
                                        fontSize = 10.sp,
                                        color = PsyTertiary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Core Solution Grid (2x2)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Solusi Kesejahteraan Mental",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PsyOnSurface
                )
                Text(
                    text = "Lengkap 24/7",
                    fontSize = 11.sp,
                    color = PsyOnSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SolutionCardItem(
                    icon = Icons.Default.Forum,
                    title = "Teman Ngobrol AI",
                    desc = "Empathetic Companion yang siap mendengar tanpa menghakimi.",
                    actionLabel = "Buka Obrolan",
                    onClick = { viewModel.navigateTo(AppScreen.CURHAT_SCAN) },
                    modifier = Modifier.weight(1f)
                )

                SolutionCardItem(
                    icon = Icons.Default.SupportAgent,
                    title = "Konsultasi Guru BK",
                    desc = "Jadwalkan konseling tatap muka atau chat privat sekolah.",
                    actionLabel = "Jadwal Terbuka",
                    onClick = { viewModel.navigateTo(AppScreen.DIRECTORY) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SolutionCardItem(
                    icon = Icons.Default.EditNote,
                    title = "Jurnal Mood Harian",
                    desc = "Catat momen kecil & rekam riwayat emosi di HP.",
                    actionLabel = "Isi Jurnal",
                    onClick = { showJournalModal = true },
                    modifier = Modifier.weight(1f)
                )

                SolutionCardItem(
                    icon = Icons.Default.Headphones,
                    title = "Audio Relaksasi",
                    desc = "Frekuensi binaural, ambient hujan, & latihan pernapasan.",
                    actionLabel = "Putar Musik",
                    onClick = { showRelaxModal = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Location-Based Healthcare Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(AppScreen.DIRECTORY) },
            shape = RoundedCornerShape(20.dp),
            color = PsySurfaceContainer,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(PsySurfaceContainerLowest, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PsyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Psikolog & Faskes Terdekat",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        Text(
                            text = "Dihitung dari GPS HP Real-Time (${uiState.currentGpsAddress})",
                            fontSize = 11.sp,
                            color = PsyOnSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(PsyPrimaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Rute",
                        tint = PsyOnPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Emergency SOS Banner
        CrisisHotlineBanner()

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Modal: Audio Relaksasi & Latihan Pernapasan
    if (showRelaxModal) {
        Dialog(onDismissRequest = { showRelaxModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PsySurfaceContainerLowest,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎧 Audio Relaksasi & Nafas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        IconButton(onClick = { showRelaxModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(PsyPrimaryContainer.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = PsyPrimary,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Text(
                        text = "Latihan Pernapasan 4-7-8",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnSurface
                    )
                    Text(
                        text = "Tarik nafas 4 detik, tahan 7 detik, hembuskan perlahan 8 detik untuk meredakan ketegangan otot wajah dan detak jantung.",
                        fontSize = 12.sp,
                        color = PsyOnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Button(
                        onClick = { showRelaxModal = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                    ) {
                        Text("Mulai Sesi Relaksasi 5 Menit", fontWeight = FontWeight.Bold, color = PsyOnPrimary)
                    }
                }
            }
        }
    }

    // Modal: Jurnal Mood Harian
    if (showJournalModal) {
        Dialog(onDismissRequest = { showJournalModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PsySurfaceContainerLowest,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📖 Catat Jurnal & Mood",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        IconButton(onClick = { showJournalModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    Text(
                        text = "Pilih emosi dan tuliskan apa yang kamu rasakan:",
                        fontSize = 12.sp,
                        color = PsyOnSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Senang" to "😊", "Tenang" to "😌", "Cemas" to "😟", "Lelah" to "🥱", "Sedih" to "🌧️").forEach { (name, emoji) ->
                            val isSel = customMoodChoice == name
                            Surface(
                                modifier = Modifier.clickable { customMoodChoice = name },
                                shape = CircleShape,
                                color = if (isSel) PsyPrimaryContainer else PsySurfaceContainerLow
                            ) {
                                Text(emoji, fontSize = 20.sp, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        placeholder = { Text("Contoh: Hari ini agak lelah belajar, tapi bangga sudah berusaha...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.selectMood(customMoodChoice, journalText)
                            showJournalModal = false
                            journalText = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                    ) {
                        Text("Simpan ke Database HP", fontWeight = FontWeight.Bold, color = PsyOnPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun SolutionCardItem(
    icon: ImageVector,
    title: String,
    desc: String,
    actionLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = PsySurfaceContainerLowest,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(PsySurfaceContainerLow, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = PsyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PsyOnSurface
                )
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = PsyOnSurfaceVariant,
                    lineHeight = 15.sp
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PsyPrimary
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = PsyPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
