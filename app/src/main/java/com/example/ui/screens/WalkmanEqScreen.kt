package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.WalkmanAudioEngine
import com.example.model.DcPhaseMode
import com.example.model.SoundstageMode
import com.example.model.VinylMode
import com.example.model.WalkmanPresets
import com.example.ui.components.FrequencyCurveView
import com.example.ui.theme.HiResYellow
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

@Composable
fun WalkmanEqScreen(
  audioEngine: WalkmanAudioEngine,
  modifier: Modifier = Modifier
) {
  val currentPreset by audioEngine.currentPreset.collectAsState()
  val customPresets by audioEngine.customPresets.collectAsState()
  var showSaveDialog by remember { mutableStateOf(false) }
  var newPresetName by remember { mutableStateOf("") }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
      .verticalScroll(scrollState)
      .padding(16.dp)
      .testTag("walkman_eq_screen")
  ) {
    // Header Bar with Sony Walkman Badge
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
            .background(WalkmanGoldPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = null,
            tint = ObsidianBackground,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "SONY WALKMAN®",
            color = WalkmanGoldPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "Personal Equalizer & DSP",
            color = PureWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Save Preset Button
      Button(
        onClick = {
          newPresetName = ""
          showSaveDialog = true
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = WalkmanGoldPrimary,
          contentColor = ObsidianBackground
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("save_preset_button")
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save Preset", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Preset selector chips
    Text(
      text = "SOUND PROFILES & PRESETS",
      color = TextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      WalkmanPresets.PRESETS.forEach { preset ->
        val isSelected = currentPreset.name == preset.name
        PresetChip(
          name = preset.name,
          isSelected = isSelected,
          onClick = { audioEngine.selectPreset(preset) },
          testTag = "preset_chip_${preset.name.replace(" ", "_")}"
        )
      }

      customPresets.forEach { preset ->
        val isSelected = currentPreset.name == preset.name
        PresetChip(
          name = preset.name,
          isSelected = isSelected,
          isCustom = true,
          onClick = { audioEngine.selectPreset(preset) },
          testTag = "preset_custom_chip_${preset.name.replace(" ", "_")}"
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Dynamic Real-time Frequency Curve Canvas
    FrequencyCurveView(bands = currentPreset.bands)

    Spacer(modifier = Modifier.height(20.dp))

    // 10-Band Graphic Equalizer Controls
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "10-BAND GRAPHIC EQUALIZER (±10 dB)",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )

      OutlinedButton(
        onClick = { audioEngine.resetToFlat() },
        shape = RoundedCornerShape(6.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ObsidianSurfaceBorder)),
        modifier = Modifier.testTag("reset_flat_button")
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Flat", color = TextMuted, fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Card(
      colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp, horizontal = 6.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        currentPreset.bands.forEachIndexed { index, gain ->
          val freqLabel = WalkmanPresets.BAND_FREQUENCIES.getOrElse(index) { "$index" }
          BandControlColumn(
            frequency = freqLabel,
            gainDb = gain,
            onGainChange = { newGain -> audioEngine.updateBand(index, newGain) },
            testTag = "band_slider_$freqLabel"
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Clear Bass Slider
    Card(
      colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(TubeAmber)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CLEAR BASS",
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          Text(
            text = if (currentPreset.clearBass >= 0) "+${currentPreset.clearBass}" else "${currentPreset.clearBass}",
            color = WalkmanGoldPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        Text(
          text = "Flagship Walkman sub-bass depth enhancement without midrange distortion.",
          color = TextMuted,
          fontSize = 11.sp,
          modifier = Modifier.padding(vertical = 4.dp)
        )

        Slider(
          value = currentPreset.clearBass.toFloat(),
          onValueChange = { audioEngine.updateClearBass(it.toInt()) },
          valueRange = -10f..10f,
          steps = 19,
          colors = SliderDefaults.colors(
            thumbColor = WalkmanGoldPrimary,
            activeTrackColor = TubeAmber,
            inactiveTrackColor = ObsidianBackground
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("clear_bass_slider")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("-10 (Tight)", color = TextSubtle, fontSize = 9.sp)
          Text("0 (Neutral)", color = TextSubtle, fontSize = 9.sp)
          Text("+10 (Maximum Impact)", color = TextSubtle, fontSize = 9.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // SONY WALKMAN DSP AUDIO ENHANCEMENTS SECTION
    Text(
      text = "SONY ACOUSTIC ENGINE (DSP)",
      color = TextMuted,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(10.dp))

    DspSwitchCard(
      title = "DSEE Ultimate™ / HX",
      description = "AI restoration of compressed streams up to 192kHz/24-bit Hi-Res clarity.",
      isChecked = currentPreset.dseeUltimate,
      onCheckedChange = { audioEngine.toggleDseeUltimate(it) },
      badge = "AI HI-RES",
      badgeColor = HiResYellow,
      testTag = "dsee_ultimate_switch"
    )

    Spacer(modifier = Modifier.height(10.dp))

    DspSwitchCard(
      title = "ClearAudio+™",
      description = "Automated digital signal processing for crisp soundfield separation.",
      isChecked = currentPreset.clearAudioPlus,
      onCheckedChange = { audioEngine.toggleClearAudioPlus(it) },
      badge = "SONY DSP",
      badgeColor = VUMeterCyan,
      testTag = "clear_audio_plus_switch"
    )

    Spacer(modifier = Modifier.height(10.dp))

    // DC Phase Linearizer Selector
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
          Text(
            text = "DC Phase Linearizer",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = currentPreset.dcPhaseLinearizer.label,
            color = WalkmanGoldPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
        Text(
          text = "Replicates low-frequency analog amplifier phase characteristics.",
          color = TextMuted,
          fontSize = 11.sp,
          modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          DcPhaseMode.values().forEach { mode ->
            val isSelected = currentPreset.dcPhaseLinearizer == mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) WalkmanGoldDark else ObsidianBackground)
                .border(1.dp, if (isSelected) WalkmanGoldPrimary else ObsidianSurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { audioEngine.setDcPhaseMode(mode) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("dc_phase_${mode.name}")
            ) {
              Text(
                text = mode.label,
                color = if (isSelected) PureWhite else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Vinyl Processor Selector
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
          Text(
            text = "Vinyl Processor™",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = currentPreset.vinylProcessor.label,
            color = TubeAmber,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
        Text(
          text = "Adds turntable tonearm acoustic warmth and rich vinyl harmonics.",
          color = TextMuted,
          fontSize = 11.sp,
          modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          VinylMode.values().forEach { mode ->
            val isSelected = currentPreset.vinylProcessor == mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) TubeAmber.copy(alpha = 0.3f) else ObsidianBackground)
                .border(1.dp, if (isSelected) TubeAmber else ObsidianSurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { audioEngine.setVinylMode(mode) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("vinyl_mode_${mode.name}")
            ) {
              Text(
                text = mode.label,
                color = if (isSelected) TubeAmber else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Spatial Soundstage Selector
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
          Text(
            text = "3D Spatial Soundstage",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = currentPreset.soundstage.label,
            color = VUMeterCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          SoundstageMode.values().forEach { mode ->
            val isSelected = currentPreset.soundstage == mode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) VUMeterCyan.copy(alpha = 0.25f) else ObsidianBackground)
                .border(1.dp, if (isSelected) VUMeterCyan else ObsidianSurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { audioEngine.setSoundstageMode(mode) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("soundstage_${mode.name}")
            ) {
              Text(
                text = mode.label,
                color = if (isSelected) VUMeterCyan else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Custom Presets Manager Section
    if (customPresets.isNotEmpty()) {
      Text(
        text = "YOUR SAVED CUSTOM PRESETS",
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        customPresets.forEach { preset ->
          Card(
            colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = preset.name,
                  color = PureWhite,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Bass: ${if (preset.clearBass >= 0) "+${preset.clearBass}" else "${preset.clearBass}"} • DSEE: ${if (preset.dseeUltimate) "ON" else "OFF"}",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                  onClick = { audioEngine.selectPreset(preset) },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentPreset.name == preset.name) WalkmanGoldPrimary else ObsidianSurfaceBorder,
                    contentColor = if (currentPreset.name == preset.name) ObsidianBackground else PureWhite
                  ),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.testTag("load_custom_preset_${preset.name.replace(" ", "_")}")
                ) {
                  Text(if (currentPreset.name == preset.name) "Active" else "Load", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                  onClick = { audioEngine.deleteCustomPreset(preset) },
                  modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_preset_${preset.name.replace(" ", "_")}")
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete preset",
                    tint = PeakRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }

  // Save Preset Dialog
  if (showSaveDialog) {
    AlertDialog(
      onDismissRequest = { showSaveDialog = false },
      title = {
        Text("Save Custom Preset", color = WalkmanGoldPrimary, fontWeight = FontWeight.Bold)
      },
      text = {
        Column {
          Text(
            "Enter a unique name for this 10-band equalizer configuration:",
            color = PureWhite,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = newPresetName,
            onValueChange = { newPresetName = it },
            placeholder = { Text("e.g. Reno 11 Audiophile Punch", color = TextSubtle) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = WalkmanGoldPrimary,
              unfocusedBorderColor = ObsidianSurfaceBorder,
              focusedTextColor = PureWhite,
              unfocusedTextColor = PureWhite
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("save_preset_name_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newPresetName.isNotBlank()) {
              audioEngine.saveCustomPreset(newPresetName)
              showSaveDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = WalkmanGoldPrimary,
            contentColor = ObsidianBackground
          ),
          modifier = Modifier.testTag("confirm_save_preset_button")
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSaveDialog = false }) {
          Text("Cancel", color = TextMuted)
        }
      },
      containerColor = ObsidianSurfaceElevated,
      shape = RoundedCornerShape(16.dp)
    )
  }
}

@Composable
fun BandControlColumn(
  frequency: String,
  gainDb: Float,
  onGainChange: (Float) -> Unit,
  testTag: String
) {
  Column(
    modifier = Modifier
      .width(52.dp)
      .padding(horizontal = 2.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = String.format(java.util.Locale.US, if (gainDb >= 0) "+%.1f" else "%.1f", gainDb),
      color = if (gainDb > 0) WalkmanGoldPrimary else if (gainDb < 0) VUMeterCyan else TextMuted,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )

    Spacer(modifier = Modifier.height(6.dp))

    Slider(
      value = gainDb,
      onValueChange = onGainChange,
      valueRange = -10.0f..10.0f,
      colors = SliderDefaults.colors(
        thumbColor = WalkmanGoldPrimary,
        activeTrackColor = WalkmanGoldPrimary,
        inactiveTrackColor = ObsidianBackground
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = frequency,
      color = PureWhite,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun PresetChip(
  name: String,
  isSelected: Boolean,
  isCustom: Boolean = false,
  onClick: () -> Unit,
  testTag: String
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) WalkmanGoldPrimary else ObsidianSurfaceElevated)
      .border(
        width = 1.dp,
        color = if (isSelected) WalkmanGoldLight else ObsidianSurfaceBorder,
        shape = RoundedCornerShape(8.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .testTag(testTag)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (isCustom) {
        Icon(
          Icons.Default.Bookmark,
          contentDescription = null,
          tint = if (isSelected) ObsidianBackground else WalkmanGoldPrimary,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
      }
      Text(
        text = name,
        color = if (isSelected) ObsidianBackground else PureWhite,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

@Composable
fun DspSwitchCard(
  title: String,
  description: String,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  badge: String,
  badgeColor: Color,
  testTag: String
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianSurfaceBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(badgeColor.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badge,
              color = badgeColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
        Text(
          text = description,
          color = TextMuted,
          fontSize = 11.sp,
          modifier = Modifier.padding(top = 2.dp)
        )
      }

      Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = WalkmanGoldPrimary,
          checkedTrackColor = WalkmanGoldDark,
          uncheckedThumbColor = TextMuted,
          uncheckedTrackColor = ObsidianBackground
        ),
        modifier = Modifier.testTag(testTag)
      )
    }
  }
}
