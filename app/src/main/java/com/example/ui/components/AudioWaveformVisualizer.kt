package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AudioWaveformVisualizer(
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
    
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = 28f,
        animationSpec = infiniteRepeatable(tween(240, easing = LinearEasing), RepeatMode.Reverse), label = "b1"
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 18f, targetValue = 32f,
        animationSpec = infiniteRepeatable(tween(320, easing = LinearEasing), RepeatMode.Reverse), label = "b2"
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(190, easing = LinearEasing), RepeatMode.Reverse), label = "b3"
    )
    val bar4 by infiniteTransition.animateFloat(
        initialValue = 14f, targetValue = 36f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse), label = "b4"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PsySurfaceContainerLowest, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "🎙️ Audio Input Spectrum",
                    fontSize = 12.sp,
                    color = PsyOnSurfaceVariant,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
            Text(
                text = if (isRecording) "Merekam Suara..." else "Live Frequency",
                fontSize = 12.sp,
                color = if (isRecording) PsyError else PsyPrimary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        }

        // 18 Wave Bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val heights = listOf(
                bar1, bar2, bar3, bar4, bar2 * 0.7f, bar1 * 1.1f,
                bar3 * 1.3f, bar4 * 0.9f, bar2 * 1.1f, bar1 * 0.8f,
                bar4 * 0.7f, bar3 * 1.1f, bar2 * 0.95f, bar1 * 1.2f,
                bar4 * 0.85f, bar3 * 0.9f, bar2 * 0.75f, bar1
            )
            heights.forEach { h ->
                val calculatedHeight = if (isRecording) (h * 1.3f).coerceIn(4f, 36f) else h.coerceIn(4f, 36f)
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(calculatedHeight.dp)
                        .background(
                            if (isRecording) PsyError else PsyPrimaryContainer,
                            RoundedCornerShape(100.dp)
                        )
                )
            }
        }
    }
}
