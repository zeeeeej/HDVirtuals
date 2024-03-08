package com.yunext.kmp.db.datasource

import com.yunext.kmp.database.Hd_user

interface DemoDataSource {
    fun findAll(): List<Hd_user>
    fun add(): Boolean
}