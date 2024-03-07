package com.yunext.virtuals

import android.app.Application
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.initHDContext

class HDApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initHDContext(){
            HDLogger.debug = true
            this.init(this@HDApp)
        }
    }
}