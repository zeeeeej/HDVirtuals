package com.yunext.kmp.mqtt.virtuals.protocol

import kotlinx.serialization.encodeToString


val ProtocolMQTTMessage.payload:ByteArray
    get() = when(this){
        is DataMQTTMessage -> hdJson.encodeToString(data).encodeToByteArray()
        is DataReplyMQTTMessage -> data
        is InfoMQTTMessage -> data
        is OnlineMQTTMessage -> hdJson.encodeToString(data).encodeToByteArray()
        is ReplyServiceMQTTMessage -> data
        is ReportMQTTMessage -> data
        is SetMQTTMessage -> data
        is SetRepayMQTTMessage -> data
        is TimestampMQTTMessage -> data
        is TimestampReplyMQTTMessage -> data
    }
sealed interface ProtocolMQTTMessage {
    // 通道
    val topic: ProtocolMQTTTopic

    // 命令
    val cmd: ProtocolMQTTCmd

//    // 数据
//    val data: ByteArray

    val qos: Int
        get() = 1
    val retain: Int
        get() = 0

    // 描述
    val desc: String

    val pub: Array<ProtocolMQTTRule>
    val sub: Array<ProtocolMQTTRule>
}

interface UpStream
interface DownStream

/**
 * 1.info
 * 设备上电时上报的基本信息
 * 无回复
 */
class InfoMQTTMessage(val data: ByteArray) : ProtocolMQTTMessage, UpStream {
    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.UP
    override val cmd: ProtocolMQTTCmd = InfoUpCmd
    override val desc: String = "设备上电时上报的基本信息 无回复"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
}

/**
 * 2.report
 * 设备数据上报
 * 无回复
 */
class ReportMQTTMessage(val data: ByteArray) : ProtocolMQTTMessage, UpStream {
    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.UP
    override val cmd: ProtocolMQTTCmd = ReportCmd
    override val desc: String = "设备数据上报"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
}


/**
 * 3.set
 * 平台下发
 * 有回复
 */
class SetMQTTMessage(val data: ByteArray) : ProtocolMQTTMessage, DownStream {
    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.DOWN
    override val cmd: ProtocolMQTTCmd = SetCmd
    override val desc: String = "平台下发"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)
}

class SetRepayMQTTMessage(val data: ByteArray) : ProtocolMQTTMessage, UpStream {
    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.UP
    override val cmd: ProtocolMQTTCmd = SetCmd
    override val desc: String = "回复平台下发"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)
}

/**
 * 4.data
 * 平台可发出消息查询当前设备参数（包括运行状态与属性）
 * 回复 参数值
 */
class DataMQTTMessage(private val keys: List<String>) :
    ProtocolMQTTMessage {
    val data: Map<String, List<String>>
        get() = mapOf("keys" to keys)


    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.DOWN
    override val cmd: ProtocolMQTTCmd = DataCmd
    override val desc: String = "平台可发出消息查询当前设备参数（包括运行状态与属性）"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)

    val subTopic: ProtocolMQTTTopic = ProtocolMQTTTopic.UP // 回复topic
}

class DataReplyMQTTMessage(val data: ByteArray) : ProtocolMQTTMessage, UpStream {
    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.UP
    override val cmd: ProtocolMQTTCmd = DataCmd
    override val desc: String = "平台可发出消息查询当前设备参数（包括运行状态与属性）"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)

    val subTopic: ProtocolMQTTTopic = ProtocolMQTTTopic.UP // 回复topic
}


/**
 * 5.设备查询当前时间（cmd=timestamp)
 *
 *      回复：
{
"cmd": "timestamp",
"params": {
"timestamp": 1604294613
}
}
 *
 */
class TimestampMQTTMessage(val data: ByteArray) : ProtocolMQTTMessage {

    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.QUERY
    override val cmd: ProtocolMQTTCmd = TimestampCmd
    override val desc: String = "设备查询当前时间"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)


}

class TimestampReplyMQTTMessage(val data: ByteArray) : ProtocolMQTTMessage, UpStream {

    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.REPLY
    override val cmd: ProtocolMQTTCmd = TimestampCmd
    override val desc: String = "回复设备查询当前时间"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
}

/**
 * 5.web通知app设备在离线
 */
class OnlineMQTTMessage(private val online: Boolean) : ProtocolMQTTMessage {
    override val cmd: ProtocolMQTTCmd = OnlineCmd
    val data: Map<String, Boolean>
        get() = mapOf("online" to online)
    override val desc: String = ""
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(
            ProtocolMQTTRule.App.Android,
            ProtocolMQTTRule.App.Ios,
            ProtocolMQTTRule.App.Mini
        )
    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.STATUS
    override val retain: Int
        get() = 1
}


/**
 * 6.服务
 * 设备数据上报
 * 无回复
 */
class ReplyServiceMQTTMessage(val data: ByteArray, serviceId: String) : ProtocolMQTTMessage,
    UpStream {
    override val topic: ProtocolMQTTTopic
        get() = ProtocolMQTTTopic.UP
    override val cmd: ProtocolMQTTCmd = ServiceCmd(serviceId)
    override val desc: String = "设备数据上报"
    override val pub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Device)
    override val sub: Array<ProtocolMQTTRule>
        get() = arrayOf(ProtocolMQTTRule.Web)
}









