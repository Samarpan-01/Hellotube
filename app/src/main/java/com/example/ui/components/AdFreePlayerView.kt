package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adblock.AdBlockManager
import com.example.ui.theme.WalkmanGoldPrimary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AdFreePlayerView(
  videoUrl: String,
  modifier: Modifier = Modifier,
  onVideoLoaded: () -> Unit = {}
) {
  var isLoading by remember { mutableStateOf(true) }
  var webViewInstance by remember { mutableStateOf<WebView?>(null) }

  DisposableEffect(Unit) {
    onDispose {
      webViewInstance?.let { wv ->
        wv.stopLoading()
        wv.loadUrl("about:blank")
        wv.destroy()
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(16f / 9f)
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surface)
      .testTag("ad_free_player_container")
  ) {
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { context ->
        WebView(context).apply {
          webViewInstance = this
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )

          settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = false
            allowContentAccess = false
            databaseEnabled = true
          }

          webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
              if (newProgress > 75) {
                isLoading = false
                onVideoLoaded()
              }
            }
          }

          webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
              view: WebView?,
              request: WebResourceRequest?
            ): WebResourceResponse? {
              request?.let {
                if (AdBlockManager.shouldBlock(it)) {
                  return AdBlockManager.createEmptyResponse()
                }
              }
              return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              super.onPageStarted(view, url, favicon)
              val cssInjection = "javascript:(function() {" +
                  "var style = document.createElement('style');" +
                  "style.type = 'text/css';" +
                  "style.innerHTML = '" + AdBlockManager.AD_BLOCK_CSS.replace("\n", " ").replace("'", "\\'") + "';" +
                  "document.head.appendChild(style);" +
                  "})()"
              view?.loadUrl(cssInjection)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              super.onPageFinished(view, url)
              isLoading = false
              view?.evaluateJavascript(AdBlockManager.AD_SKIP_SCRIPT, null)
            }
          }

          loadUrl(videoUrl)
        }
      },
      update = { wv ->
        if (wv.url != videoUrl) {
          isLoading = true
          wv.loadUrl(videoUrl)
        }
      }
    )

    if (isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          color = WalkmanGoldPrimary,
          strokeWidth = 3.dp,
          modifier = Modifier.testTag("player_loading_indicator")
        )
      }
    }
  }
}
