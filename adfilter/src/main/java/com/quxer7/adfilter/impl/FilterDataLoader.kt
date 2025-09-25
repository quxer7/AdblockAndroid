package com.quxer7.adfilter.impl

import com.quxer7.adblockclient.AdBlockClient
import com.quxer7.adfilter.CustomFilter

/**
 * Created by Edsuns@qq.com on 2020/10/24.
 * Modified by quxer7 on 19/08/2025.
 */
internal class FilterDataLoader(
    val detector: Detector,
    private val binaryDataStore: BinaryDataStore
) {

    fun load(id: String) {
        if (binaryDataStore.hasData(id)) {
            val client = AdBlockClient(id)
            client.loadProcessedData(binaryDataStore.loadData(id))
            if (id == ID_CUSTOM) {
                detector.customFilterClient = client
            } else {
                detector.addClient(client)
            }
        }
    }

    fun unload(id: String) {
        detector.removeClient(id)
    }

    fun unloadAll() {
        detector.clearAllClient()
    }

    fun remove(id: String) {
        binaryDataStore.clearData(id)
        binaryDataStore.clearData("_$id")
        unload(id)
    }

    fun isCustomFilterEnabled() = detector.customFilterClient != null

    fun getCustomFilter(): CustomFilter {
        if (binaryDataStore.hasData(RAW_CUSTOM)) {
            return CustomFilterImpl(this, String(binaryDataStore.loadData(RAW_CUSTOM)))
        }
        return CustomFilterImpl(this)
    }

    fun loadCustomFilter(rawData: ByteArray) {
        binaryDataStore.saveData(RAW_CUSTOM, rawData)
        val client = AdBlockClient(ID_CUSTOM)
        client.loadBasicData(rawData, true)
        binaryDataStore.saveData(ID_CUSTOM, client.getProcessedData())
        load(ID_CUSTOM)
    }

    fun unloadCustomFilter() {
        detector.customFilterClient = null
    }

    companion object {
        const val RAW_CUSTOM = "_custom"
        const val ID_CUSTOM = "custom"
    }
}