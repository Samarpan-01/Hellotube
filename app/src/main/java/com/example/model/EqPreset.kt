package com.example.model

data class EqPreset(
  val name: String,
  val bands: List<Float>, // 10 bands: 31, 62, 125, 250, 500, 1k, 2k, 4k, 8k, 16k in dB (-10.0f to +10.0f)
  val clearBass: Int = 4, // -10 to +10
  val dseeUltimate: Boolean = true,
  val clearAudioPlus: Boolean = true,
  val dcPhaseLinearizer: DcPhaseMode = DcPhaseMode.STANDARD_A,
  val vinylProcessor: VinylMode = VinylMode.SURFACE_WARMTH,
  val dynamicNormalizer: Boolean = false,
  val soundstage: SoundstageMode = SoundstageMode.STUDIO
)

enum class DcPhaseMode(val label: String) {
  OFF("Off"),
  STANDARD_A("Type A Standard"),
  STANDARD_B("Type B Standard"),
  HIGH_A("Type A High"),
  HIGH_B("Type B High")
}

enum class VinylMode(val label: String) {
  OFF("Off"),
  STANDARD("Standard"),
  SURFACE_WARMTH("Surface Warmth"),
  TONEARM_RESONANCE("Tonearm Resonance"),
  GROOVE_DEPTH("Groove Depth")
}

enum class SoundstageMode(val label: String) {
  OFF("Off"),
  STUDIO("Studio Reference"),
  CLUB("Live Club"),
  CONCERT_HALL("Concert Hall"),
  OPEN_ARENA("Open Arena")
}

object WalkmanPresets {
  val BAND_FREQUENCIES = listOf("31", "62", "125", "250", "500", "1k", "2k", "4k", "8k", "16k")

  val PRESETS = listOf(
    EqPreset(
      name = "Sony Signature",
      bands = listOf(3.5f, 2.5f, 1.0f, 0.5f, 0.0f, 0.5f, 1.5f, 2.5f, 3.5f, 4.0f),
      clearBass = 6,
      dseeUltimate = true,
      clearAudioPlus = true,
      dcPhaseLinearizer = DcPhaseMode.STANDARD_A,
      vinylProcessor = VinylMode.SURFACE_WARMTH,
      soundstage = SoundstageMode.STUDIO
    ),
    EqPreset(
      name = "Reference Flat",
      bands = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
      clearBass = 0,
      dseeUltimate = true,
      clearAudioPlus = false,
      dcPhaseLinearizer = DcPhaseMode.OFF,
      vinylProcessor = VinylMode.OFF,
      soundstage = SoundstageMode.OFF
    ),
    EqPreset(
      name = "Bass Impact",
      bands = listOf(7.0f, 6.0f, 4.5f, 2.0f, 0.0f, -0.5f, 0.5f, 1.5f, 2.0f, 2.5f),
      clearBass = 9,
      dseeUltimate = true,
      clearAudioPlus = true,
      dcPhaseLinearizer = DcPhaseMode.STANDARD_B,
      vinylProcessor = VinylMode.GROOVE_DEPTH,
      soundstage = SoundstageMode.CLUB
    ),
    EqPreset(
      name = "Vocal Master",
      bands = listOf(-1.0f, -0.5f, 0.5f, 1.5f, 3.0f, 4.5f, 3.5f, 2.0f, 1.5f, 1.0f),
      clearBass = 2,
      dseeUltimate = true,
      clearAudioPlus = true,
      dcPhaseLinearizer = DcPhaseMode.STANDARD_A,
      vinylProcessor = VinylMode.OFF,
      soundstage = SoundstageMode.CONCERT_HALL
    ),
    EqPreset(
      name = "Analog Vinyl Warmth",
      bands = listOf(3.0f, 2.5f, 2.0f, 1.0f, 0.5f, 0.0f, -0.5f, -1.0f, -1.5f, -2.0f),
      clearBass = 4,
      dseeUltimate = true,
      clearAudioPlus = false,
      dcPhaseLinearizer = DcPhaseMode.HIGH_A,
      vinylProcessor = VinylMode.TONEARM_RESONANCE,
      soundstage = SoundstageMode.STUDIO
    ),
    EqPreset(
      name = "Acoustic Detail",
      bands = listOf(1.5f, 1.0f, 0.5f, 0.0f, 0.5f, 1.5f, 2.5f, 3.5f, 4.5f, 5.0f),
      clearBass = 3,
      dseeUltimate = true,
      clearAudioPlus = true,
      dcPhaseLinearizer = DcPhaseMode.STANDARD_A,
      vinylProcessor = VinylMode.SURFACE_WARMTH,
      soundstage = SoundstageMode.STUDIO
    ),
    EqPreset(
      name = "Live Arena 3D",
      bands = listOf(2.5f, 2.0f, 1.0f, 0.0f, -0.5f, 1.0f, 2.0f, 3.0f, 4.0f, 3.5f),
      clearBass = 5,
      dseeUltimate = true,
      clearAudioPlus = true,
      dcPhaseLinearizer = DcPhaseMode.STANDARD_B,
      vinylProcessor = VinylMode.OFF,
      soundstage = SoundstageMode.OPEN_ARENA
    ),
    EqPreset(
      name = "Custom 1",
      bands = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
      clearBass = 4,
      dseeUltimate = true,
      clearAudioPlus = false,
      dcPhaseLinearizer = DcPhaseMode.STANDARD_A,
      vinylProcessor = VinylMode.OFF,
      soundstage = SoundstageMode.STUDIO
    )
  )
}
