package com.ycngmn.adfilter.impl

import com.ycngmn.adfilter.CustomFilter
import com.ycngmn.adfilter.util.RuleIterator

/**
 * Created by Edsuns@qq.com on 2021/7/29.
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