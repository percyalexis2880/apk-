package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr.QrCodeGenerator
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrimaryGreen

@Composable
fun QrCodeView(
  content: String,
  modifier: Modifier = Modifier,
  sizeDp: Dp = 240.dp,
  centerEmoji: String? = "💳",
  showScanBeam: Boolean = true,
  darkColor: Color = Color(0xFF0F172A),
  lightColor: Color = Color.White
) {
  val qrMatrix = remember(content) {
    try {
      QrCodeGenerator.encode(content)
    } catch (e: Exception) {
      QrCodeGenerator.encode("https://qrpay.com/pay")
    }
  }

  // Scanning laser beam animation
  val transition = rememberInfiniteTransition(label = "scan_beam")
  val beamY by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "beam_position"
  )

  Surface(
    modifier = modifier
      .size(sizeDp)
      .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
      .clip(RoundedCornerShape(24.dp))
      .background(lightColor)
      .border(
        width = 2.dp,
        brush = Brush.linearGradient(
          colors = listOf(PrimaryGreen, CyanAccent)
        ),
        shape = RoundedCornerShape(24.dp)
      )
      .testTag("qr_code_container"),
    color = lightColor
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      // Draw QR Canvas
      Canvas(modifier = Modifier.fillMaxSize()) {
        val matrixSize = qrMatrix.size
        val moduleWidth = this.size.width / matrixSize
        val moduleHeight = this.size.height / matrixSize
        val cornerRadius = CornerRadius(moduleWidth * 0.25f, moduleHeight * 0.25f)

        // Draw background
        drawRect(color = lightColor)

        // Draw Modules
        for (y in 0 until matrixSize) {
          for (x in 0 until matrixSize) {
            if (qrMatrix[x, y]) {
              // Highlight finder pattern corners with rich navy or emerald
              val isFinder = (x < 8 && y < 8) || (x >= matrixSize - 8 && y < 8) || (x < 8 && y >= matrixSize - 8)
              val moduleColor = if (isFinder) Color(0xFF003820) else darkColor

              drawRoundRect(
                color = moduleColor,
                topLeft = Offset(x * moduleWidth, y * moduleHeight),
                size = Size(moduleWidth, moduleHeight),
                cornerRadius = cornerRadius
              )
            }
          }
        }

        // Draw active laser scanning line if enabled
        if (showScanBeam) {
          val scanTop = this.size.height * beamY
          drawLine(
            brush = Brush.horizontalGradient(
              colors = listOf(
                Color.Transparent,
                PrimaryGreen.copy(alpha = 0.8f),
                CyanAccent,
                PrimaryGreen.copy(alpha = 0.8f),
                Color.Transparent
              )
            ),
            start = Offset(0f, scanTop),
            end = Offset(this.size.width, scanTop),
            strokeWidth = 3.dp.toPx()
          )
        }
      }

      // Center decorative badge / logo
      if (!centerEmoji.isNullOrEmpty()) {
        Box(
          modifier = Modifier
            .size(sizeDp * 0.2f)
            .clip(CircleShape)
            .background(lightColor)
            .border(2.dp, PrimaryGreen, CircleShape)
            .shadow(4.dp, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = centerEmoji,
            fontSize = (sizeDp.value * 0.09f).sp
          )
        }
      }
    }
  }
}
