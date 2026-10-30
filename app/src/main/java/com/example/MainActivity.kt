package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.audio.WalkmanAudioEngine
import com.example.ui.HelloApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBackground

class MainActivity : ComponentActivity() {

  private lateinit var audioEngine: WalkmanAudioEngine

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    audioEngine = WalkmanAudioEngine(applicationContext)

    setContent {
      MyApplicationTheme(darkTheme = true) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = ObsidianBackground
        ) {
          HelloApp(audioEngine = audioEngine)
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::audioEngine.isInitialized) {
      audioEngine.release()
    }
  }
}
