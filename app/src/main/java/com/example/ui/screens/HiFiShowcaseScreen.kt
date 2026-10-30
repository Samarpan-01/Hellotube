package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.audio.WalkmanAudioEngine
import com.example.data.HiFiContentRepository
import com.example.model.MediaItem
import com.example.ui.theme.HiResYellow
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceBorder
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.VUMeterCyan
import com.example.ui.theme.WalkmanGoldDark
import com.example.ui.theme.WalkmanGoldPrimary

@Composable
fun HiFiShowcaseScreen(
  audioEngine: WalkmanAudioEngine,
  onSelectTrack: (MediaItem) -> Unit,
  onNavigateToPlayer: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("All") }

  val items = remember(selectedCategory) {
    HiFiContentRepository.filter("", selectedCategory)
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
      .testTag("hifi_showcase_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WalkmanGoldPrimary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.hifi_audio_hero_1793384092350),
            contentDescription = "Hi-Fi Audio Hero",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Transparent,
                    ObsidianBackground.copy(alpha = 0.85f),
                    ObsidianBackground
                  )
                )
              )
          )

          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(16.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(HiResYellow)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "HI-RES AUDIO",
                  color = ObsidianBackground,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "STUDIO MASTER SELECTION",
                color = WalkmanGoldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Pristine High-Fidelity Vault",
              color = PureWhite,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )

            Text(
              text = "Uncompressed master recordings engineered to test Reno 11 speakers, DAC & Walkman EQ.",
              color = TextMuted,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        HiFiContentRepository.CATEGORIES.forEach { category ->
          val isSelected = selectedCategory == category
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) WalkmanGoldPrimary else ObsidianSurfaceElevated)
              .border(
                1.dp,
                if (isSelected) WalkmanGoldDark else ObsidianSurfaceBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { selectedCategory = category }
              .padding(horizontal = 14.dp, vertical = 8.dp)
              .testTag("hifi_category_${category.replace(" ", "_")}")
          ) {
            Text(
              text = category,
              color = if (isSelected) ObsidianBackground else PureWhite,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }
    }

    items(items, key = { it.id }) { item ->
      HiFiDetailCard(
        item = item,
        onPlay = {
          onSelectTrack(item)
          audioEngine.playTrack(item)
          onNavigateToPlayer()
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun HiFiDetailCard(
  item: MediaItem,
  onPlay: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianSurfaceBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onPlay)
      .testTag("hifi_card_${item.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(8.dp))
        ) {
          AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play",
              tint = WalkmanGoldPrimary,
              modifier = Modifier.size(32.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(WalkmanGoldPrimary.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = item.category,
                color = WalkmanGoldPrimary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = item.duration,
              color = TextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = item.title,
            color = PureWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = item.artistOrChannel,
            color = TextMuted,
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = item.description,
        color = TextSubtle,
        fontSize = 11.sp,
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(ObsidianBackground)
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.GraphicEq, contentDescription = null, tint = HiResYellow, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(item.sampleRate, color = HiResYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Text(item.codec, color = VUMeterCyan, fontSize = 10.sp, fontWeight = FontWeight.Medium)

        Button(
          onClick = onPlay,
          colors = ButtonDefaults.buttonColors(containerColor = WalkmanGoldPrimary, contentColor = ObsidianBackground),
          shape = RoundedCornerShape(6.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text("Play Hi-Fi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
