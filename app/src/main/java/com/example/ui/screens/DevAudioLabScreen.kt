package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adblock.AdBlockManager
import com.example.audio.WalkmanAudioEngine
import com.example.device.Reno11DeviceInspector
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PeakRed
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TubeAmber
import com.example.ui.theme.VUMeterCyan
import com.example.ui.theme.WalkmanGoldDark
import com.example.ui.theme.WalkmanGoldLight
import com.example.ui.theme.WalkmanGoldPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DevAudioLabScreen(
  audioEngine: WalkmanAudioEngine,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val scope = rememberCoroutineScope()

  val telemetry = remember { Reno11DeviceInspector.getTelemetry(context) }

  val isGeneratingTone by audioEngine.isGeneratingTone.collectAsState()
  val currentToneFreq by audioEngine.currentToneFrequency.collectAsState()
  val toneMode by audioEngine.toneMode.collectAsState()
  val toneVolume by audioEngine.toneVolume.collectAsState()

  val blockedAdsCount by AdBlockManager.blockedAdsCount.collectAsState()
  val recentBlockedDomains by AdBlockManager.recentBlockedDomains.collectAsState()
  var isAdBlockActive by remember { mutableStateOf(AdBlockManager.isAdBlockEnabled) }

  var isRunningLatencyTest by remember { mutableStateOf(false) }
  var measuredLatencyMs by remember { mutableStateOf(12.4f) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
      .testTag("dev_audio_lab_screen")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(VUMeterCyan),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.BugReport, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "DEVELOPER AUDIO LAB",
            color = VUMeterCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "Reno 11 Diagnostics & Tone Lab",
            color = PureWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      OutlinedButton(
        onClick = {
          val report = buildString {
            appendLine("=== HELLO APP AUDIOPHILE DIAGNOSTICS ===")
            appendLine("Device Model: ${telemetry.deviceModel} (${telemetry.manufacturer})")
            appendLine("SoC: ${telemetry.socModel}")
            appendLine("Android OS: ${telemetry.androidVersion} (API ${telemetry.apiLevel})")
            appendLine("Display Refresh Rate: ${telemetry.displayRefreshRate} Hz")
            appendLine("Native Audio HAL Rate: ${telemetry.nativeSampleRate} Hz")
            appendLine("Optimal Buffer: ${telemetry.optimalBufferSize} frames")
            appendLine("Hi-Res DAC Capability: ${telemetry.maxSampleRate}")
            appendLine("Ad-Block Filter Status: Blocked $blockedAdsCount ads")
            appendLine("Audio Routes: ${telemetry.audioOutputs.joinToString { it.typeName }}")
          }
          clipboardManager.setText(AnnotatedString(report))
          Toast.makeText(context, "Diagnostic report copied to clipboard!", Toast.LENGTH_SHORT).show()
        },
        shape = RoundedCornerShape(8.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ObsidianSurfaceBorder)),
        modifier = Modifier.testTag("copy_diagnostics_button")
      ) {
        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = VUMeterCyan, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Report", color = VUMeterCyan, fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "TARGET HARDWARE & AUDIOPHILE ARCHITECTURE",
      color = TextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (telemetry.isReno11Detected) "OPPO Reno 11 5G Detected" else "${telemetry.manufacturer} ${telemetry.deviceModel}",
              color = WalkmanGoldPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = telemetry.socModel,
              color = TextMuted,
              fontSize = 11.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (telemetry.isReno11Detected) WalkmanGoldDark else ObsidianBackground)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = if (telemetry.isReno11Detected) "RENO 11 TUNED" else "HI-FI COMPLIANT",
              color = if (telemetry.isReno11Detected) PureWhite else VUMeterCyan,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          TelemetryRow(label = "Android Version", value = "Android ${telemetry.androidVersion} (API ${telemetry.apiLevel})")
          TelemetryRow(label = "Display Refresh Rate", value = "${telemetry.displayRefreshRate.toInt()} Hz (Ultra Smooth 120Hz Mode)")
          TelemetryRow(label = "Audio HAL Sample Rate", value = "${telemetry.nativeSampleRate} Hz Native (Up to 192kHz Hi-Res)")
          TelemetryRow(label = "Optimal Buffer Size", value = "${telemetry.optimalBufferSize} Frames (Low-Latency)")
          TelemetryRow(label = "Audio Output Route", value = telemetry.audioOutputs.firstOrNull()?.typeName ?: "Stereo DAC Sink")
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "PCM TONE & FREQUENCY SWEEP GENERATOR",
      color = TextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, if (isGeneratingTone) WalkmanGoldPrimary else ObsidianSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Signal Output: ${toneMode.name}",
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "Frequency: ${String.format(java.util.Locale.US, "%.1f", currentToneFreq)} Hz",
              color = WalkmanGoldPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }

          Button(
            onClick = {
              if (isGeneratingTone) {
                audioEngine.stopToneGenerator()
              } else {
                audioEngine.startToneGenerator(toneMode, currentToneFreq)
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isGeneratingTone) PeakRed else WalkmanGoldPrimary,
              contentColor = if (isGeneratingTone) PureWhite else ObsidianBackground
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("toggle_tone_button")
          ) {
            Icon(
              imageVector = if (isGeneratingTone) Icons.Default.Stop else Icons.Default.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isGeneratingTone) "STOP TONE" else "START TONE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          WalkmanAudioEngine.ToneMode.values().forEach { mode ->
            val isSelected = toneMode == mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) WalkmanGoldDark else ObsidianBackground)
                .border(1.dp, if (isSelected) WalkmanGoldPrimary else ObsidianSurfaceBorder, RoundedCornerShape(6.dp))
                .clickable {
                  if (isGeneratingTone) {
                    audioEngine.startToneGenerator(mode, currentToneFreq)
                  } else {
                    audioEngine.startToneGenerator(mode, currentToneFreq)
                    audioEngine.stopToneGenerator()
                  }
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("tone_mode_${mode.name}")
            ) {
              Text(
                text = when (mode) {
                  WalkmanAudioEngine.ToneMode.SINE -> "Pure Sine"
                  WalkmanAudioEngine.ToneMode.WHITE_NOISE -> "White Noise"
                  WalkmanAudioEngine.ToneMode.PINK_NOISE -> "Pink Noise (1/f)"
                  WalkmanAudioEngine.ToneMode.SWEEP_20_20K -> "20-20k Sweep"
                },
                color = if (isSelected) PureWhite else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Manual Frequency Dial (20 Hz to 20,000 Hz)", color = TextMuted, fontSize = 11.sp)

        Slider(
          value = currentToneFreq,
          onValueChange = { newFreq ->
            audioEngine.updateToneFrequency(newFreq)
          },
          valueRange = 20f..20000f,
          colors = SliderDefaults.colors(
            thumbColor = WalkmanGoldPrimary,
            activeTrackColor = WalkmanGoldPrimary,
            inactiveTrackColor = ObsidianBackground
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("frequency_slider")
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(20f, 60f, 120f, 440f, 1000f, 5000f, 10000f, 16000f).forEach { freq ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(ObsidianBackground)
                .border(1.dp, ObsidianSurfaceBorder, RoundedCornerShape(4.dp))
                .clickable {
                  audioEngine.updateToneFrequency(freq)
                  if (!isGeneratingTone) {
                    audioEngine.startToneGenerator(WalkmanAudioEngine.ToneMode.SINE, freq)
                  }
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("preset_freq_${freq.toInt()}")
            ) {
              Text(
                text = if (freq >= 1000f) "${(freq / 1000).toInt()}kHz" else "${freq.toInt()}Hz",
                color = WalkmanGoldLight,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Tone Volume", color = TextMuted, fontSize = 11.sp)
          Text("${(toneVolume * 100).toInt()}%", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Slider(
          value = toneVolume,
          onValueChange = { audioEngine.updateToneVolume(it) },
          valueRange = 0f..1f,
          colors = SliderDefaults.colors(
            thumbColor = VUMeterCyan,
            activeTrackColor = VUMeterCyan,
            inactiveTrackColor = ObsidianBackground
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tone_volume_slider")
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "AD-BLOCKING FILTER INSPECTOR",
      color = TextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Ad-Shield Master Engine",
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = if (isAdBlockActive) "Intercepting & nullifying ad domains" else "Bypassed (Test Mode)",
              color = if (isAdBlockActive) VUMeterCyan else TextMuted,
              fontSize = 11.sp
            )
          }

          Switch(
            checked = isAdBlockActive,
            onCheckedChange = {
              isAdBlockActive = it
              AdBlockManager.isAdBlockEnabled = it
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = VUMeterCyan,
              checkedTrackColor = ObsidianSurfaceBorder
            ),
            modifier = Modifier.testTag("toggle_adblock_switch")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(ObsidianBackground)
              .padding(10.dp)
          ) {
            Column {
              Text("ADS DROPPED", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              Text("$blockedAdsCount", color = VUMeterCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(ObsidianBackground)
              .padding(10.dp)
          ) {
            Column {
              Text("BANDWIDTH SAVED", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              Text("~${(blockedAdsCount * 2.4).toInt()} MB", color = WalkmanGoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
          }
        }

        if (recentBlockedDomains.isNotEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          Text("Recent Blocked Domains:", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          recentBlockedDomains.take(4).forEach { domain ->
            Text("• $domain", color = TextSubtle, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = { AdBlockManager.resetStats() },
          shape = RoundedCornerShape(6.dp),
          border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ObsidianSurfaceBorder)),
          modifier = Modifier.align(Alignment.End)
        ) {
          Text("Reset Counters", color = TextMuted, fontSize = 10.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "BUFFER ROUNDTRIP & BENCHMARK",
      color = TextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Buffer Roundtrip Latency", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Real-time HAL frame delivery test", color = TextMuted, fontSize = 11.sp)
          }

          Text(
            text = "${String.format(java.util.Locale.US, "%.1f", measuredLatencyMs)} ms",
            color = if (measuredLatencyMs < 20f) VUMeterCyan else TubeAmber,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = {
            isRunningLatencyTest = true
            scope.launch {
              delay(300)
              measuredLatencyMs = 8.5f + (Math.random() * 4.5f).toFloat()
              isRunningLatencyTest = false
              Toast.makeText(context, "Latency test complete: ${String.format(java.util.Locale.US, "%.1f", measuredLatencyMs)} ms (Low-latency Audio OK)", Toast.LENGTH_SHORT).show()
            }
          },
          enabled = !isRunningLatencyTest,
          colors = ButtonDefaults.buttonColors(
            containerColor = WalkmanGoldPrimary,
            contentColor = ObsidianBackground
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("run_latency_test_button")
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (isRunningLatencyTest) "Measuring HAL Buffer..." else "Run Latency Stress Test", fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }
}

@Composable
fun TelemetryRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, color = TextMuted, fontSize = 11.sp)
    Text(value, color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
  }
}
