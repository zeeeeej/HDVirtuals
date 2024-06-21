package com.yunext.kmp.db.database

import com.yunext.kmp.context.hdContext
import com.yunext.kmp.database.DemoDatabase
import com.yunext.kmp.database.DemoDatabaseQueries
import com.yunext.kmp.db.createDatabase

internal interface DemoDatabaseOwner {

     val database:DemoDatabase

     val queries:DemoDatabaseQueries

    companion object:DemoDatabaseOwner{
        override val database: DemoDatabase
            get() =  createDatabase(hdContext)
        override val queries: DemoDatabaseQueries
            get() = database.demoDatabaseQueries

    }
}