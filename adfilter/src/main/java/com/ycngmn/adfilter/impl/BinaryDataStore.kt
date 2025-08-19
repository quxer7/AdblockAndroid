package com.ycngmn.adfilter.impl

import android.util.Log
import java.io.File

/**
 * Created by Edsuns@qq.com on 2020/10/24.
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