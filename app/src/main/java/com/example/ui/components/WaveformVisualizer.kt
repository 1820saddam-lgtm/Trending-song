package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HotMagenta
import com.example.ui.theme.NeonViolet

@Composable
fun WaveformVisualizer(
  isPlaying: Boolean,
  levels: List<Float>,
  modifier: Modifier = Modifier,
  height: Dp = 32.dp,
  barCount: Int = 16,
  colors: List<Color> = listOf(NeonViolet, ElectricCyan, HotMagenta)
) {
  val animatedPhase = remember { Animatable(0f) }

  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      animatedPhase.animateTo(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    } else {
      animatedPhase.snapTo(0f)
    }
  }

  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
  ) {
    val totalWidth = size.width
    val maxHeight = size.height
    val spacing = 3.dp.toPx()
    val barWidth = ((totalWidth - (barCount - 1) * spacing) / barCount).coerceAtLeast(2.dp.toPx())

    val brush = Brush.linearGradient(
      colors = colors,
      start = Offset(0f, 0f),
      end = Offset(totalWidth, maxHeight)
    )

    for (i in 0 until barCount) {
      val rawLevel = if (isPlaying) {
        val levelIdx = i % levels.size
        val dynamicFactor = (levels[levelIdx] * (0.6f + 0.4f * animatedPhase.value))
        dynamicFactor.coerceIn(0.12f, 1.0f)
      } else {
        0.08f
      }

      val barHeight = (maxHeight * rawLevel).coerceAtLeast(3.dp.toPx())
      val xOffset = i * (barWidth + spacing)
      val yOffset = (maxHeight - barHeight) / 2f

      drawRoundRect(
        brush = brush,
        topLeft = Offset(xOffset, yOffset),
        size = Size(barWidth, barHeight),
        cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
      )
    }
  }
}
