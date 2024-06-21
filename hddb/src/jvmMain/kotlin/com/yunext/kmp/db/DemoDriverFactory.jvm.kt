package com.yunext.kmp.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.database.DemoDatabase
import com.yunext.kmp.db.database.DEFAULT_ADAPTER
import java.io.File

private const val JDBC_SCHEMA = "jdbc:sqlite:"

internal actual class DemoDriverFactory(private val memory: Boolean = false) {
    actual fun createDriver(): SqlDriver {
        // 参考 https://github.com/RackaApps/Reluct/blob/main/common/persistence/database/src/desktopMain/kotlin/work/racka/reluct/common/database/di/Platform.kt
        val driver = if (memory) {
            println("===createDriver=== IN_MEMORY")
            val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            driver
        } else {
            val dbPath = File(System.getProperty("java.io.tmpdir"), DB_NAME)
            val driver = JdbcSqliteDriver(url = "${JDBC_SCHEMA}${dbPath.absolutePath}")
            if (!dbPath.exists()) {
                println("===createDriver=== $dbPath")
                DemoDatabase.Schema.create(driver)
            }
            driver
        }
        return driver
    }
}

actual fun createDatabase(context: HDContext): DemoDatabase {
    return DemoDatabase(
        DemoDriverFactory(false).createDriver(), hd_logAdapter = DEFAULT_ADAPTER
    )
}