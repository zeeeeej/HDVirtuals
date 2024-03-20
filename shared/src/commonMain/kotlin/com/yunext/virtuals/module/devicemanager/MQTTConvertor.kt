package com.yunext.virtuals.module.devicemanager


import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTContainer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 对接受来的MqttMessage消息进行处理
 * todo MqttMessage -> MQTTMessage?
 */
interface MQTTConvertor {
    fun decode(source: ByteArray): ProtocolMQTTContainer<*>

    fun encode(message: HDMqttMessage): ByteArray
}

class DefaultMqttConvertor : MQTTConvertor {
    override fun decode(source: ByteArray): ProtocolMQTTContainer<*> {
        //TODO("序列化")
        val json = ""//String(charArrayOf(source))
        val msg =    Json.decodeFromString<ProtocolMQTTContainer<*>>(json)
            ?: throw IllegalStateException("decode result is null")
        return msg
    }

    override fun encode(message: HDMqttMessage): ByteArray {
        //TODO("序列化")
        return Json.encodeToString(message).encodeToByteArray()
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