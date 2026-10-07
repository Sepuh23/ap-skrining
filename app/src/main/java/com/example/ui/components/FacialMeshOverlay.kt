package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensorTelemetry
import com.example.ui.theme.*

@Composable
fun FacialMeshOverlay(
    telemetry: SensorTelemetry,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh_pulse")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_line"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mesh_glow"
    )

    Box(modifier = modifier) {
        // Biometric Face Mesh Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerX = w / 2f
            val centerY = h * 0.48f

            val primaryColor = PsySoftSkyBlue.copy(alpha = pulseAlpha)
            val strokeWidth = 2.dp.toPx()
            val dashedStroke = Stroke(
                width = strokeWidth,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )

            // Face Oval Contour (MediaPipe outline)
            val facePath = Path().apply {
                moveTo(centerX, centerY - h * 0.32f)
                cubicTo(
                    centerX + w * 0.32f, centerY - h * 0.32f,
                    centerX + w * 0.34f, centerY + h * 0.28f,
                    centerX, centerY + h * 0.34f
                )
                cubicTo(
                    centerX - w * 0.34f, centerY + h * 0.28f,
                    centerX - w * 0.32f, centerY - h * 0.32f,
                    centerX, centerY - h * 0.32f
                )
                close()
            }
            drawPath(facePath, color = primaryColor, style = dashedStroke)

            // Eyebrow Contours (AU4)
            val leftBrow = Path().apply {
                moveTo(centerX - w * 0.22f, centerY - h * 0.12f)
                quadraticBezierTo(centerX - w * 0.14f, centerY - h * 0.16f, centerX - w * 0.05f, centerY - h * 0.13f)
            }
            val rightBrow = Path().apply {
                moveTo(centerX + w * 0.05f, centerY - h * 0.13f)
                quadraticBezierTo(centerX + w * 0.14f, centerY - h * 0.16f, centerX + w * 0.22f, centerY - h * 0.12f)
            }
            drawPath(leftBrow, color = PsyPrimaryContainer, style = Stroke(width = strokeWidth))
            drawPath(rightBrow, color = PsyPrimaryContainer, style = Stroke(width = strokeWidth))
            drawCircle(color = PsyPrimaryContainer, radius = 5.dp.toPx(), center = Offset(centerX, centerY - h * 0.13f))

            // Eyes (MediaPipe Iris & Lids)
            drawOval(
                color = primaryColor,
                topLeft = Offset(centerX - w * 0.22f, centerY - h * 0.08f),
                size = Size(w * 0.16f, h * 0.08f),
                style = Stroke(width = strokeWidth)
            )
            drawCircle(
                color = PsyCyanGlow,
                radius = 4.dp.toPx(),
                center = Offset(centerX - w * 0.14f, centerY - h * 0.04f)
            )

            drawOval(
                color = primaryColor,
                topLeft = Offset(centerX + w * 0.06f, centerY - h * 0.08f),
                size = Size(w * 0.16f, h * 0.08f),
                style = Stroke(width = strokeWidth)
            )
            drawCircle(
                color = PsyCyanGlow,
                radius = 4.dp.toPx(),
                center = Offset(centerX + w * 0.14f, centerY - h * 0.04f)
            )

            // Nose Bridge
            drawLine(
                color = primaryColor.copy(alpha = 0.6f),
                start = Offset(centerX, centerY - h * 0.10f),
                end = Offset(centerX, centerY + h * 0.08f),
                strokeWidth = 1.5.dp.toPx()
            )
            drawCircle(color = PsyPrimaryContainer, radius = 3.5.dp.toPx(), center = Offset(centerX, centerY + h * 0.08f))

            // Mouth & Smile AU12 Tension Points
            val mouthPath = Path().apply {
                val mouthY = centerY + h * 0.18f
                moveTo(centerX - w * 0.18f, mouthY)
                val curveDown = if (telemetry.isSuppressedSmile) mouthY + h * 0.03f else mouthY + h * 0.06f
                quadraticBezierTo(centerX, curveDown, centerX + w * 0.18f, mouthY)
            }
            drawPath(mouthPath, color = PsyPrimaryContainer, style = Stroke(width = 2.5.dp.toPx()))
            drawCircle(color = PsyPrimaryContainer, radius = 4.dp.toPx(), center = Offset(centerX - w * 0.18f, centerY + h * 0.18f))
            drawCircle(color = PsyPrimaryContainer, radius = 4.dp.toPx(), center = Offset(centerX + w * 0.18f, centerY + h * 0.18f))

            // Laser Biometric Scanning Line
            val scanY = h * 0.15f + (h * 0.7f * scanProgress)
            drawLine(
                color = PsyPrimaryContainer.copy(alpha = 0.8f),
                start = Offset(w * 0.1f, scanY),
                end = Offset(w * 0.9f, scanY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // HUD Top Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(PsySurfaceContainerLowest.copy(alpha = 0.85f), RoundedCornerShape(100.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(PsyError, RoundedCornerShape(100.dp))
                )
                Text(
                    text = "LIVE • 30 FPS",
                    color = PsyOnSurface,
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .background(PsySurfaceContainerLowest.copy(alpha = 0.85f), RoundedCornerShape(100.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "📷 MediaPipe 468 FACS",
                    color = PsyPrimary,
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }
        }

        // Floating FACS Telemetry Badges at Bottom of Viewfinder
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(PsySurfaceContainerLowest.copy(alpha = 0.92f), RoundedCornerShape(100.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(if (telemetry.microTensionIndex > 50) PsyYellowWarning else PsyGreenSafe, RoundedCornerShape(100.dp))
                )
                Text(
                    text = "Kerutan Dahi (AU4): ${telemetry.tensionStatusLabel}",
                    color = PsyOnSurface,
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier
                    .background(PsySurfaceContainerLowest.copy(alpha = 0.92f), RoundedCornerShape(100.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(if (telemetry.isSuppressedSmile) PsyPrimaryContainer else PsyGreenSafe, RoundedCornerShape(100.dp))
                )
                Text(
                    text = if (telemetry.isSuppressedSmile) "AU12 Smile: Suppressed / Fake Smile Detected" else "AU12 Smile: Natural & Relaxed",
                    color = PsyOnSurface,
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier
                    .background(PsySurfaceContainerLowest.copy(alpha = 0.92f), RoundedCornerShape(100.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(PsySoftSkyBlue, RoundedCornerShape(100.dp))
                )
                Text(
                    text = "Kedipan: ${telemetry.eyeBlinkRhythmRate} bpm (Stabil)",
                    color = PsyOnSurface,
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
        }
    }
}
