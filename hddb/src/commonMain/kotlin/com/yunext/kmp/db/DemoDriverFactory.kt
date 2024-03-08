package com.yunext.kmp.db

import app.cash.sqldelight.db.SqlDriver
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.database.DemoDatabase

internal const val DB_NAME = "hd_virtuals.db"

internal expect class DemoDriverFactory {
    fun createDriver(): SqlDriver
}

expect fun createDatabase(context: HDContext): DemoDatabase