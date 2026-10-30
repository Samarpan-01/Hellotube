package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.adblock.AdBlockManager
import com.example.audio.WalkmanAudioEngine
import com.example.data.HiFiContentRepository
import com.example.model.MediaItem
import com.example.ui.components.AdFreePlayerView
import com.example.ui.theme.HiResYellow
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TubeAmber
import com.example.ui.theme.VUMeterCyan
import com.example.ui.theme.WalkmanGoldPrimary

@Composable
fun VideoMirrorScreen(
  audioEngine: WalkmanAudioEngine,
  activeVideo: MediaItem?,
  onSelectVideo: (MediaItem) -> Unit,
  onNavigateToEq: () -> Unit,
  modifier: Modifier = Modifier
) {
  val clipboardManager = LocalClipboardManager.current
  var searchQuery by remember { mutableStateOf("") }
  val blockedAdsCount by AdBlockManager.blockedAdsCount.collectAsState()
  val currentPreset by audioEngine.currentPreset.collectAsState()

  val filteredVideos = remember(searchQuery) {
    HiFiContentRepository.filter(searchQuery, "All")
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
      .testTag("video_mirror_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header & Ad-Shield Status
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "HELLO",
              color = WalkmanGoldPrimary,
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(VUMeterCyan.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "MIRROR",
                color = VUMeterCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Text(
            text = "Zero Ads • Hi-Fi Audio • Walkman EQ",
            color = TextMuted,
            fontSize = 12.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ObsidianSurfaceElevated)
            .border(1.dp, VUMeterCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("ad_shield_badge")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Ad shield",
              tint = VUMeterCyan,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "$blockedAdsCount ADS BLOCKED",
              color = VUMeterCyan,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }

    // Active Ad-Free Player View
    if (activeVideo != null) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, WalkmanGoldPrimary.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            AdFreePlayerView(
              videoUrl = activeVideo.videoUrl,
              onVideoLoaded = {
                audioEngine.playTrack(activeVideo)
              }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = activeVideo.title,
              color = PureWhite,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 2
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${activeVideo.artistOrChannel} • ${activeVideo.viewCount}",
                color = TextMuted,
                fontSize = 12.sp
              )

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(HiResYellow.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = activeVideo.sampleRate,
                  color = HiResYellow,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianBackground)
                .padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.GraphicEq,
                  contentDescription = null,
                  tint = WalkmanGoldPrimary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "EQ: ${currentPreset.name}",
                  color = PureWhite,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }

              Button(
                onClick = onNavigateToEq,
                colors = ButtonDefaults.buttonColors(
                  containerColor = WalkmanGoldPrimary,
                  contentColor = ObsidianBackground
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("tune_eq_button")
              ) {
                Text("Tune EQ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Search & Direct URL Input Box
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search YouTube or paste URL...", color = TextSubtle, fontSize = 13.sp) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
              }
            } else {
              IconButton(
                onClick = {
                  val text = clipboardManager.getText()?.text
                  if (!text.isNullOrEmpty()) {
                    searchQuery = text
                    val item = HiFiContentRepository.parseCustomYouTubeUrl(text)
                    onSelectVideo(item)
                  }
                },
                modifier = Modifier.testTag("paste_url_button")
              ) {
                Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = WalkmanGoldPrimary)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WalkmanGoldPrimary,
            unfocusedBorderColor = ObsidianSurfaceBorder,
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("search_video_input")
        )

        if (searchQuery.isNotEmpty() && (searchQuery.contains("http") || searchQuery.contains("youtube"))) {
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val item = HiFiContentRepository.parseCustomYouTubeUrl(searchQuery)
              onSelectVideo(item)
            },
            colors = ButtonDefaults.buttonColors(containerColor = WalkmanGoldPrimary, contentColor = ObsidianBackground),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("play_custom_url_button")
          ) {
            Text("Stream", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Feed Section Title
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (searchQuery.isEmpty()) "AD-FREE RECOMMENDED STREAMS" else "SEARCH RESULTS",
          color = TextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )

        Text(
          text = "${filteredVideos.size} Videos",
          color = TextSubtle,
          fontSize = 11.sp
        )
      }
    }

    items(filteredVideos, key = { it.id }) { video ->
      VideoFeedCard(
        video = video,
        isPlaying = activeVideo?.id == video.id,
        onPlayClick = {
          onSelectVideo(video)
          audioEngine.playTrack(video)
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun VideoFeedCard(
  video: MediaItem,
  isPlaying: Boolean,
  onPlayClick: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = if (isPlaying) ObsidianSurfaceElevated else ObsidianSurface
    ),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isPlaying) WalkmanGoldPrimary else ObsidianSurfaceBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onPlayClick)
      .testTag("video_card_${video.id}")
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        AsyncImage(
          model = video.thumbnailUrl,
          contentDescription = video.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(ObsidianBackground.copy(alpha = 0.85f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = video.duration,
            color = PureWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Box(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(ObsidianBackground.copy(alpha = 0.85f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = VUMeterCyan, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("NO ADS", color = VUMeterCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }

        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isPlaying) WalkmanGoldPrimary else ObsidianBackground.copy(alpha = 0.75f))
            .align(Alignment.Center),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = if (isPlaying) ObsidianBackground else PureWhite,
            modifier = Modifier.size(28.dp)
          )
        }
      }

      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = video.title,
          color = PureWhite,
          fontSize = 14.sp,
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
            text = "${video.artistOrChannel} • ${video.viewCount}",
            color = TextMuted,
            fontSize = 11.sp
          )

          Text(
            text = video.codec,
            color = TubeAmber,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
