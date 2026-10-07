package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.*

@Composable
fun GameStressScreen(
    viewModel: PsyVibeViewModel,
    uiState: PsyVibeUiState,
    modifier: Modifier = Modifier
) {
    val game = uiState.gameState

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PsyBackground)
            .statusBarsPadding()
            .padding(bottom = 64.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        text = "Vibe Arena • Pereda Stres",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnSurface
                    )
                    Text(
                        text = "Game Biofeedback Santai & Menenangkan",
                        fontSize = 11.sp,
                        color = PsyOnSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = PsyPrimaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PsyOnPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${game.score} Pts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnPrimary
                    )
                }
            }
        }

        // Stress Biomarker Indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(1.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = if (game.stressBiomarker > 35) PsyError else PsyGreenSafe,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Indeks Beban Mental & Stres",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PsyOnSurface
                        )
                    }
                    Text(
                        text = "${game.stressBiomarker}% (${if (game.stressBiomarker > 35) "Tegang" else "Rileks ✨"})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (game.stressBiomarker > 35) PsyError else PsyGreenSafe
                    )
                }

                LinearProgressIndicator(
                    progress = { (game.stressBiomarker / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    color = if (game.stressBiomarker > 35) PsyError else PsyGreenSafe,
                    trackColor = PsySurfaceContainerHighest
                )
            }
        }

        // Game Mode Tab Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val modes = listOf(
                StressGameMode.XOX_TICTACTOE to "❌⭕ XOX vs AI",
                StressGameMode.TETRIS_BLOCKS to "🧱 Tetris Calm",
                StressGameMode.VOICE_PITCH_JUMP to "🎤 Pitch Jump"
            )
            modes.forEach { (mode, label) ->
                val isSelected = game.activeMode == mode
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectGameMode(mode) },
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) PsyPrimaryContainer else PsySurfaceContainerLowest,
                    shadowElevation = if (isSelected) 2.dp else 1.dp
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PsyOnPrimary else PsyOnSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Active Game Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            when (game.activeMode) {
                StressGameMode.XOX_TICTACTOE -> XoxGameView(game.xoxState, onMove = { viewModel.playXoxMove(it) }, onReset = { viewModel.resetXoxGame() })
                StressGameMode.TETRIS_BLOCKS -> TetrisGameView(game.tetrisState, onClear = { viewModel.clearTetrisBlock() })
                StressGameMode.VOICE_PITCH_JUMP -> PitchJumpGameView(game, onSmile = { viewModel.triggerSmileBoost() }, onPitch = { viewModel.triggerVoicePitchJump() })
            }
        }
    }
}

@Composable
fun XoxGameView(
    state: XoxBoardState,
    onMove: (Int) -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Kamu (X)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
                Text("${state.scoreUser} Menang", fontSize = 11.sp, color = PsyOnSurfaceVariant)
            }
            Text("VS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PsyOnSurfaceVariant)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("VibeBot AI (O)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyPrimaryContainer)
                Text("${state.scoreAi} Menang", fontSize = 11.sp, color = PsyOnSurfaceVariant)
            }
        }

        // 3x3 Grid
        Column(
            modifier = Modifier.size(240.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        val value = state.board[index]
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(PsySurfaceContainerLow, RoundedCornerShape(16.dp))
                                .clickable(enabled = value.isEmpty() && state.winner == null && state.isUserTurn) {
                                    onMove(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = value,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (value == "X") PsyPrimary else PsyPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Status & Reset
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val statusText = when (state.winner) {
                "X" -> "🎉 Kamu Menang! Stres berkurang -15%"
                "O" -> "🤖 VibeBot AI Menang! Mau coba lagi?"
                "DRAW" -> "🤝 Seri! Pertandingan yang seru!"
                else -> if (state.isUserTurn) "Giliranmu (Tap kotak kosong)" else "VibeBot sedang berpikir..."
            }
            Text(text = statusText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onReset,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text("Reset Papan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PsyOnPrimary)
            }
        }
    }
}

@Composable
fun TetrisGameView(
    state: TetrisState,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🧱 Tetris Calm • Susun Balok", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PsyOnSurface)
            Text("Pecahkan baris balok warna-warni untuk melepaskan penat belajar.", fontSize = 11.sp, color = PsyOnSurfaceVariant, textAlign = TextAlign.Center)
        }

        // Simple Visual Tetris Matrix
        Surface(
            modifier = Modifier
                .size(200.dp, 160.dp),
            shape = RoundedCornerShape(16.dp),
            color = PsySurfaceContainerLow
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = Modifier.size(28.dp).background(PsyPrimaryContainer, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(PsyPrimaryContainer, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(PsySoftSkyBlue, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(Color.Transparent))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = Modifier.size(28.dp).background(PsyYellowWarning, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(PsyYellowWarning, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(PsyYellowWarning, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(PsyGreenSafe, RoundedCornerShape(6.dp)))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(modifier = Modifier.size(28.dp).background(PsyPrimary, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(PsyPrimary, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(PsyPrimary, RoundedCornerShape(6.dp)))
                    Box(modifier = Modifier.size(28.dp).background(PsyPrimary, RoundedCornerShape(6.dp)))
                }
            }
        }

        Button(
            onClick = onClear,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PsyPrimary)
        ) {
            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Clear Baris Balok (+120 Pts)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PsyOnPrimary)
        }
    }
}

@Composable
fun PitchJumpGameView(
    game: GameState,
    onSmile: () -> Unit,
    onPitch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(PsyIceBlue)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 24.dp, y = ((game.playerY - 0.5f) * 200).dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PsySurfaceContainerLowest),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vibebot_mascot_1791383044160),
                    contentDescription = "Player",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSmile,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PsyPrimary)
            ) {
                Text("Senyum Lebar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PsyOnPrimary)
            }
            Button(
                onClick = onPitch,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
            ) {
                Text("Nada Tinggi Jump", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PsyOnPrimary)
            }
        }
    }
}
