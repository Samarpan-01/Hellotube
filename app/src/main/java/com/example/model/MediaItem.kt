package com.example.model

data class MediaItem(
  val id: String,
  val title: String,
  val artistOrChannel: String,
  val duration: String,
  val thumbnailUrl: String,
  val videoUrl: String,
  val isHiResAudio: Boolean = true,
  val sampleRate: String = "96kHz / 24-bit",
  val codec: String = "FLAC / Opus HD",
  val category: String = "Audiophile",
  val description: String = "",
  val viewCount: String = "1.2M views",
  val isLocalSample: Boolean = false
)

enum class PlayerMode {
  VIDEO,
  AUDIO_ONLY,
  MINI_PLAYER,
  FULL_SCREEN
}
