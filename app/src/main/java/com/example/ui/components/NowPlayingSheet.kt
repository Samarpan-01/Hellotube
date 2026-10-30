package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.audio.WalkmanAudioEngine
import com.example.model.MediaItem
import com.example.ui.theme.HiResYellow
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TubeAmber
import com.example.ui.theme.VUMeterCyan
import com.example.ui.theme.WalkmanGoldPrimary

@Composable
fun MiniPlayerBar(
  currentTrack: MediaItem,
  isPlaying: Boolean,
  onTogglePlay: () -> Unit,
  onBarClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(64.dp)
      .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      .background(ObsidianSurfaceElevated)
      .border(1.dp, ObsidianSurfaceBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      .clickable(onClick = onBarClick)
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("mini_player_bar")
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
        ) {
          AsyncImage(
            model = currentTrack.thumbnailUrl,
            contentDescription = currentTrack.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = currentTrack.title,
            color = PureWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = currentTrack.artistOrChannel,
              color = TextMuted,
              fontSize = 11.sp,
              maxLines = 1
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "• ${currentTrack.sampleRate}",
              color = HiResYellow,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(WalkmanGoldPrimary)
            .clickable(onClick = onTogglePlay),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = ObsidianBackground,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}

@Composable
fun NowPlayingView(
  audioEngine: WalkmanAudioEngine,
  currentTrack: MediaItem,
  onDismiss: () -> Unit,
  onNavigateToEq: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isPlaying by audioEngine.isPlaying.collectAsState()
  val currentPosMs by audioEngine.currentPositionMs.collectAsState()
  val durationMs by audioEngine.durationMs.collectAsState()
  val leftVU by audioEngine.leftChannelVU.collectAsState()
  val rightVU by audioEngine.rightChannelVU.collectAsState()
  val spectrumBands by audioEngine.spectrumBands.collectAsState()
  val currentPreset by audioEngine.currentPreset.collectAsState()
  val isBackgroundPlayEnabled by audioEngine.isBackgroundPlayEnabled.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
      .testTag("now_playing_expanded_view")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_player_button")) {
        Icon(Icons.Default.ExpandMore, contentDescription = "Minimize", tint = PureWhite, modifier = Modifier.size(28.dp))
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("NOW PLAYING", color = TextMuted, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
        Text("Sony Walkman Master Player", color = WalkmanGoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
      }

      IconButton(onClick = onNavigateToEq, modifier = Modifier.testTag("open_eq_from_player")) {
        Icon(Icons.Default.GraphicEq, contentDescription = "EQ", tint = WalkmanGoldPrimary, modifier = Modifier.size(24.dp))
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "ANALOG VU PEAK LEVEL METERS",
      color = TextMuted,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AnalogVUMeter(
        channelName = "L",
        level = leftVU,
        modifier = Modifier.weight(1f)
      )
      AnalogVUMeter(
        channelName = "R",
        level = rightVU,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "REAL-TIME 16-BAND SPECTRUM ANALYZER",
      color = TextMuted,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    SpectrumVisualizerView(bands = spectrumBands)

    Spacer(modifier = Modifier.height(14.dp))

    Card(
      colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
      shape = RoundedCornerShape(12.dp),
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ScreenLockPortrait,
            contentDescription = null,
            tint = if (isBackgroundPlayEnabled) VUMeterCyan else TextMuted,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Screen-Off & Background Play",
              color = PureWhite,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = if (isBackgroundPlayEnabled) "Audio stays awake with screen off (WakeLock active)" else "Pauses when screen turns off",
              color = if (isBackgroundPlayEnabled) VUMeterCyan else TextMuted,
              fontSize = 10.sp
            )
          }
        }

        Switch(
          checked = isBackgroundPlayEnabled,
          onCheckedChange = { audioEngine.toggleBackgroundPlay(it) },
          colors = SwitchDefaults.colors(
            checkedThumbColor = VUMeterCyan,
            checkedTrackColor = ObsidianBackground
          ),
          modifier = Modifier.testTag("toggle_screen_off_play")
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, ObsidianSurfaceBorder, RoundedCornerShape(16.dp))
    ) {
      AsyncImage(
        model = currentTrack.thumbnailUrl,
        contentDescription = currentTrack.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(12.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(ObsidianBackground.copy(alpha = 0.9f))
          .border(1.dp, HiResYellow.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Column(horizontalAlignment = Alignment.End) {
          Text("HI-RES AUDIO", color = HiResYellow, fontSize = 9.sp, fontWeight = FontWeight.Black)
          Text(currentTrack.sampleRate, color = PureWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = currentTrack.title,
      color = PureWhite,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 2
    )

    Spacer(modifier = Modifier.height(4.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = currentTrack.artistOrChannel,
        color = TextMuted,
        fontSize = 13.sp
      )

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(VUMeterCyan.copy(alpha = 0.2f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "EQ: ${currentPreset.name}",
          color = VUMeterCyan,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    val progressFrac = if (durationMs > 0) (currentPosMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
    Slider(
      value = progressFrac,
      onValueChange = { frac ->
        audioEngine.seekTo((frac * durationMs).toInt())
      },
      colors = SliderDefaults.colors(
        thumbColor = WalkmanGoldPrimary,
        activeTrackColor = WalkmanGoldPrimary,
        inactiveTrackColor = ObsidianSurfaceElevated
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("player_progress_slider")
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val curMin = (currentPosMs / 1000) / 60
      val curSec = (currentPosMs / 1000) % 60
      val durMin = (durationMs / 1000) / 60
      val durSec = (durationMs / 1000) % 60

      Text(String.format(java.util.Locale.US, "%d:%02d", curMin, curSec), color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
      Text(String.format(java.util.Locale.US, "%d:%02d", durMin, durSec), color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { audioEngine.seekTo(0) },
        modifier = Modifier.size(44.dp)
      ) {
        Icon(Icons.Default.SkipPrevious, contentDescription = "Restart", tint = PureWhite, modifier = Modifier.size(28.dp))
      }

      IconButton(
        onClick = { audioEngine.seekTo(currentPosMs - 10000) },
        modifier = Modifier.size(44.dp)
      ) {
        Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s", tint = PureWhite, modifier = Modifier.size(26.dp))
      }

      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(WalkmanGoldPrimary)
          .clickable { audioEngine.togglePlayPause() }
          .testTag("player_play_pause_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "Pause" else "Play",
          tint = ObsidianBackground,
          modifier = Modifier.size(36.dp)
        )
      }

      IconButton(
        onClick = { audioEngine.seekTo(currentPosMs + 10000) },
        modifier = Modifier.size(44.dp)
      ) {
        Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = PureWhite, modifier = Modifier.size(26.dp))
      }

      IconButton(
        onClick = { audioEngine.seekTo(durationMs) },
        modifier = Modifier.size(44.dp)
      ) {
        Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = PureWhite, modifier = Modifier.size(28.dp))
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
