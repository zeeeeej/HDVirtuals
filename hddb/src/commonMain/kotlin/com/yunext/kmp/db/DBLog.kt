package com.yunext.kmp.db

import com.yunext.kmp.common.logger.HDLogger

internal object DBLog {
    private const val DEBUG = true
    private const val TAG = "_db_"

    internal fun i(msg: String) {
        if (DEBUG) return
        HDLogger.i(TAG, msg)
    }

    internal fun d(msg: String) {
        if (DEBUG) return
        HDLogger.d(TAG, msg)
    }

    internal fun w(msg: String) {
        if (DEBUG) return
        HDLogger.w(TAG, msg)
    }

    internal fun e(msg: String) {
        if (DEBUG) return
        HDLogger.e(TAG, msg)
    }
}