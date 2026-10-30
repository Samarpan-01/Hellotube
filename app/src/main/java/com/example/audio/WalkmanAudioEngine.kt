package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.os.PowerManager
import com.example.model.DcPhaseMode
import com.example.model.EqPreset
import com.example.model.MediaItem
import com.example.model.SoundstageMode
import com.example.model.VinylMode
import com.example.model.WalkmanPresets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class WalkmanAudioEngine(private val context: Context) {

  private val scope = CoroutineScope(Dispatchers.Default + Job())

  // Background play & screen-off WakeLock management
  private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
  private var wakeLock: PowerManager.WakeLock? = null

  private val _isBackgroundPlayEnabled = MutableStateFlow(true)
  val isBackgroundPlayEnabled: StateFlow<Boolean> = _isBackgroundPlayEnabled.asStateFlow()

  // Currently playing item
  private val _currentTrack = MutableStateFlow<MediaItem?>(null)
  val currentTrack: StateFlow<MediaItem?> = _currentTrack.asStateFlow()

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _currentPositionMs = MutableStateFlow(0)
  val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

  private val _durationMs = MutableStateFlow(240000)
  val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

  // Equalizer & Walkman DSP state
  private val _currentPreset = MutableStateFlow(WalkmanPresets.PRESETS[0])
  val currentPreset: StateFlow<EqPreset> = _currentPreset.asStateFlow()

  private val _customPresets = MutableStateFlow<List<EqPreset>>(
    listOf(
      WalkmanPresets.PRESETS.last(),
      EqPreset(
        name = "My Custom Hi-Fi",
        bands = listOf(4.0f, 3.0f, 1.5f, 0.5f, 0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 4.5f),
        clearBass = 6,
        dseeUltimate = true,
        clearAudioPlus = true,
        dcPhaseLinearizer = DcPhaseMode.STANDARD_A,
        vinylProcessor = VinylMode.SURFACE_WARMTH,
        soundstage = SoundstageMode.STUDIO
      )
    )
  )
  val customPresets: StateFlow<List<EqPreset>> = _customPresets.asStateFlow()

  // Real-time VU meter values (0.0f to 1.0f)
  private val _leftChannelVU = MutableStateFlow(0.15f)
  val leftChannelVU: StateFlow<Float> = _leftChannelVU.asStateFlow()

  private val _rightChannelVU = MutableStateFlow(0.18f)
  val rightChannelVU: StateFlow<Float> = _rightChannelVU.asStateFlow()

  // 16-band spectrum visualizer levels (0.0f to 1.0f)
  private val _spectrumBands = MutableStateFlow(List(16) { 0.1f })
  val spectrumBands: StateFlow<List<Float>> = _spectrumBands.asStateFlow()

  // Audio FX hardware objects
  private var mediaPlayer: MediaPlayer? = null
  private var hwEqualizer: Equalizer? = null
  private var hwBassBoost: BassBoost? = null

  // Tone Generator for Audiophile & Developer Testing
  private var toneTrack: AudioTrack? = null
  private var toneJob: Job? = null
  private val _isGeneratingTone = MutableStateFlow(false)
  val isGeneratingTone: StateFlow<Boolean> = _isGeneratingTone.asStateFlow()

  private val _currentToneFrequency = MutableStateFlow(440f)
  val currentToneFrequency: StateFlow<Float> = _currentToneFrequency.asStateFlow()

  private val _toneMode = MutableStateFlow(ToneMode.SINE)
  val toneMode: StateFlow<ToneMode> = _toneMode.asStateFlow()

  private val _toneVolume = MutableStateFlow(0.7f)
  val toneVolume: StateFlow<Float> = _toneVolume.asStateFlow()

  enum class ToneMode {
    SINE,
    WHITE_NOISE,
    PINK_NOISE,
    SWEEP_20_20K
  }

  init {
    startVisualizerLoop()
  }

  private fun startVisualizerLoop() {
    scope.launch {
      var angle = 0.0
      while (isActive) {
        if (_isPlaying.value || _isGeneratingTone.value) {
          angle += 0.25
          val bassBoostFactor = (_currentPreset.value.clearBass / 10f).coerceIn(0f, 1f)
          val baseEnergy = if (_isGeneratingTone.value) 0.75f else 0.55f

          val l = (baseEnergy + 0.35f * sin(angle).toFloat() + 0.1f * sin(angle * 2.3).toFloat()).coerceIn(0.05f, 0.95f)
          val r = (baseEnergy + 0.32f * sin(angle + 0.8).toFloat() + 0.12f * sin(angle * 1.7).toFloat()).coerceIn(0.05f, 0.95f)

          _leftChannelVU.value = l
          _rightChannelVU.value = r

          val eq = _currentPreset.value.bands
          val bands = List(16) { i ->
            val eqIdx = (i * 10 / 16).coerceIn(0, 9)
            val eqGainFactor = (eq[eqIdx] + 10f) / 20f
            val wave = 0.4f + 0.35f * sin(angle * 1.5 + i * 0.45).toFloat() + 0.15f * bassBoostFactor
            (wave * (0.5f + 0.5f * eqGainFactor)).coerceIn(0.05f, 1.0f)
          }
          _spectrumBands.value = bands

          if (_isPlaying.value) {
            val newPos = _currentPositionMs.value + 100
            if (newPos >= _durationMs.value) {
              _currentPositionMs.value = 0
            } else {
              _currentPositionMs.value = newPos
            }
          }
        } else {
          _leftChannelVU.value = (_leftChannelVU.value * 0.85f).coerceAtLeast(0.02f)
          _rightChannelVU.value = (_rightChannelVU.value * 0.85f).coerceAtLeast(0.02f)
          _spectrumBands.value = _spectrumBands.value.map { (it * 0.85f).coerceAtLeast(0.02f) }
        }
        delay(60)
      }
    }
  }

  fun toggleBackgroundPlay(enabled: Boolean) {
    _isBackgroundPlayEnabled.value = enabled
    if (enabled && _isPlaying.value) {
      acquireWakeLock()
    } else if (!enabled) {
      releaseWakeLock()
    }
  }

  private fun acquireWakeLock() {
    try {
      if (wakeLock == null && powerManager != null) {
        wakeLock = powerManager.newWakeLock(
          PowerManager.PARTIAL_WAKE_LOCK,
          "HelloApp:WalkmanAudioWakeLock"
        ).apply {
          setReferenceCounted(false)
        }
      }
      wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes timeout safety
    } catch (_: Exception) {}
  }

  private fun releaseWakeLock() {
    try {
      if (wakeLock?.isHeld == true) {
        wakeLock?.release()
      }
    } catch (_: Exception) {}
  }

  fun playTrack(item: MediaItem) {
    _currentTrack.value = item
    _currentPositionMs.value = 0
    _isPlaying.value = true
    if (_isBackgroundPlayEnabled.value) {
      acquireWakeLock()
    }
  }

  fun togglePlayPause() {
    val willPlay = !_isPlaying.value
    _isPlaying.value = willPlay
    if (willPlay && _isBackgroundPlayEnabled.value) {
      acquireWakeLock()
    } else {
      releaseWakeLock()
    }
  }

  fun seekTo(positionMs: Int) {
    _currentPositionMs.value = positionMs.coerceIn(0, _durationMs.value)
  }

  // --- EQ & PRESET MANAGEMENT ---
  fun selectPreset(preset: EqPreset) {
    _currentPreset.value = preset
    applyHardwareEffects(preset)
  }

  fun updateBand(index: Int, gainDb: Float) {
    val current = _currentPreset.value
    val newBands = current.bands.toMutableList()
    if (index in newBands.indices) {
      newBands[index] = gainDb.coerceIn(-10.0f, 10.0f)
      val updated = current.copy(name = if (current.name.startsWith("Custom")) current.name else "Custom", bands = newBands)
      _currentPreset.value = updated
      applyHardwareEffects(updated)
    }
  }

  fun updateClearBass(level: Int) {
    val updated = _currentPreset.value.copy(clearBass = level.coerceIn(-10, 10))
    _currentPreset.value = updated
    applyHardwareEffects(updated)
  }

  fun toggleDseeUltimate(enabled: Boolean) {
    _currentPreset.value = _currentPreset.value.copy(dseeUltimate = enabled)
  }

  fun toggleClearAudioPlus(enabled: Boolean) {
    _currentPreset.value = _currentPreset.value.copy(clearAudioPlus = enabled)
  }

  fun setDcPhaseMode(mode: DcPhaseMode) {
    _currentPreset.value = _currentPreset.value.copy(dcPhaseLinearizer = mode)
  }

  fun setVinylMode(mode: VinylMode) {
    _currentPreset.value = _currentPreset.value.copy(vinylProcessor = mode)
  }

  fun toggleDynamicNormalizer(enabled: Boolean) {
    _currentPreset.value = _currentPreset.value.copy(dynamicNormalizer = enabled)
  }

  fun setSoundstageMode(mode: SoundstageMode) {
    _currentPreset.value = _currentPreset.value.copy(soundstage = mode)
  }

  fun saveCustomPreset(presetName: String) {
    val name = presetName.trim().ifEmpty { "Custom Preset ${_customPresets.value.size + 1}" }
    val newPreset = _currentPreset.value.copy(name = name)
    val list = _customPresets.value.toMutableList()
    val existingIdx = list.indexOfFirst { it.name.equals(name, ignoreCase = true) }
    if (existingIdx != -1) {
      list[existingIdx] = newPreset
    } else {
      list.add(newPreset)
    }
    _customPresets.value = list
    _currentPreset.value = newPreset
  }

  fun deleteCustomPreset(preset: EqPreset) {
    val list = _customPresets.value.toMutableList()
    list.removeAll { it.name == preset.name }
    _customPresets.value = list
    if (_currentPreset.value.name == preset.name) {
      selectPreset(WalkmanPresets.PRESETS[0])
    }
  }

  fun resetToFlat() {
    selectPreset(WalkmanPresets.PRESETS[1])
  }

  private fun applyHardwareEffects(preset: EqPreset) {
    try {
      hwBassBoost?.let { bb ->
        if (bb.strengthSupported) {
          val strength = ((preset.clearBass.coerceAtLeast(0) / 10f) * 1000).toInt().toShort()
          bb.setStrength(strength)
          bb.enabled = preset.clearBass > 0
        }
      }
    } catch (_: Exception) {}
  }

  // --- DEVELOPER TONE & SWEEP GENERATOR (PCM AudioTrack) ---
  fun startToneGenerator(mode: ToneMode = ToneMode.SINE, frequency: Float = 440f) {
    stopToneGenerator()
    _toneMode.value = mode
    _currentToneFrequency.value = frequency
    _isGeneratingTone.value = true

    toneJob = scope.launch(Dispatchers.Default) {
      val sampleRate = 48000
      val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT
      )

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
            .build()
        )
        .setBufferSizeInBytes(bufferSize * 2)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .build()

      toneTrack = track
      track.play()

      val numSamples = bufferSize / 2
      val buffer = ShortArray(numSamples)
      var phase = 0.0
      var sweepFreq = 20.0

      var b0 = 0.0
      var b1 = 0.0
      var b2 = 0.0

      while (isActive && _isGeneratingTone.value) {
        val vol = _toneVolume.value.coerceIn(0f, 1f)
        val modeVal = _toneMode.value

        when (modeVal) {
          ToneMode.SINE -> {
            val freq = _currentToneFrequency.value
            val phaseInc = 2.0 * PI * freq / sampleRate
            for (i in 0 until numSamples step 2) {
              val sample = (sin(phase) * 32767.0 * vol).toInt().toShort()
              buffer[i] = sample
              buffer[i + 1] = sample
              phase += phaseInc
              if (phase > 2.0 * PI) phase -= 2.0 * PI
            }
          }
          ToneMode.WHITE_NOISE -> {
            for (i in 0 until numSamples step 2) {
              val sample = ((Math.random() * 2.0 - 1.0) * 32767.0 * vol).toInt().toShort()
              buffer[i] = sample
              buffer[i + 1] = sample
            }
          }
          ToneMode.PINK_NOISE -> {
            for (i in 0 until numSamples step 2) {
              val white = Math.random() * 2.0 - 1.0
              b0 = 0.99886 * b0 + white * 0.0555179
              b1 = 0.99332 * b1 + white * 0.0750759
              b2 = 0.96900 * b2 + white * 0.1538520
              val pink = b0 + b1 + b2 + white * 0.5362
              val sample = ((pink * 0.15) * 32767.0 * vol).toInt().coerceIn(-32768, 32767).toShort()
              buffer[i] = sample
              buffer[i + 1] = sample
            }
          }
          ToneMode.SWEEP_20_20K -> {
            sweepFreq *= 1.0003
            if (sweepFreq > 20000.0) sweepFreq = 20.0
            _currentToneFrequency.value = sweepFreq.toFloat()
            val phaseInc = 2.0 * PI * sweepFreq / sampleRate
            for (i in 0 until numSamples step 2) {
              val sample = (sin(phase) * 32767.0 * vol).toInt().toShort()
              buffer[i] = sample
              buffer[i + 1] = sample
              phase += phaseInc
              if (phase > 2.0 * PI) phase -= 2.0 * PI
            }
          }
        }

        track.write(buffer, 0, numSamples)
      }

      try {
        track.stop()
        track.release()
      } catch (_: Exception) {}
    }
  }

  fun updateToneFrequency(freq: Float) {
    _currentToneFrequency.value = freq
  }

  fun updateToneVolume(vol: Float) {
    _toneVolume.value = vol
  }

  fun stopToneGenerator() {
    _isGeneratingTone.value = false
    toneJob?.cancel()
    toneJob = null
    try {
      toneTrack?.stop()
      toneTrack?.release()
    } catch (_: Exception) {}
    toneTrack = null
  }

  fun release() {
    releaseWakeLock()
    stopToneGenerator()
    try {
      hwEqualizer?.release()
      hwBassBoost?.release()
      mediaPlayer?.release()
    } catch (_: Exception) {}
  }
}
