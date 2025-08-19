package com.ycngmn.adblockandroid

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ycngmn.adblockandroid.ui.theme.AdblockAndroidTheme
import com.ycngmn.adfilter.AdFilter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AdFilter.create(this)

        setContent {
            AdblockAndroidTheme {
                WebViewScreen("https://adblock-tester.com/")
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {

    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { context ->

            val webView = WebView(context)

            val filter = AdFilter.get()
            val filterViewModel = filter.viewModel
            filterViewModel.enableCustomFilter()
            filter.setupWebView(webView)

            // Add filter list subscriptions on first installation.
            if (!filter.hasInstallation) {
                Toast.makeText(
                    context,
                    "Downloading filters..",
                    Toast.LENGTH_LONG
                ).show()

                val map = mapOf(
                    "AdGuard Base" to "https://filters.adtidy.org/extension/chromium/filters/2.txt",
                    "EasyPrivacy Lite" to "https://filters.adtidy.org/extension/chromium/filters/118_optimized.txt",
                    "AdGuard Tracking Protection" to "https://filters.adtidy.org/extension/chromium/filters/3.txt",
                    "AdGuard Annoyances" to "https://filters.adtidy.org/extension/chromium/filters/14.txt",
                    "AdGuard Chinese" to "https://filters.adtidy.org/extension/chromium/filters/224.txt",
                    "NoCoin Filter List" to "https://filters.adtidy.org/extension/chromium/filters/242.txt"
                )
                for ((key, value) in map) {
                    val subscription = filterViewModel.addFilter(key, value)
                    filterViewModel.download(subscription.id)
                }

            }

            filterViewModel.onDirty.observe(lifecycleOwner) {
                webView.clearCache(false)
            }

            webView.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = AdblockWebViewClient
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
    )
}

object AdblockWebViewClient: WebViewClient() {
    private val filter = AdFilter.get()

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val result = filter.shouldIntercept(view!!, request!!)
        return result.resourceResponse
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        filter.performScript(view, url)
    }
}