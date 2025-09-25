package com.quxer7.adfilter.script

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.anthonycr.mezzanine.mezzanine
import com.quxer7.adfilter.impl.Detector
import com.quxer7.adfilter.script.raws.IMElemHideBlocked
import com.quxer7.adfilter.script.raws.IMElemHiding
import com.quxer7.adfilter.script.raws.IMExtendedCSSMin
import java.net.MalformedURLException
import java.net.URL

internal class ElementHiding(
    private val detector: Detector
) {

    private val eleHidingJS: String by lazy {
        val extendedCssJS = mezzanine<IMExtendedCSSMin>().produce()
        val elemHidingJS = mezzanine<IMElemHiding>().produce()
        extendedCssJS + ScriptInjection.parseScript(this, elemHidingJS, wrapper = true)
    }

    private val elemhideBlockedJs: String by lazy {
        val script = mezzanine<IMElemHideBlocked>()
        ScriptInjection.parseScript(this, script.produce())
    }

    internal fun elemhideBlockedResource(webView: WebView?, resourceUrl: String?) {
        var filenameWithQuery: String
        try {
            filenameWithQuery = extractPathWithQuery(resourceUrl)
            if (filenameWithQuery.startsWith("/")) {
                filenameWithQuery = filenameWithQuery.substring(1)
            }
        } catch (_: MalformedURLException) {
            Log.d("ElementHiding", "Failed to parse URI for blocked resource: $resourceUrl. Skipping element hiding")
            return
        }

        val selectorBuilder = StringBuilder()
            .append("[src$='").append(filenameWithQuery)
            .append("'], [srcset$='")
            .append(filenameWithQuery)
            .append("']")

        webView?.post {
            val scriptBuilder = StringBuilder(elemhideBlockedJs)
                .append("\n\n")
                .append("elemhideForSelector(\"")
                .append(resourceUrl)  // 1st argument
                .append("\", \"")
                .append(escapeJavaScriptString(selectorBuilder.toString())) // 2nd argument
                .append("\", 0)") // attempt #0

            webView.evaluateJavascript(scriptBuilder.toString(), null)
        }
    }

    fun perform(webView: WebView?) {
        webView?.evaluateJavascript(eleHidingJS, null)
    }

    private fun List<String>.joinString(): String {
        val builder = StringBuilder()
        for (s in this) {
            builder.append(s)
        }
        return builder.toString()
    }

    @JavascriptInterface
    fun getStyleSheet(documentUrl: String): String {
        val result = StringBuilder()
        val selectors = detector.getElementHidingSelectors(documentUrl)
        val customSelectors = detector.getCustomElementHidingSelectors(documentUrl)
        val cssRules = detector.getCssRules(documentUrl).joinString()
        if (selectors.isNotBlank()) {
            result.append(selectors).append(HIDING_CSS)
        }
        if (customSelectors.isNotBlank()) {
            result.append(customSelectors).append(HIDING_CSS)
        }
        if (cssRules.isNotBlank()) {
            result.append(cssRules)
        }
        // stylesheet has a limit of length, split it into smaller pieces by the replacement
        return result.toString().replace(", ", HIDING_CSS, 200)
    }

    @JavascriptInterface
    fun getExtendedCssStyleSheet(documentUrl: String): String {
        val extendedCss = detector.getExtendedCssSelectors(documentUrl)
        if (extendedCss.isNotEmpty()) {
            // join to String with ", "
            return extendedCss.joinToString() + HIDING_CSS
        }
        return ""
    }

    @Throws(MalformedURLException::class)
    fun extractPathWithQuery(urlString: String?): String {
        val url = URL(urlString)
        val sb = StringBuilder(url.path)
        if (url.query != null) {
            sb.append("?").append(url.query)
        }
        return sb.toString()
    }

    private fun escapeJavaScriptString(line: String): String {
        val sb = StringBuilder()
        for (c in line) {
            when (c) {
                '"', '\'', '\\' -> {
                    sb.append('\\')
                    sb.append(c)
                }
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                else -> sb.append(c)
            }
        }
        return sb.toString()
            .replace(U2028, "\u2028")
            .replace(U2029, "\u2029")
    }

    private fun String.replace(
        oldValue: String,
        newValue: String,
        every: Int,
        ignoreCase: Boolean = false
    ): String {
        var occurrenceIndex: Int = indexOf(oldValue, 0, ignoreCase)
        if (occurrenceIndex < 0) return this

        val oldValueLength = oldValue.length
        val searchStep = oldValueLength.coerceAtLeast(1)
        val newLengthHint = length - oldValueLength + newValue.length
        if (newLengthHint < 0) throw OutOfMemoryError()
        val stringBuilder = StringBuilder(newLengthHint)

        var count = 0
        var i = 0
        do {
            count++
            stringBuilder.append(this, i, occurrenceIndex)
            if (count == every) {
                stringBuilder.append(newValue)
                count = 0
            } else {
                stringBuilder.append(oldValue)
            }
            i = occurrenceIndex + oldValueLength
            if (occurrenceIndex >= length) break
            occurrenceIndex = indexOf(oldValue, occurrenceIndex + searchStep, ignoreCase)
        } while (occurrenceIndex > 0)
        return stringBuilder.append(this, i, length).toString()
    }

    companion object {
        private val U2028 = String(byteArrayOf(0xE2.toByte(), 0x80.toByte(), 0xA8.toByte()))
        private val U2029 = String(byteArrayOf(0xE2.toByte(), 0x80.toByte(), 0xA9.toByte()))

        private const val HIDING_CSS = "{display: none !important; visibility: hidden !important;}"
    }
}
