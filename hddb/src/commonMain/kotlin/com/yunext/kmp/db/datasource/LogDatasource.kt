package com.yunext.kmp.db.datasource

import com.yunext.kmp.db.entity.LogEntity

interface LogDatasource {
    fun findAll(): List<LogEntity>
    fun searchAll(
        deviceId: String,
        /*标记分类*/
        sign: Sign,
        /*模糊查询*/
        search: String?,
        /*开始时间*/
        start: Long,
        /*结束时间*/
        end: Long,
        pageNumber: Int,
        pageSize: Int,
    ):  List<LogEntity>

    fun add(logEntity: LogEntity)

    fun clearById(vararg logId: Long)

    fun clear()

    fun clearByDevice(deviceId: String)

    enum class Sign {
        ALL, FAIL, SUCCESS, ONLINE, UP, DOWN
    }
}