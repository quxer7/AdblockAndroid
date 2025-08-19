package com.ycngmn.adfilter.script

import android.content.Context
import kotlin.random.Random

internal object ScriptInjection {

    private const val INJECTION = "{{INJECTION}}"
    private const val DEBUG_FLAG = "{{DEBUG}}"
    private const val JS_BRIDGE = "{{BRIDGE}}"

    private val bridgeRegister = arrayListOf(
        ElementHiding::class.java,
        Scriptlet::class.java
    )

    private val bridgeNamePrefix = randomAlphanumericString()

    fun loadInjectJS(context: Context): String {
        val raw = context.assets.open("inject.js").bufferedReader().use { it.readText() }
        return parse(raw)
    }

    fun bridgeNameFor(owner: Any): String {
        val clazz = owner::class.java
        val index = bridgeRegister.indexOf(clazz)
        if (index < 0) {
            error("$clazz isn't registered as a bridge!")
        }
        return bridgeNamePrefix + index
    }

    private fun randomAlphanumericString(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z')
        val outputStrLength = (10..36).shuffled().first()

        return (1..outputStrLength)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun parse(raw: String, bridgeName: String? = null): String {
        var js = raw.replace(DEBUG_FLAG, "//")
        if (bridgeName != null) {
            js = js.replace(JS_BRIDGE, bridgeName)
        }
        return js
    }

    fun parseScript(context: Context, owner: Any, raw: String, wrapper: Boolean = false): String {
        val js = parse(raw, bridgeNameFor(owner))
        return if (wrapper) {
            val injectJS = loadInjectJS(context)
            injectJS.replace(INJECTION, js)
        } else {
            js
        }
    }
}
