package com.yunext.virtuals.module.devicemanager


import com.yunext.kmp.http.datasource.hdJson
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTContainer
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTMessage
import com.yunext.kmp.mqtt.virtuals.protocol.payload
import kotlinx.serialization.json.JsonObject

/**
 * 对接受来的MqttMessage消息进行处理
 * todo MqttMessage -> MQTTMessage?
 */
interface MQTTConvertor {
    fun decode(source: ByteArray): ProtocolMQTTContainer<*>

    fun encode(message: ProtocolMQTTMessage): ByteArray
}

class DefaultMqttConvertor : MQTTConvertor {
    override fun decode(source: ByteArray): ProtocolMQTTContainer<*> {
        val json = source.decodeToString()
        val msg = hdJson.decodeFromString<ProtocolMQTTContainer<JsonObject>>(json)
            ?: throw IllegalStateException("decode result is null")
        return msg
    }

    override fun encode(message: ProtocolMQTTMessage): ByteArray {
        val payload = message.payload
        return payload
    }

    private data class Param<T : Any>(val cmd: String, val params: T)

//    private fun MQTTMessage.toPayload() =
//        (if (data is JSONObject) data.toString() else
//            mqttGson.toJson(Param(this.cmd.cmd, this.data)).also {
//                li("payload:$it")
//            }).toByteArray()

}

//class DefaultMqttConvertor2(val tsl:Tsl) : MQTTConvertor, ILogger by DefaultLogger("MQTTConvertor") {
//    override fun decode(source: ByteArray): MQTTContainer<*> {
//        TODO("Not yet implemented")
//    }
//
//    override fun encode(message: MQTTMessage): ByteArray {
//        TODO("Not yet implemented")
//    }
//
//}