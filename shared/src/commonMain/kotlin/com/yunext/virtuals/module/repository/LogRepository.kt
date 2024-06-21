package com.yunext.virtuals.module.repository

import com.yunext.kmp.common.logger.HDLogger
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

private class LogRepositoryImpl(private val logDatasource: LogDatasource = LogDatasourceImpl()) :
    LogRepository {
    override suspend fun add(log: Log): Boolean {
        HDLogger.d("LogScreenModel::add", "log:$log")
        return suspendCancellableCoroutine { con ->
            try {
                HDLogger.d("LogScreenModel::add", "log:$log 1111")
                val logEntity = log.convert()
                logDatasource.add(logEntity)
                con.resume(true)
                HDLogger.d("LogScreenModel::add", "log:$log 2222")
            } catch (e: Exception) {
                HDLogger.d("LogScreenModel::add", "error: $e")
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
                HDLogger.d("LogScreenModel", "doSearch 555555  =>1")
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
                HDLogger.d("LogScreenModel", "doSearch 555555  =>2")
                con.resume(list)
            } catch (e: Exception) {
                con.resumeWithException(e)
                HDLogger.d("LogScreenModel", "doSearch 555555  =>3 ${e.message}")
            }
            con.invokeOnCancellation {
                // ignore
                HDLogger.d("LogScreenModel", "doSearch 555555  =>4")
            }
        }
    }

}