package com.example.data

import com.example.model.MediaItem

object HiFiContentRepository {

  val CURATED_ITEMS = listOf(
    MediaItem(
      id = "hifi_1",
      title = "Audiophile Acoustic Master (24-bit / 192kHz DSD)",
      artistOrChannel = "Sony Classical Studio Masters",
      duration = "4:42",
      thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://www.youtube.com/embed/jfKfPfyJRdk?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "192 kHz / 24-bit PCM",
      codec = "DSD256 / Direct Stream",
      category = "Audiophile Master",
      description = "Pristine acoustic guitar and intimate vocals recorded in direct DSD256 master quality. Optimized for Walkman DSEE Ultimate upscaling.",
      viewCount = "3.4M views"
    ),
    MediaItem(
      id = "hifi_2",
      title = "4K HDR 60fps Colors of Nature & Cinematic Symphony",
      artistOrChannel = "Ultra Cinema 4K",
      duration = "6:15",
      thumbnailUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://www.youtube.com/embed/LXb3EKWsInQ?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "96 kHz / 24-bit",
      codec = "AV1 4K HDR • Opus Master",
      category = "4K HDR Visuals",
      description = "Breathtaking 4K HDR landscapes calibrated for Reno 11 AMOLED 120Hz display with uncompressed surround acoustics.",
      viewCount = "9.8M views"
    ),
    MediaItem(
      id = "hifi_3",
      title = "Sub-Bass 20Hz - 120Hz Audiophile Transient Drop Test",
      artistOrChannel = "Acoustic Physics Lab",
      duration = "3:30",
      thumbnailUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://www.youtube.com/embed/kJQP7kiw5Fk?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "96 kHz / 24-bit",
      codec = "Linear PCM Sub-Bass",
      category = "Bass Calibration",
      description = "Engineered sub-bass test tone sweeps from 20Hz to 120Hz to test Reno 11 speaker and headphone dynamic bass drivers with Sony Clear Bass.",
      viewCount = "1.8M views"
    ),
    MediaItem(
      id = "hifi_4",
      title = "Binaural 3D Holographic Spatial Audio for Headphones",
      artistOrChannel = "Immersive Soundworks",
      duration = "7:10",
      thumbnailUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://www.youtube.com/embed/21X5lGlDOfg?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "96 kHz / 24-bit",
      codec = "3D Ambisonic • Opus",
      category = "3D Spatial Audio",
      description = "True binaural dummy-head microphone recording creating pin-point 360-degree positional audio imaging.",
      viewCount = "4.2M views"
    ),
    MediaItem(
      id = "hifi_5",
      title = "Tokyo Midnight Lo-Fi Beats & Analog Vinyl Warmth",
      artistOrChannel = "ChilledCow Audiophile Sessions",
      duration = "5:20",
      thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://www.youtube.com/embed/5qap5aO4i9A?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "88.2 kHz / 24-bit",
      codec = "FLAC Vinyl Transfer",
      category = "Analog Vinyl",
      description = "Analog tape mastering with authentic vinyl crackle and rich tube preamp harmonic distortion.",
      viewCount = "12.5M views"
    ),
    MediaItem(
      id = "hifi_6",
      title = "London Philharmonic Live Orchestral Symphony - Hans Zimmer Live",
      artistOrChannel = "Philharmonic Masterworks",
      duration = "9:45",
      thumbnailUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://www.youtube.com/embed/s7L2PVdrb_8?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "192 kHz / 24-bit PCM",
      codec = "Pure FLAC Live Master",
      category = "Audiophile Master",
      description = "Full 80-piece orchestra recorded in Royal Albert Hall with wide dynamic range (DR14).",
      viewCount = "7.1M views"
    ),
    MediaItem(
      id = "hifi_7",
      title = "Cyberpunk Synthwave & Deep Analog Bass Odyssey",
      artistOrChannel = "RetroWave Hi-Fi",
      duration = "5:50",
      thumbnailUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://www.youtube.com/embed/mAKsZ26SabQ?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "96 kHz / 24-bit",
      codec = "Moog Sub37 Analog PCM",
      category = "Bass Calibration",
      description = "Heavy analog Moog synthesizer basslines that exercise deep dynamic range and driver excursion.",
      viewCount = "2.9M views"
    ),
    MediaItem(
      id = "hifi_8",
      title = "Nordic Forest Rainstorm & Binaural Acoustic Resonance",
      artistOrChannel = "Earth Audio Labs",
      duration = "8:30",
      thumbnailUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://www.youtube.com/embed/DWcJFNfaw9c?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "96 kHz / 24-bit",
      codec = "Natural High-Res Ambient",
      category = "3D Spatial Audio",
      description = "Ultra high frequency atmospheric water droplets and thunder captured with precision ribbon microphones.",
      viewCount = "5.6M views"
    )
  )

  val CATEGORIES = listOf(
    "All",
    "Audiophile Master",
    "4K HDR Visuals",
    "Bass Calibration",
    "3D Spatial Audio",
    "Analog Vinyl"
  )

  fun filter(query: String, category: String): List<MediaItem> {
    return CURATED_ITEMS.filter { item ->
      val matchesCategory = category == "All" || item.category == category
      val matchesQuery = query.isEmpty() ||
          item.title.contains(query, ignoreCase = true) ||
          item.artistOrChannel.contains(query, ignoreCase = true) ||
          item.category.contains(query, ignoreCase = true)
      matchesCategory && matchesQuery
    }
  }

  fun parseCustomYouTubeUrl(input: String): MediaItem {
    val trimmed = input.trim()
    val videoId = when {
      trimmed.contains("v=") -> trimmed.substringAfter("v=").substringBefore("&")
      trimmed.contains("youtu.be/") -> trimmed.substringAfter("youtu.be/").substringBefore("?")
      trimmed.contains("embed/") -> trimmed.substringAfter("embed/").substringBefore("?")
      else -> trimmed
    }

    val finalId = if (videoId.isNotEmpty()) videoId else "jfKfPfyJRdk"
    return MediaItem(
      id = "yt_$finalId",
      title = if (input.contains("http")) "Custom Stream: $finalId" else "Search: $trimmed",
      artistOrChannel = "YouTube Direct Stream",
      duration = "Live / HD",
      thumbnailUrl = "https://img.youtube.com/vi/$finalId/hqdefault.jpg",
      videoUrl = "https://www.youtube.com/embed/$finalId?autoplay=1&rel=0&modestbranding=1",
      isHiResAudio = true,
      sampleRate = "48 kHz / 24-bit HD",
      codec = "VP9 / Opus Stream",
      category = "YouTube Stream",
      description = "Ad-free playback stream processed through Sony Walkman Hi-Fi engine."
    )
  }
}
