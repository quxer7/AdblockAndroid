package com.quxer7.adfilter.script

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.anthonycr.mezzanine.mezzanine
import com.quxer7.adfilter.impl.Detector
import com.quxer7.adfilter.script.raws.IMScriptletsMin

internal class Scriptlet(
    private val detector: Detector
) {

    private val rawScriptletsJs by lazy(LazyThreadSafetyMode.NONE) {
        mezzanine<IMScriptletsMin>().produce()
    }

    private val scriptletsJS: String by lazy(LazyThreadSafetyMode.NONE) {
        rawScriptletsJs + ScriptInjection.parseScript(this, rawScriptletsJs, wrapper = true)
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
