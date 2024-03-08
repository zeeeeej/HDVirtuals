package com.yunext.kmp.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.application
import com.yunext.kmp.context.hdContext
import com.yunext.kmp.database.DemoDatabase

internal actual class DemoDriverFactory(private val context: HDContext) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(DemoDatabase.Schema, context.application, DB_NAME)
    }
}

actual fun createDatabase(context: HDContext): DemoDatabase {
    return DemoDatabase(DemoDriverFactory(context).createDriver())
}