package com.yunext.virtuals

import android.app.Application
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.initHDContext
import com.yunext.virtuals.ui.initHDRes

class HDApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initHDContext(){
            this.init(this@HDApp)
        }
    }
}