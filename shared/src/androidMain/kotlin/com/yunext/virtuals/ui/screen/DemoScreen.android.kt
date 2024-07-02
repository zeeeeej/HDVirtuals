package com.yunext.virtuals.ui.screen

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.application
import com.yunext.kmp.context.hdContext

//actual fun gotoSetting() {
//    HDLogger.d("DemoScreen", "gotoSetting")
//    hdContext.application.gotoSetting()
//}

private fun Context.gotoSetting() {
    try {
        startActivity(Intent(Settings.ACTION_SETTINGS).apply {
            if (this@gotoSetting is Application) {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        })
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}