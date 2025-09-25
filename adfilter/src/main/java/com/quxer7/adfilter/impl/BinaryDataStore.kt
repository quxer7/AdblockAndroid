package com.quxer7.adfilter.impl

import java.io.File

/**
 * Created by Edsuns@qq.com on 2020/10/24.
 * Modified by quxer7 on 19/08/2025.
 */
internal class BinaryDataStore(private val dir: File) {

    init {
        if (!dir.exists() ) dir.mkdirs()
    }

    fun hasData(name: String): Boolean = File(dir, name).exists()

    fun loadData(name: String): ByteArray = File(dir, name).readBytes()

    fun saveData(name: String, byteArray: ByteArray) {
        File(dir, name).writeBytes(byteArray)
    }

    fun clearData(name: String) {
        File(dir, name).delete()
    }
}