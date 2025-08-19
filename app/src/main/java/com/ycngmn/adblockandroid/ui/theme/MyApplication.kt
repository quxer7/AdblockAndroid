package com.ycngmn.adblockandroid.ui.theme

import android.app.Application
import com.ycngmn.adfilter.AdFilter

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val filter = AdFilter.create(this)
    }
}