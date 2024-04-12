//package com.yunext.kmp.mqtt.virtuals.repository
//
//import android.content.Context
//import android.util.Log
//import com.yunext.farm.common.logger.DefaultLogger
//import com.yunext.farm.common.logger.ILogger
//import com.yunext.farm.http.datasource.TslDatasource
//import com.yunext.farm.twins.data.device.MQTTDevice
//import com.yunext.farm.twins.data.tsl.*
//import com.yunext.kmp.mqtt.virtuals.protocol.tsl.Tsl
//import kotlinx.coroutines.delay
//import javax.inject.Inject
//
//class TslRepositoryImpl @Inject constructor(
//    private val tslDatasource: TslDatasource,
//    private val tslLocalTslDatasource: LocalTslDatasource,
//) : TslRepository, ILogger by DefaultLogger("_tsl_") {
//
//
//    private suspend fun tryLocalTsl(id: String): Tsl? {
//        return try {
//            tslLocalTslDatasource.take(id)
//        } catch (e: Throwable) {
//            null
//        }
//    }
//
//    private suspend fun tryRemoteTsl(clientId: String, id: String): Tsl? {
//        var index = 0
//        while (index < 3) {
//            val tsl: Tsl? = try {
//                val container = tslDatasource.getTsl(clientId, "")
//                if (container.success == true) {
//                    // 保存到本地
//                    val r = container.data?.convert()
//                        ?: throw IllegalStateException("${container.code} / ${container.msg}")
////                    tslLocalTslDatasource.put(id,r)
//                    r
//                } else {
//                    throw IllegalStateException("${container.code} / ${container.msg}")
//                }
//            } catch (e: Throwable) {
//                e.printStackTrace()
//                null
//            }
//            if (tsl != null) {
//                return tsl
//            }
//            index++
//            delay(1000)
//        }
//        return null
//    }
//
//    override suspend fun getTsl(
//        context: Context,
//        clientId: String,
//        device: MQTTDevice
//    ): Tsl? {
//        val id = device.generateId()
//        val tsl = try {
//            val remote = tryRemoteTsl(clientId, id)
//            if (remote != null) {
//                tslLocalTslDatasource.put(id, remote)
//                remote
//            } else {
//                tryLocalTsl(id)
//            }
//        } catch (e: Throwable) {
//            null
//        }
//
//        Log.i("TslRepository::getTsl","tsl:$tsl")
//        return tsl
//    }
//
//    override suspend fun updateTsl(
//        context: Context,
//        clientId: String,
//        device: MQTTDevice
//    ): Tsl? {
//        val id = device.generateId()
//        val remote = tryRemoteTsl(clientId, id)
//        val local = tryLocalTsl(id)
//        if (remote != null) {
//            if (local != null) {
//                if (remote.version == local.version) {
//                    throw IllegalStateException("已是最新版本")
//                }
//            }
//            tslLocalTslDatasource.put(id, remote)
//        }
//        return remote ?: local
//    }
//
//    @Deprecated("使用sp")
//    private fun loadTslFromAsset(
//        context: Context,
//        filename: String
//    ): Tsl? {
//        return try {
//            TslAssetsParser("$TSL_PATH/$filename").parse(context)?.convert()
//        } catch (e: Throwable) {
//            null
//        }
//    }
//}