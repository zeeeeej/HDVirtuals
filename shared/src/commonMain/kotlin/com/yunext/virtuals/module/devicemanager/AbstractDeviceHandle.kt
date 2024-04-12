package com.yunext.virtuals.module.devicemanager


import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.http.datasource.hdJson
import com.yunext.kmp.mqtt.virtuals.protocol.DataCmd
import com.yunext.kmp.mqtt.virtuals.protocol.InfoUpCmd
import com.yunext.kmp.mqtt.virtuals.protocol.OnlineCmd
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTCmd
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTContainer
import com.yunext.kmp.mqtt.virtuals.protocol.ReportCmd
import com.yunext.kmp.mqtt.virtuals.protocol.ServiceCmd
import com.yunext.kmp.mqtt.virtuals.protocol.SetCmd
import com.yunext.kmp.mqtt.virtuals.protocol.TimestampCmd
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslValueParser
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleUpdatePropertyValuesFromJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject

abstract class AbstractDeviceHandle(protected val coroutineScope: CoroutineScope) : DeviceHandle

class DefaultDeviceHandle(coroutineScope: CoroutineScope) : AbstractDeviceHandle(coroutineScope){


    /**
     *  todo 处理方式阻塞？还是并行？
     */
    override fun handle(deviceStore: DeviceStore, message: ProtocolMQTTContainer<*>): Boolean {
        val cmd = message.cmd?:return true
        val params = message.params ?: return true
        try {
            when (val mqttCmd = ProtocolMQTTCmd.from(cmd)) {
                InfoUpCmd -> {
                    //handleProperty(params.toString())
                }
                OnlineCmd -> {
                    // handleProperty(params.toString())
                }
                ReportCmd -> {
                    //replySet(mqttCmd.cmd, params.toString())
                }
                SetCmd -> {
                    // TODO replySet
                    val json = params.toString() // 其实就是一个JsonObject
                    li("SetCmd json = $json")
                    val tsl = deviceStore.tryGetTsl()?:return true
                    val propertyValues = TslValueParser.fromJson(tsl,json)
                    deviceStore.sendProperty(*propertyValues.toTypedArray(), publishLimit = false)
                }
                is ServiceCmd -> {
                    // TODO handeService
                }

                DataCmd -> {
                    // TODO replyData
                }

                TimestampCmd -> {

                }

            }
        } catch (e: Throwable) {
            li("[handle] error :$e")
//            if (e is TslCmdException) {
//                // 处理service
//                handleService(cmd)
//            }
        }
        return true
    }

    private fun li(msg:String){
        HDLogger.d("DefaultDeviceHandle",msg)
    }


}
