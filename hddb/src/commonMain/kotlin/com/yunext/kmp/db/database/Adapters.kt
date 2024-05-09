package com.yunext.kmp.db.database

import app.cash.sqldelight.ColumnAdapter
import com.yunext.kmp.database.Hd_log

class LidAdapter : ColumnAdapter<Long, Long> {
    override fun decode(databaseValue: Long): Long {
        return databaseValue
    }

    override fun encode(value: Long): Long {
        return value
    }
}

val DEFAULT_ADAPTER by lazy {
    Hd_log.Adapter(
        lidAdapter = LidAdapter(), timestampAdapter = LidAdapter()
    )
}
