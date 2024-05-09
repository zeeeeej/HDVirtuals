package com.yunext.kmp.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.database.DemoDatabase
import com.yunext.kmp.db.database.DEFAULT_ADAPTER

internal actual class DemoDriverFactory {
    actual fun createDriver(): SqlDriver {
       return NativeSqliteDriver(DemoDatabase.Schema, DB_NAME)
    }
}

actual fun createDatabase(context: HDContext): DemoDatabase {
    return DemoDatabase(DemoDriverFactory().createDriver(), hd_logAdapter = DEFAULT_ADAPTER)
}