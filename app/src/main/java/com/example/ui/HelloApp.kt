package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.WalkmanAudioEngine
import com.example.data.HiFiContentRepository
import com.example.model.MediaItem
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.NowPlayingView
import com.example.ui.screens.DevAudioLabScreen
import com.example.ui.screens.HiFiShowcaseScreen
import com.example.ui.screens.VideoMirrorScreen
import com.example.ui.screens.WalkmanEqScreen
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VUMeterCyan
import com.example.ui.theme.WalkmanGoldDark
import com.example.ui.theme.WalkmanGoldPrimary

enum class HelloTab(val title: String, val icon: ImageVector) {
  MIRROR("Mirror", Icons.Default.PlayCircle),
  HIFI_VAULT("Hi-Fi", Icons.Default.Star),
  WALKMAN_EQ("Walkman EQ", Icons.Default.GraphicEq),
  DEV_LAB("Dev Lab", Icons.Default.BugReport)
}

@Composable
fun HelloApp(
  audioEngine: WalkmanAudioEngine,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(HelloTab.MIRROR) }
  var isPlayerExpanded by remember { mutableStateOf(false) }

  val currentTrack by audioEngine.currentTrack.collectAsState()
  val isPlaying by audioEngine.isPlaying.collectAsState()
  var activeVideo by remember { mutableStateOf<MediaItem?>(HiFiContentRepository.CURATED_ITEMS.first()) }

  // Handle hardware Back press safely
  BackHandler(enabled = isPlayerExpanded || selectedTab != HelloTab.MIRROR) {
    if (isPlayerExpanded) {
      isPlayerExpanded = false
    } else if (selectedTab != HelloTab.MIRROR) {
      selectedTab = HelloTab.MIRROR
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
      .statusBarsPadding()
  ) {
    Scaffold(
      containerColor = ObsidianBackground,
      bottomBar = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
        ) {
          // Mini-player bar when track is present and player is not expanded
          val track = currentTrack ?: activeVideo
          if (track != null && !isPlayerExpanded) {
            MiniPlayerBar(
              currentTrack = track,
              isPlaying = isPlaying,
              onTogglePlay = { audioEngine.togglePlayPause() },
              onBarClick = { isPlayerExpanded = true }
            )
          }

          // Main Navigation Bar
          NavigationBar(
            containerColor = ObsidianSurface,
            tonalElevation = 8.dp,
            modifier = Modifier
              .fillMaxWidth()
              .border(0.5.dp, ObsidianSurfaceBorder)
              .testTag("main_navigation_bar")
          ) {
            HelloTab.values().forEach { tab ->
              val isSelected = selectedTab == tab
              NavigationBarItem(
                selected = isSelected,
                onClick = { selectedTab = tab },
                icon = {
                  Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.title,
                    tint = if (isSelected) WalkmanGoldPrimary else TextMuted
                  )
                },
                label = {
                  Text(
                    text = tab.title,
                    color = if (isSelected) WalkmanGoldPrimary else TextMuted,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = WalkmanGoldDark.copy(alpha = 0.4f)
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
              )
            }
          }
        }
      }
    ) { paddingValues ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
      ) {
        when (selectedTab) {
          HelloTab.MIRROR -> {
            VideoMirrorScreen(
              audioEngine = audioEngine,
              activeVideo = activeVideo,
              onSelectVideo = { item ->
                activeVideo = item
              },
              onNavigateToEq = { selectedTab = HelloTab.WALKMAN_EQ }
            )
          }
          HelloTab.HIFI_VAULT -> {
            HiFiShowcaseScreen(
              audioEngine = audioEngine,
              onSelectTrack = { item ->
                activeVideo = item
              },
              onNavigateToPlayer = {
                isPlayerExpanded = true
              }
            )
          }
          HelloTab.WALKMAN_EQ -> {
            WalkmanEqScreen(audioEngine = audioEngine)
          }
          HelloTab.DEV_LAB -> {
            DevAudioLabScreen(audioEngine = audioEngine)
          }
        }
      }
    }

    // Modal Full-Screen Now Playing View
    AnimatedVisibility(
      visible = isPlayerExpanded,
      enter = slideInVertically(initialOffsetY = { it }),
      exit = slideOutVertically(targetOffsetY = { it })
    ) {
      val track = currentTrack ?: activeVideo ?: HiFiContentRepository.CURATED_ITEMS.first()
      NowPlayingView(
        audioEngine = audioEngine,
        currentTrack = track,
        onDismiss = { isPlayerExpanded = false },
        onNavigateToEq = {
          isPlayerExpanded = false
          selectedTab = HelloTab.WALKMAN_EQ
        }
      )
    }
  }
}
