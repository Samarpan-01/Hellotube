package com.example.adblock

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayInputStream

object AdBlockManager {
  private val _blockedAdsCount = MutableStateFlow(0)
  val blockedAdsCount: StateFlow<Int> = _blockedAdsCount.asStateFlow()

  private val _blockedTrackersCount = MutableStateFlow(0)
  val blockedTrackersCount: StateFlow<Int> = _blockedTrackersCount.asStateFlow()

  private val _recentBlockedDomains = MutableStateFlow<List<String>>(emptyList())
  val recentBlockedDomains: StateFlow<List<String>> = _recentBlockedDomains.asStateFlow()

  var isAdBlockEnabled: Boolean = true

  private val AD_HOSTS = listOf(
    "googleads.g.doubleclick.net",
    "pagead2.googlesyndication.com",
    "adservice.google.com",
    "youtube.com/pagead",
    "youtube.com/api/stats/ads",
    "youtube.com/get_midroll_info",
    "youtube.com/ptracking",
    "youtube.com/api/stats/qoe?adformat",
    "play.google.com/log",
    "ade.googlesyndication.com",
    "static.doubleclick.net",
    "ad.doubleclick.net",
    "stats_ads"
  )

  val AD_BLOCK_CSS = """
    .video-ads,
    .ytp-ad-module,
    .ytp-ad-overlay-container,
    .ytp-ad-player-overlay,
    .ytp-ad-player-overlay-layout,
    .ytp-ad-skip-button-slot,
    .ytp-ad-preview-container,
    .ytp-ad-text,
    .ytd-promoted-video-renderer,
    .ytd-promoted-sparkles-web-renderer,
    .ytd-ad-slot-renderer,
    .ytd-banner-promo-renderer,
    .ytd-statement-banner-renderer,
    .ytd-in-feed-ad-layout-renderer,
    .ytd-player-legacy-desktop-watch-ads-renderer,
    #masthead-ad,
    #player-ads,
    ytm-promoted-sparkles-web-renderer,
    ytm-companion-ad-renderer,
    ytm-promoted-video-renderer,
    .ytm-ad-slot-renderer,
    ytm-paid-content-overlay-renderer {
      display: none !important;
      visibility: hidden !important;
      opacity: 0 !important;
      pointer-events: none !important;
      height: 0px !important;
      width: 0px !important;
    }
  """.trimIndent()

  val AD_SKIP_SCRIPT = """
    (function() {
      function killAds() {
        const video = document.querySelector('video');
        const adShowing = document.querySelector('.ad-showing, .ad-interrupting');
        
        if (adShowing && video) {
          if (video.duration && !isNaN(video.duration)) {
            video.currentTime = video.duration;
          }
        }
        
        const skipButtons = document.querySelectorAll(
          '.ytp-ad-skip-button, .ytp-ad-skip-button-modern, .ytp-skip-ad-button, .videoAdUiSkipButton, .ytm-ad-skip-button'
        );
        skipButtons.forEach(btn => {
          if (btn) btn.click();
        });
        
        const overlays = document.querySelectorAll('.ytp-ad-overlay-close-button');
        overlays.forEach(btn => {
          if (btn) btn.click();
        });
      }
      
      setInterval(killAds, 250);
      
      const style = document.createElement('style');
      style.type = 'text/css';
      style.innerHTML = `${AD_BLOCK_CSS.replace("\n", " ")}`;
      document.head.appendChild(style);
    })();
  """.trimIndent()

  fun shouldBlock(request: WebResourceRequest): Boolean {
    if (!isAdBlockEnabled) return false
    val url = request.url.toString().lowercase()
    for (host in AD_HOSTS) {
      if (url.contains(host)) {
        recordBlocked(request.url.host ?: host)
        return true
      }
    }
    return false
  }

  fun createEmptyResponse(): WebResourceResponse {
    return WebResourceResponse(
      "text/plain",
      "UTF-8",
      ByteArrayInputStream("".toByteArray())
    )
  }

  @Synchronized
  private fun recordBlocked(domain: String) {
    _blockedAdsCount.value += 1
    val current = _recentBlockedDomains.value.toMutableList()
    current.add(0, domain)
    if (current.size > 20) current.removeAt(current.size - 1)
    _recentBlockedDomains.value = current
  }

  @Synchronized
  fun resetStats() {
    _blockedAdsCount.value = 0
    _blockedTrackersCount.value = 0
    _recentBlockedDomains.value = emptyList()
  }
}
