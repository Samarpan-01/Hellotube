package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adblock.AdBlockManager
import com.example.device.Reno11DeviceInspector
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceBorder
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WalkmanGoldPrimary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AdFreePlayerView(
  videoUrl: String,
  modifier: Modifier = Modifier,
  onVideoLoaded: () -> Unit = {}
) {
  var isLoading by remember { mutableStateOf(true) }
  var hasRenderError by remember { mutableStateOf(false) }
  var lastLoadedTargetUrl by remember { mutableStateOf("") }
  var reloadTrigger by remember { mutableIntStateOf(0) }
  var useSoftwareLayer by remember { mutableStateOf(Reno11DeviceInspector.isEmulatorEnvironment()) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(16f / 9f)
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surface)
      .testTag("ad_free_player_container")
  ) {
    if (!hasRenderError) {
      AndroidView<WebView>(
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
          createConfiguredWebView(
            context = context,
            targetUrl = videoUrl,
            useSoftwareRendering = useSoftwareLayer,
            onLoadingStateChanged = { loading -> isLoading = loading },
            onLoaded = { onVideoLoaded() },
            onRenderGone = {
              // If GPU render process fails (e.g. rendernode missing in emulator),
              // fall back to software rendering safely instead of crashing
              useSoftwareLayer = true
              hasRenderError = true
            }
          ).also {
            lastLoadedTargetUrl = videoUrl
          }
        },
        update = { wv: WebView ->
          // Avoid reload loop: only load when the target prop changes, never check wv.url
          // because embedded players redirect and change their internal URL.
          if (lastLoadedTargetUrl != videoUrl) {
            lastLoadedTargetUrl = videoUrl
            isLoading = true
            loadVideoContent(wv, videoUrl)
          }
        },
        onRelease = { wv: WebView ->
          try {
            wv.stopLoading()
            wv.loadUrl("about:blank")
            wv.destroy()
          } catch (_: Exception) {}
        }
      )
    } else {
      // Fallback display if render process fails
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(ObsidianBackground)
          .border(1.dp, ObsidianSurfaceBorder, RoundedCornerShape(12.dp))
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = WalkmanGoldPrimary,
            modifier = Modifier.size(32.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Video Engine Safe Mode",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = "Graphics driver fallback activated for virtualized container.",
            color = TextMuted,
            fontSize = 11.sp,
            modifier = Modifier.padding(vertical = 4.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              hasRenderError = false
              useSoftwareLayer = true
              reloadTrigger += 1
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = WalkmanGoldPrimary,
              contentColor = ObsidianBackground
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("reload_player_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Resume Stream", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    if (isLoading && !hasRenderError) {
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

private fun extractVideoId(url: String): String {
  return when {
    url.contains("embed/") -> url.substringAfter("embed/").substringBefore("?").substringBefore("&")
    url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
    url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
    else -> url.substringAfterLast("/").substringBefore("?")
  }
}

private fun buildResponsiveEmbedHtml(videoId: String): String {
  return """
    <!DOCTYPE html>
    <html style="background:#000;margin:0;padding:0;width:100%;height:100%;overflow:hidden;">
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
      <style>
        body, html { margin:0; padding:0; width:100%; height:100%; background:#000; overflow:hidden; }
        iframe { width:100%; height:100%; border:none; display:block; }
      </style>
    </head>
    <body>
      <iframe
        src="https://www.youtube-nocookie.com/embed/$videoId?autoplay=1&playsinline=1&rel=0&modestbranding=1&enablejsapi=1&fs=0"
        allow="autoplay; encrypted-media; picture-in-picture"
        allowfullscreen>
      </iframe>
    </body>
    </html>
  """.trimIndent()
}

private fun loadVideoContent(webView: WebView, videoUrl: String) {
  val videoId = extractVideoId(videoUrl)
  if (videoId.isNotBlank() && videoId.length in 5..25 && !videoId.contains(".")) {
    val html = buildResponsiveEmbedHtml(videoId)
    webView.loadDataWithBaseURL("https://www.youtube-nocookie.com", html, "text/html", "UTF-8", null)
  } else {
    webView.loadUrl(videoUrl)
  }
}

@SuppressLint("SetJavaScriptEnabled")
private fun createConfiguredWebView(
  context: Context,
  targetUrl: String,
  useSoftwareRendering: Boolean,
  onLoadingStateChanged: (Boolean) -> Unit,
  onLoaded: () -> Unit,
  onRenderGone: () -> Unit
): WebView {
  return WebView(context).apply {
    layoutParams = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )

    // In virtualized / container environments without DRM rendernodes,
    // software layer prevents driver-level rendernode errors.
    val isEmulator = Reno11DeviceInspector.isEmulatorEnvironment()
    if (isEmulator || useSoftwareRendering) {
      setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    settings.apply {
      javaScriptEnabled = true
      domStorageEnabled = true
      mediaPlaybackRequiresUserGesture = false
      loadWithOverviewMode = true
      useWideViewPort = true
      cacheMode = WebSettings.LOAD_DEFAULT
      allowFileAccess = false
      allowContentAccess = false

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
      }

      // Remove '; wv' from user agent so YouTube HTML5 player does not degrade video streams
      try {
        val original = userAgentString
        if (original.contains("; wv")) {
          userAgentString = original.replace("; wv", "")
        }
      } catch (_: Exception) {}
    }

    webChromeClient = object : WebChromeClient() {
      override fun onProgressChanged(view: WebView?, newProgress: Int) {
        if (newProgress > 70) {
          onLoadingStateChanged(false)
          onLoaded()
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

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onLoadingStateChanged(false)
        view?.evaluateJavascript(AdBlockManager.AD_SKIP_SCRIPT, null)
      }

      override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
      ) {
        super.onReceivedError(view, request, error)
        if (request?.isForMainFrame == true) {
          onLoadingStateChanged(false)
        }
      }

      override fun onRenderProcessGone(
        view: WebView?,
        detail: RenderProcessGoneDetail?
      ): Boolean {
        // CRITICAL: Must return true so Android OS does not kill the host application!
        view?.let { wv ->
          (wv.parent as? ViewGroup)?.removeView(wv)
          try {
            wv.destroy()
          } catch (_: Exception) {}
        }
        onRenderGone()
        return true
      }
    }

    loadVideoContent(this, targetUrl)
  }
}

