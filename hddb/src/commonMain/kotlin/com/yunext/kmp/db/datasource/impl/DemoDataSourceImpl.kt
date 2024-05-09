package com.yunext.kmp.db.datasource.impl

import com.yunext.kmp.database.Hd_user
import com.yunext.kmp.db.database.DemoDatabaseOwner
import com.yunext.kmp.db.datasource.DemoDataSource
import kotlin.random.Random

class DemoDataSourceImpl : DemoDataSource, DemoDatabaseOwner() {

    override fun findAll(): List<Hd_user> {
        return database.demoDatabaseQueries.selectAll().executeAsList()
    }

    override fun add(): Boolean {
        database.demoDatabaseQueries.insert(Random.nextLong(), Random.nextBytes(4).toString())
        return true
    }
}