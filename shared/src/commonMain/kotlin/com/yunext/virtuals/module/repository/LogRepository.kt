package com.yunext.virtuals.module.repository

import com.yunext.kmp.db.datasource.LogDatasource
import com.yunext.kmp.db.datasource.impl.LogDatasourceImpl
import com.yunext.kmp.db.entity.LogEntity
import com.yunext.virtuals.data.Log
import com.yunext.virtuals.data.convert
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface LogRepository {

    suspend fun add(log: Log): Boolean

    suspend fun findAll(): List<Log>
    suspend fun deleteALl(deviceId: String): Boolean
    suspend fun search(
        deviceId: String,
        sign: LogDatasource.Sign,
        search: String?,
        start: Long,
        end: Long,
        pageNumber: Int,
        pageSize: Int,
    ): List<Log>

    companion object : LogRepository by LogRepositoryImpl()

}

private class LogRepositoryImpl : LogRepository {
    private val logDatasource: LogDatasource = LogDatasourceImpl()
    override suspend fun add(log: Log): Boolean {
        return suspendCancellableCoroutine { con ->
            try {
                val logEntity = log.convert()
                logDatasource.add(logEntity)
                con.resume(true)
            } catch (e: Exception) {
                con.resumeWithException(e)
            }
            con.invokeOnCancellation {
                // ignore
            }
        }

    }

    override suspend fun findAll(): List<Log> {
        return suspendCancellableCoroutine { con ->
            try {
                val list = logDatasource.findAll().map(LogEntity::convert)
                con.resume(list)
            } catch (e: Exception) {
                con.resumeWithException(e)
            }
            con.invokeOnCancellation {
                // ignore
            }
        }
    }

    override suspend fun deleteALl(deviceId: String): Boolean {
        return suspendCancellableCoroutine { con ->
            try {
               logDatasource.clearByDevice(deviceId)
                con.resume(true)
            } catch (e: Exception) {
                con.resumeWithException(e)
            }
            con.invokeOnCancellation {
                // ignore
            }
        }
    }

    override suspend fun search(
        deviceId: String,
        sign: LogDatasource.Sign,
        search: String?,
        start: Long,
        end: Long,
        pageNumber: Int,
        pageSize: Int,
    ): List<Log> {
        return suspendCancellableCoroutine { con ->
            try {
                val list = logDatasource.searchAll(
                    deviceId = deviceId,
                    sign = sign,
                    search = search,
                    start = start,
                    end = end,
                    pageNumber = pageNumber,
                    pageSize = pageSize
                )
                    .map(LogEntity::convert)
                con.resume(list)
            } catch (e: Exception) {
                con.resumeWithException(e)
            }
            con.invokeOnCancellation {
                // ignore
            }
        }
    }

}