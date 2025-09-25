package com.quxer7.adfilter.impl

import com.quxer7.adfilter.CustomFilter
import com.quxer7.adfilter.util.RuleIterator

/**
 * Created by Edsuns@qq.com on 2021/7/29.
 * Modified by quxer7 on 19/08/2025.
 */
internal class CustomFilterImpl(
    private val filterDataLoader: FilterDataLoader,
    data: String? = null
) : CustomFilter, RuleIterator(data) {

    override fun flush() {
        val blacklistStr = dataBuilder.toString()
        if (blacklistStr.isNotBlank()) {
            val rawData = blacklistStr.toByteArray()
            filterDataLoader.loadCustomFilter(rawData)
        }
    }
}