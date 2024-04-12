package com.yunext.virtuals

import android.app.Application
import com.yunext.kmp.context.initHDContext

class HDApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initHDContext(){
            this.init(this@HDApp)
        }
    }
}