package com.ycngmn.adfilter

import android.webkit.WebResourceResponse

/**
 * Created by Edsuns@qq.com on 2021/1/24.
 * Modified by ycngmn on 19/08/2025.
 */
data class FilterResult(
    val rule: String?,
    val resourceUrl: String,
    val resourceResponse: WebResourceResponse?,
    val shouldBlock: Boolean = rule != null
)
