package com.yunext.kmp.db.datasource.impl

import com.yunext.kmp.context.hdContext
import com.yunext.kmp.database.Hd_user
import com.yunext.kmp.db.createDatabase
import com.yunext.kmp.db.datasource.DemoDataSource
import kotlin.random.Random

class DemoDataSourceImpl:DemoDataSource {

    private val database =  createDatabase(hdContext)
    override fun findAll(): List<Hd_user> {
        return database.demoDatabaseQueries.selectAll().executeAsList()
    }

    override fun add(): Boolean {
        database.demoDatabaseQueries.insert(Random.nextLong(), Random.nextBytes(4).toString())
        return true
    }
}