package com.yunext.virtuals.module.repository

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.http.core.HDResult
import com.yunext.kmp.http.core.map
import com.yunext.kmp.http.datasource.LocalTslDatasource
import com.yunext.kmp.http.datasource.LocalTslDatasourceImpl
import com.yunext.kmp.http.datasource.RemoteTslDatasource
import com.yunext.kmp.http.datasource.RemoteTslDatasourceImpl
import com.yunext.kmp.resp.tsl.TslResp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.Tsl
import com.yunext.kmp.mqtt.virtuals.repository.convert

interface TslRepository {
    suspend fun load(clientID: String): HDResult<Tsl>

    companion object : TslRepository {

        private val remoteTslDatasource: RemoteTslDatasource by lazy {
            RemoteTslDatasourceImpl()
        }

        private val localTslDatasource: LocalTslDatasource by lazy {
            LocalTslDatasourceImpl()
        }

        override suspend fun load(clientID: String): HDResult<Tsl> {
            HDLogger.d("TslRepository", "load remote tsl ...")
            val remoteTslResult = remoteTslDatasource.getTsl(clientID, "")
            val result = when (remoteTslResult) {
                is HDResult.Fail -> {
                    HDLogger.d("TslRepository", "load local tsl ...")
                    localTslDatasource.getTsl2()
                }

                is HDResult.Success -> remoteTslResult
            }

            return result.map (TslResp::convert)
//            return when (result) {
//                is HDResult.Fail -> HDResult.Fail(result.error)
//                is HDResult.Success -> {
//                    val container = result.data
//                    if (container.success == true) {
//                        val data = container.data?.convert()
//                        data?.run {
//                            HDResult.Success(this)
//                        }
//                            ?: HDResult.Fail(IllegalStateException("获取[$clientID]tsl失败!data:${container.data}"))
//
//                    } else HDResult.Fail(IllegalStateException("获取[$clientID]tsl失败!success:${container.success}"))
//                }
//            }
        }
    }
}