package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.SuggestedActionType
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.CameraPreviewView
import com.example.ui.components.FacialMeshOverlay
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PsyVibeUiState
import com.example.viewmodel.PsyVibeViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CurhatScanScreen(
    viewModel: PsyVibeViewModel,
    uiState: PsyVibeUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val topicScrollState = rememberScrollState()
    val context = LocalContext.current
    val telemetry = uiState.liveTelemetry

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasCameraPermission = perms[Manifest.permission.CAMERA] ?: false
        hasAudioPermission = perms[Manifest.permission.RECORD_AUDIO] ?: false
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasAudioPermission) {
            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Mic Pulse Ring Animations
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val ringScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "r1"
    )
    val ringScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "r2"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PsyBackground)
            .statusBarsPadding()
            .padding(bottom = 60.dp)
    ) {
        // Top Header matching exact screenshot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
                Column {
                    Text(
                        text = "Live Curhat & Scan Muka",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnSurface
                    )
                    Text(
                        text = "● Serene Vision • Dual-Sensor Active",
                        fontSize = 11.sp,
                        color = PsyGreenSafe,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = { viewModel.toggleCamera() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(PsySurfaceContainerLow, CircleShape)
                ) {
                    Icon(
                        imageVector = if (uiState.isCameraActive) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Kamera",
                        tint = PsyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PsyPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = PsyOnPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Pill: Continuous Real-time ... 🔒 Biometrik Terenkripsi E2E
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp),
                color = PsySurfaceContainerLow,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(PsyPrimaryContainer, CircleShape)
                        )
                        Text(
                            text = "Continuous Real-time FACS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PsyOnSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = PsyPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Biometrik Terenkripsi E2E",
                            fontSize = 11.sp,
                            color = PsyOnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Camera Viewfinder Box with Real CameraX and Permission Fallback
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                CameraPreviewView(
                    telemetry = telemetry,
                    hasCameraPermission = hasCameraPermission,
                    onRequestPermission = {
                        permissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Top Viewfinder Badges
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = PsySurfaceContainerLowest.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(PsyError, CircleShape)
                            )
                            Text(
                                text = "LIVE • 29 FPS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PsyOnSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = PsyPrimaryContainer.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.AllInclusive, contentDescription = null, tint = PsyOnPrimary, modifier = Modifier.size(12.dp))
                            Text(
                                text = "468 FACS Mesh Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PsyOnPrimary
                            )
                        }
                    }
                }

                // Bottom Mimik Detection Pill
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(100.dp),
                    color = PsySurfaceContainerLowest.copy(alpha = 0.92f),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(PsyYellowWarning, CircleShape)
                        )
                        Text(
                            text = "Mimik Terdeteksi:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PsyOnSurface
                        )
                        Text(
                            text = telemetry.facialStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyPrimary
                        )
                    }
                }
            }

            // Dual Action Buttons below Camera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.toggleCamera() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PsyPrimary)
                ) {
                    Icon(
                        imageVector = if (uiState.isCameraActive) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isCameraActive) "Matikan Kamera" else "Nyalakan Kamera",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { viewModel.scanFaceNow() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                ) {
                    if (uiState.isScanningFaceNow) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PsyOnPrimary, strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = PsyOnPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pindai Wajah Sekarang",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnPrimary
                        )
                    }
                }
            }

            // Parameter Biometrik FACS (2x2 Grid)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueryStats,
                                contentDescription = null,
                                tint = PsyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Parameter Biometrik FACS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PsyOnSurface
                                )
                                Text(
                                    text = "Live Action Units & Affective Telemetry",
                                    fontSize = 10.sp,
                                    color = PsyOnSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = PsyGreenSafe.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(5.dp).background(PsyGreenSafe, CircleShape))
                                Text(
                                    text = "Sinkron",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PsyGreenSafe
                                )
                            }
                        }
                    }

                    // 2x2 Parameter Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric 1: Micro-Tension
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = PsySurfaceContainerLow
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Micro-Tension", fontSize = 11.sp, color = PsyOnSurfaceVariant)
                                    Text("${telemetry.microTensionIndex}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
                                }
                                LinearProgressIndicator(
                                    progress = { (telemetry.microTensionIndex / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(100.dp)),
                                    color = PsyPrimaryContainer,
                                    trackColor = PsySurfaceContainerHighest
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PsyGreenSafe, modifier = Modifier.size(12.dp))
                                    Text(telemetry.microTensionLabel, fontSize = 10.sp, color = PsyOnSurfaceVariant)
                                }
                            }
                        }

                        // Metric 2: Eye-Blink Rhythm
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = PsySurfaceContainerLow
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Eye-Blink Rhythm", fontSize = 11.sp, color = PsyOnSurfaceVariant)
                                    Text("${telemetry.eyeBlinkRhythmRate}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
                                }
                                LinearProgressIndicator(
                                    progress = { (telemetry.eyeBlinkRhythmRate / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(100.dp)),
                                    color = PsyPrimaryContainer,
                                    trackColor = PsySurfaceContainerHighest
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Waves, contentDescription = null, tint = PsyPrimary, modifier = Modifier.size(12.dp))
                                    Text(telemetry.blinkRhythmLabel, fontSize = 10.sp, color = PsyOnSurfaceVariant)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric 3: AU4 Kerutan Dahi
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = PsySurfaceContainerLow
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("AU4 Kerutan Dahi", fontSize = 11.sp, color = PsyOnSurfaceVariant)
                                    Text("${telemetry.au4ForeheadWrinkle}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
                                }
                                LinearProgressIndicator(
                                    progress = { (telemetry.au4ForeheadWrinkle / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(100.dp)),
                                    color = PsyPrimaryContainer,
                                    trackColor = PsySurfaceContainerHighest
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.SentimentSatisfied, contentDescription = null, tint = PsyGreenSafe, modifier = Modifier.size(12.dp))
                                    Text(telemetry.au4StatusLabel, fontSize = 10.sp, color = PsyOnSurfaceVariant)
                                }
                            }
                        }

                        // Metric 4: AU12 Otot Senyum
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = PsySurfaceContainerLow
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("AU12 Otot Senyu...", fontSize = 11.sp, color = PsyOnSurfaceVariant)
                                    Text("${telemetry.au12SmileMuscle}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
                                }
                                LinearProgressIndicator(
                                    progress = { (telemetry.au12SmileMuscle / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(100.dp)),
                                    color = PsyPrimaryContainer,
                                    trackColor = PsySurfaceContainerHighest
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = PsyYellowWarning, modifier = Modifier.size(12.dp))
                                    Text(telemetry.au12StatusLabel, fontSize = 10.sp, color = PsyOnSurfaceVariant)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.sendCurhatMessage("Berikut hasil scan biometrikku hari ini: AU4 ${telemetry.au4ForeheadWrinkle}%, AU12 ${telemetry.au12SmileMuscle}%.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PsyPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = PsyOnPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Curhat dengan Hasil Ini", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PsyOnPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = PsyOnPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Audio Prosody & Spektrum Suara Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = PsyPrimary, modifier = Modifier.size(18.dp))
                            Text("Audio Prosody & Spektrum Suara", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyOnSurface)
                        }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = PsyPrimaryContainer.copy(alpha = 0.2f)
                        ) {
                            Text("● Live Frequency", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PsyPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    AudioWaveformVisualizer(isRecording = uiState.isRecordingSpeech)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pitch Intonation: ${telemetry.pitchIntonation}", fontSize = 11.sp, color = PsyOnSurfaceVariant)
                        Text("Jeda Nada: ${telemetry.pitchPauseSeconds}s (Reflektif)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = PsyPrimary)
                    }
                }
            }

            // VibeBot Empathy AI Dialogue Card
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
                    uiState.chatMessages.forEach { msg ->
                        ChatBubbleItem(
                            message = msg,
                            onActionClick = { action ->
                                when (action) {
                                    SuggestedActionType.MINI_GAME_STRESS_RELIEF -> viewModel.navigateTo(AppScreen.GAME_STRESS)
                                    SuggestedActionType.GURU_BK_CONSULTATION -> viewModel.navigateTo(AppScreen.DIRECTORY)
                                    SuggestedActionType.SEJIWA_HOTLINE_119 -> {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
                                        context.startActivity(dialIntent)
                                    }
                                    SuggestedActionType.JOURNAL_PROMPT -> viewModel.navigateTo(AppScreen.DASHBOARD)
                                    SuggestedActionType.BREATHING_EXERCISE -> viewModel.navigateTo(AppScreen.DASHBOARD)
                                }
                            }
                        )
                    }

                    if (uiState.isAiTyping) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PsyPrimaryContainer, strokeWidth = 2.dp)
                            Text("VibeBot sedang menyimak...", fontSize = 11.sp, color = PsyOnSurfaceVariant)
                        }
                    }

                    // Quick Chips matching exact screenshot
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(topicScrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val topics = listOf(
                            "Lagi stress tugas • 📚",
                            "Capek mental • 🌧️",
                            "Overthinking • 💭",
                            "Pura-pura senyum 🙂"
                        )
                        topics.forEach { topic ->
                            Surface(
                                modifier = Modifier.clickable {
                                    viewModel.onCurhatInputChanged(topic)
                                    viewModel.sendCurhatMessage(topic)
                                },
                                shape = RoundedCornerShape(100.dp),
                                color = PsySurfaceContainerLowest,
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = topic,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PsyOnSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Glowing Blue Mic Hold-to-Speak Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isRecordingSpeech) {
                        Box(
                            modifier = Modifier
                                .size((80 * ringScale1).dp)
                                .background(PsyPrimaryContainer.copy(alpha = 0.25f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size((70 * ringScale2).dp)
                                .background(PsyPrimaryContainer.copy(alpha = 0.4f), CircleShape)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                if (uiState.isRecordingSpeech) PsyPrimary else PsyPrimaryContainer,
                                CircleShape
                            )
                            .pointerInteropFilter { motionEvent ->
                                when (motionEvent.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        viewModel.startHoldToSpeak()
                                        true
                                    }
                                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                        viewModel.stopHoldToSpeak()
                                        true
                                    }
                                    else -> false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Bicara",
                            tint = PsyOnPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Text(
                    text = if (uiState.isRecordingSpeech) "Merekam suaramu... Lepas untuk mengirim" else "Tekan & tahan untuk bicara prosody...",
                    fontSize = 12.sp,
                    color = if (uiState.isRecordingSpeech) PsyError else PsyOnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom Text Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp),
                color = PsySurfaceContainerLowest,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onCurhatInputChanged("Hari ini aku merasa ") }) {
                        Icon(
                            imageVector = Icons.Default.SentimentSatisfied,
                            contentDescription = "Emoji",
                            tint = PsyOnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    TextField(
                        value = uiState.curhatInputText,
                        onValueChange = { viewModel.onCurhatInputChanged(it) },
                        placeholder = { Text("Ketik curhatanmu di sini...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    IconButton(
                        onClick = { viewModel.sendCurhatMessage() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(PsyPrimary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Kirim",
                            tint = PsyOnPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    onActionClick: (SuggestedActionType) -> Unit
) {
    if (message.sender == MessageSender.USER) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp).copy(bottomEnd = androidx.compose.foundation.shape.CornerSize(2.dp)),
                color = PsyPrimaryContainer,
                shadowElevation = 1.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = PsyOnPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PsySurfaceContainerLowest, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "VibeBot",
                    tint = PsyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(18.dp).copy(topStart = androidx.compose.foundation.shape.CornerSize(2.dp)),
                color = PsySurfaceContainerLowest,
                shadowElevation = 1.dp,
                modifier = Modifier.widthIn(max = 300.dp)
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
                        Text(
                            text = "VibeBot Empathy AI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        Text(
                            text = "● Menyimakmu",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PsyPrimary
                        )
                    }

                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = PsyOnSurface,
                        lineHeight = 19.sp
                    )

                    if (message.suggestedAction != null) {
                        Button(
                            onClick = { onActionClick(message.suggestedAction) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            val label = when (message.suggestedAction) {
                                SuggestedActionType.MINI_GAME_STRESS_RELIEF -> "🎮 Main Game Pereda Stres (XOX / Tetris)"
                                SuggestedActionType.GURU_BK_CONSULTATION -> "🏫 Hubungi Guru BK / Psikolog"
                                SuggestedActionType.SEJIWA_HOTLINE_119 -> "📞 Hubungi Hotline Sejiwa 119"
                                SuggestedActionType.JOURNAL_PROMPT -> "📖 Buka Jurnal Refleksi"
                                SuggestedActionType.BREATHING_EXERCISE -> "🎧 Putar Audio Relaksasi"
                            }
                            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PsyOnPrimary)
                        }
                    }
                }
            }
        }
    }
}
