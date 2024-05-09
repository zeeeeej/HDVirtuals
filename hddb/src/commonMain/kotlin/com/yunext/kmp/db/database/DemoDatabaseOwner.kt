package com.yunext.kmp.db.database

import com.yunext.kmp.context.hdContext
import com.yunext.kmp.db.createDatabase

abstract class DemoDatabaseOwner {
    protected val database = createDatabase(hdContext)

    protected val queries = database.demoDatabaseQueries
}