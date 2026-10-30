package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PeakRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TubeAmber
import com.example.ui.theme.VUMeterCyan
import com.example.ui.theme.WalkmanGoldPrimary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FrequencyCurveView(
  bands: List<Float>,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(130.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(ObsidianSurfaceElevated)
      .border(1.dp, ObsidianSurfaceBorder, RoundedCornerShape(12.dp))
      .padding(horizontal = 12.dp, vertical = 8.dp)
      .testTag("frequency_curve_view")
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val width = size.width
      val height = size.height
      val midY = height / 2f

      val gridColor = ObsidianSurfaceBorder.copy(alpha = 0.6f)
      val zeroColor = WalkmanGoldPrimary.copy(alpha = 0.35f)

      drawLine(
        color = zeroColor,
        start = Offset(0f, midY),
        end = Offset(width, midY),
        strokeWidth = 1.5f
      )

      val yPlus5 = midY - (height * 0.25f)
      val yMinus5 = midY + (height * 0.25f)
      drawLine(gridColor, Offset(0f, yPlus5), Offset(width, yPlus5), strokeWidth = 1f)
      drawLine(gridColor, Offset(0f, yMinus5), Offset(width, yMinus5), strokeWidth = 1f)

      if (bands.isEmpty()) return@Canvas

      val stepX = width / (bands.size - 1)
      val points = bands.mapIndexed { index, gain ->
        val x = index * stepX
        val normalizedGain = (gain.coerceIn(-10f, 10f)) / 10f
        val y = midY - (normalizedGain * (height * 0.45f))
        Offset(x, y)
      }

      val fillPath = Path().apply {
        moveTo(0f, height)
        lineTo(points.first().x, points.first().y)
        for (i in 0 until points.size - 1) {
          val p0 = points[i]
          val p1 = points[i + 1]
          val midX = (p0.x + p1.x) / 2f
          cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
        }
        lineTo(width, height)
        close()
      }

      drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
          colors = listOf(
            WalkmanGoldPrimary.copy(alpha = 0.45f),
            VUMeterCyan.copy(alpha = 0.15f),
            Color.Transparent
          )
        )
      )

      val strokePath = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 0 until points.size - 1) {
          val p0 = points[i]
          val p1 = points[i + 1]
          val midX = (p0.x + p1.x) / 2f
          cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
        }
      }

      drawPath(
        path = strokePath,
        brush = Brush.horizontalGradient(
          listOf(TubeAmber, WalkmanGoldPrimary, VUMeterCyan)
        ),
        style = Stroke(width = 3.5f)
      )

      for (p in points) {
        drawCircle(
          color = WalkmanGoldPrimary,
          radius = 4f,
          center = p
        )
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter)
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text("+10dB", color = TextMuted, fontSize = 9.sp)
      Text("RESP. CURVE", color = WalkmanGoldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
      Text("-10dB", color = TextMuted, fontSize = 9.sp)
    }
  }
}

@Composable
fun AnalogVUMeter(
  channelName: String,
  level: Float,
  modifier: Modifier = Modifier
) {
  val animatedLevel by animateFloatAsState(
    targetValue = level.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 60),
    label = "vu_needle"
  )

  val isPeaking = animatedLevel > 0.88f

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(ObsidianBackground)
      .border(1.5.dp, ObsidianSurfaceBorder, RoundedCornerShape(10.dp))
      .padding(8.dp)
      .testTag("vu_meter_$channelName")
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "CH $channelName",
          color = WalkmanGoldPrimary,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "PEAK",
            color = if (isPeaking) PeakRed else TextMuted.copy(alpha = 0.5f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(4.dp))
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(if (isPeaking) PeakRed else ObsidianSurfaceBorder)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(65.dp)
      ) {
        val w = size.width
        val h = size.height
        val pivot = Offset(w / 2f, h * 1.05f)
        val radius = h * 0.95f

        val startAngle = 210.0
        val sweepAngle = 120.0

        val tickCount = 13
        for (i in 0..tickCount) {
          val frac = i / tickCount.toDouble()
          val angleDeg = startAngle + frac * sweepAngle
          val angleRad = Math.toRadians(angleDeg)

          val isRedZone = frac > 0.75
          val tickColor = if (isRedZone) PeakRed else VUMeterCyan.copy(alpha = 0.7f)
          val tickLen = if (i % 3 == 0) 14f else 8f

          val x1 = pivot.x + (radius - tickLen).toFloat() * cos(angleRad).toFloat()
          val y1 = pivot.y + (radius - tickLen).toFloat() * sin(angleRad).toFloat()
          val x2 = pivot.x + radius * cos(angleRad).toFloat()
          val y2 = pivot.y + radius * sin(angleRad).toFloat()

          drawLine(tickColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 2f)
        }

        val currentAngleDeg = startAngle + (animatedLevel.toDouble() * sweepAngle)
        val currentAngleRad = Math.toRadians(currentAngleDeg)

        val needleColor = if (isPeaking) PeakRed else VUMeterCyan
        val tipX = pivot.x + (radius * 0.92f) * cos(currentAngleRad).toFloat()
        val tipY = pivot.y + (radius * 0.92f) * sin(currentAngleRad).toFloat()

        drawLine(
          color = needleColor,
          start = pivot,
          end = Offset(tipX, tipY),
          strokeWidth = 2.5f
        )

        drawCircle(color = WalkmanGoldPrimary, radius = 6f, center = pivot)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("-20dB", color = TextMuted, fontSize = 8.sp)
        Text("0 VU", color = VUMeterCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Text("+3dB", color = PeakRed, fontSize = 8.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun SpectrumVisualizerView(
  bands: List<Float>,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(55.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(ObsidianBackground)
      .border(1.dp, ObsidianSurfaceBorder, RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp)
      .testTag("spectrum_visualizer_view"),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Bottom
  ) {
    bands.forEachIndexed { index, level ->
      val animatedLevel by animateFloatAsState(
        targetValue = level.coerceIn(0.05f, 1f),
        animationSpec = tween(durationMillis = 60),
        label = "spectrum_bar_$index"
      )

      val barColor = when {
        index < 4 -> TubeAmber
        index < 10 -> WalkmanGoldPrimary
        else -> VUMeterCyan
      }

      Column(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(0.7f)
            .fillMaxHeight(animatedLevel)
            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
            .background(
              Brush.verticalGradient(
                colors = listOf(barColor, barColor.copy(alpha = 0.35f))
              )
            )
        )
      }
    }
  }
}
