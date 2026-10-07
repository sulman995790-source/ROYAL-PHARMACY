package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import java.net.URLEncoder

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InAppSearchDialog(
  initialQuery: String,
  title: String = "In-App Google Search",
  onDismiss: () -> Unit
) {
  val targetUrl = remember(initialQuery) {
    if (initialQuery.startsWith("http://") || initialQuery.startsWith("https://")) {
      initialQuery
    } else {
      val encoded = try {
        URLEncoder.encode(initialQuery, "UTF-8")
      } catch (_: Exception) {
        initialQuery
      }
      "https://www.google.com/search?q=$encoded"
    }
  }

  var webViewInstance by remember { mutableStateOf<WebView?>(null) }
  var canGoBack by remember { mutableStateOf(false) }
  var progress by remember { mutableIntStateOf(0) }
  var isLoading by remember { mutableStateOf(true) }

  BackHandler {
    if (webViewInstance?.canGoBack() == true) {
      webViewInstance?.goBack()
    } else {
      onDismiss()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = Color.White
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Toolbar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(RoyalNavy)
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              if (webViewInstance?.canGoBack() == true) {
                webViewInstance?.goBack()
              } else {
                onDismiss()
              }
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(18.dp)
          )

          Spacer(modifier = Modifier.width(8.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = title,
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = initialQuery.take(60),
              color = Color(0xFFCBD5E1),
              fontSize = 11.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          IconButton(
            onClick = { webViewInstance?.reload() }
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Reload",
              tint = Color.White
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        if (isLoading && progress < 100) {
          LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = RoyalMagenta,
            trackColor = Color(0xFFE2E8F0)
          )
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          AndroidView(
            factory = { context ->
              WebView(context).apply {
                settings.apply {
                  javaScriptEnabled = true
                  domStorageEnabled = true
                  loadWithOverviewMode = true
                  useWideViewPort = true
                  setSupportZoom(true)
                  builtInZoomControls = true
                  displayZoomControls = false
                }

                webViewClient = object : WebViewClient() {
                  override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false
                  }

                  override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isLoading = true
                    canGoBack = view?.canGoBack() == true
                  }

                  override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isLoading = false
                    canGoBack = view?.canGoBack() == true
                  }
                }

                webChromeClient = object : WebChromeClient() {
                  override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progress = newProgress
                    if (newProgress >= 100) {
                      isLoading = false
                    }
                  }
                }

                loadUrl(targetUrl)
                webViewInstance = this
              }
            },
            modifier = Modifier.fillMaxSize()
          )

          if (isLoading && progress < 30) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator(
                color = RoyalMagenta,
                modifier = Modifier.size(36.dp)
              )
            }
          }
        }
      }
    }
  }
}
