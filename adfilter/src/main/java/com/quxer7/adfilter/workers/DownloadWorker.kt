package com.quxer7.adfilter.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.quxer7.adfilter.AdFilter
import com.quxer7.adfilter.impl.AdFilterImpl
import com.quxer7.adfilter.impl.Constants.KEY_DOWNLOADED_DATA
import com.quxer7.adfilter.impl.Constants.KEY_DOWNLOAD_URL
import com.quxer7.adfilter.impl.Constants.KEY_FILTER_ID
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Created by Edsuns@qq.com on 2021/1/1.
 * Modified by quxer7 on 19/08/2025.
 */
internal class DownloadWorker(context: Context, params: WorkerParameters) : Worker(
    context,
    params
) {
    private val binaryDataStore = (AdFilter.get(applicationContext) as AdFilterImpl).binaryDataStore

    override fun doWork(): Result {
        val id = inputData.getString(KEY_FILTER_ID) ?: return Result.failure()
        val url = inputData.getString(KEY_DOWNLOAD_URL) ?: return Result.failure()

        try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()

            response.use {
                if (!it.isSuccessful) return Result.failure(inputData)
                val bodyBytes = it.body.bytes()
                val dataName = "_$id"
                binaryDataStore.saveData(dataName, bodyBytes)

                return Result.success(
                    workDataOf(
                        KEY_FILTER_ID to id,
                        KEY_DOWNLOADED_DATA to dataName
                    )
                )
            }
        } catch (_: IOException) {
            return Result.failure(inputData)
        }
    }

}