package com.quxer7.adfilter.script.raws

import com.anthonycr.mezzanine.FileStream

@FileStream("src/main/java/com/quxer7/adfilter/script/raws/element_hiding.js")
interface IMElemHiding {
    fun produce(): String
}

@FileStream("src/main/java/com/quxer7/adfilter/script/raws/elemhide_blocked.js")
interface IMElemHideBlocked {
    fun produce(): String
}

@FileStream("src/main/java/com/quxer7/adfilter/script/raws/extended-css.min.js")
interface IMExtendedCSSMin {
    fun produce(): String
}

@FileStream("src/main/java/com/quxer7/adfilter/script/raws/inject.js")
interface IMInject {
    fun produce(): String
}

@FileStream("src/main/java/com/quxer7/adfilter/script/raws/scriptlets.min.js")
interface IMScriptletsMin {
    fun produce(): String
}

@FileStream("src/main/java/com/quxer7/adfilter/script/raws/scriptlets_inject.js")
interface IMScriptletsInject {
    fun produce(): String
}