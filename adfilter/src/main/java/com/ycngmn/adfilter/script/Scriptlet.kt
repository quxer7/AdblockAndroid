package com.ycngmn.adfilter.script

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.ycngmn.adfilter.impl.Detector
import java.io.BufferedReader
import java.io.InputStreamReader

internal class Scriptlet(
    private val detector: Detector,
    private val context: Context
) {

    private val rawScriptletsJs by lazy(LazyThreadSafetyMode.NONE) {
        val inputStream = context.assets.open("scriptlets.min.js")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.use { it.readText() }
    }

    private val scriptletsJS: String by lazy(LazyThreadSafetyMode.NONE) {
        rawScriptletsJs + ScriptInjection.parseScript(context, this, rawScriptletsJs, wrapper = true)
    }

    fun perform(webView: WebView?) {
        webView?.evaluateJavascript(scriptletsJS, null)
    }

    @JavascriptInterface
    fun getScriptlets(documentUrl: String): String {
        val list = detector.getScriptlets(documentUrl)
        return list.toScriptletsJSON()
    }

    private fun Collection<String>.toScriptletsJSON(): String {
        val builder = StringBuilder()
        for (str in this) {
            if (builder.isNotEmpty()) builder.append(',')
            builder.append('[').append(str).append(']')
        }
        builder.insert(0, '[')
        builder.append(']')
        return builder.toString().replace('\'', '"') // only double quotes allowed
    }
}
