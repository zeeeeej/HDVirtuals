package yunext.kotlin.rtc.procotol

import yunext.kotlin.bluetooth.ble.util.uuidFromShort


class RTCCmdData(
    val cmd: RTCCmd,
    val serviceShortUUID: String ="",
    val characteristicsShortUUID: String="",
    val serviceUUID: String = "",
    val characteristicsUUID: String = "",
)

val RTCCmdData.serviceRealUUID:String
    get() = if (serviceShortUUID.isNotEmpty()) uuidFromShort(serviceShortUUID) else serviceUUID

val RTCCmdData.characteristicsRealUUID:String
    get() = if (serviceShortUUID.isNotEmpty()) uuidFromShort(serviceShortUUID) else characteristicsUUID

enum class RTCCmd(
    val byte: Byte,
    val maxLength: Int,
    val characteristicsType: RTCCharacteristicsType,
    val desc: String,
) {
    /* 鉴权下发 */
    AuthenticationWrite(
        0xB1.toByte(),
        0x50,
        RTCWriteNoResponseCharacteristicsType,
        "写入鉴权数据（16进制字符串）"
    ),

    /* 鉴权结果 */
    AuthenticationNotify(
        0xB2.toByte(),
        0x01,
        RTCNotifyCharacteristicsType,
        "鉴权结果1:成功 0:失败"
    ),

    /* 参数设置 */
    ParameterWrite(0x00.toByte(), 0xEA, RTCWriteNoResponseCharacteristicsType, "参数设置"),

    /* 时间戳设置 */
    TimestampWrite(0x01.toByte(), 0x04, RTCWriteNoResponseCharacteristicsType, "时间戳设置")
    ;
}

class ParameterPacket(

    /* 参数-参数值 nBYTE */
    val data: ParameterData,
) {
    /* data长度 1BYTE */
    val length: Byte = (data.key.size + data.value.size).toByte()
}

class ParameterData(
    /* 参数 >=3BYTE ascii*/
    val key: ByteArray,
    /* 参数值 nBYTE */
    val value: ByteArray,
)

val ParameterData.payload: ByteArray
    get() = key + value

enum class ParameterKey {
    /* 温度设置值 */
    F01,

    /* 开机温度回差 */
    F02,

    /* 温度传感器矫正 */
    F03,

    /* 温度最高设定值 */
    F04,

    /* 温度最低设定值 */
    F05,

    /* 压缩机最小停机时间 */
    F06,

    /* 化霜周期 */
    F07,

    /* 化霜时间 */
    F08,

    /* 故障运行时间 */
    F09,

    /* 故障停机时间 */
    F10,

    /* 化霜显示模式 */
    F11,

    /* 高温报警温度 */
    F12,

    /* 低温报警温度 */
    F13,

    /* 温度报警延时 */
    F14,

    /* 柜温升高1℃显示延时 */
    F15
    ;
}

fun parameterKeyOrNull(data: ByteArray) = when {
    data.contentEquals(ParameterKey.F01.assci) -> ParameterKey.F01
    data.contentEquals(ParameterKey.F02.assci) -> ParameterKey.F02
    data.contentEquals(ParameterKey.F03.assci) -> ParameterKey.F03
    data.contentEquals(ParameterKey.F04.assci) -> ParameterKey.F04
    data.contentEquals(ParameterKey.F05.assci) -> ParameterKey.F05
    data.contentEquals(ParameterKey.F06.assci) -> ParameterKey.F06
    data.contentEquals(ParameterKey.F07.assci) -> ParameterKey.F07
    data.contentEquals(ParameterKey.F08.assci) -> ParameterKey.F08
    data.contentEquals(ParameterKey.F09.assci) -> ParameterKey.F09
    data.contentEquals(ParameterKey.F10.assci) -> ParameterKey.F10
    data.contentEquals(ParameterKey.F11.assci) -> ParameterKey.F11
    data.contentEquals(ParameterKey.F12.assci) -> ParameterKey.F12
    data.contentEquals(ParameterKey.F13.assci) -> ParameterKey.F13
    data.contentEquals(ParameterKey.F14.assci) -> ParameterKey.F14
    data.contentEquals(ParameterKey.F15.assci) -> ParameterKey.F15
    else -> null
}

val ParameterKey.text: String
    get() = when (this) {
        ParameterKey.F01 -> "温度设置值"
        ParameterKey.F02 -> "开机温度回差"
        ParameterKey.F03 -> "温度传感器矫正"
        ParameterKey.F04 -> "温度最高设定值"
        ParameterKey.F05 -> "温度最低设定值"
        ParameterKey.F06 -> "压缩机最小停机时间"
        ParameterKey.F07 -> "化霜周期"
        ParameterKey.F08 -> "化霜时间"
        ParameterKey.F09 -> "故障运行时间"
        ParameterKey.F10 -> "故障停机时间"
        ParameterKey.F11 -> "化霜显示模式"
        ParameterKey.F12 -> "高温报警温度"
        ParameterKey.F13 -> "低温报警温度"
        ParameterKey.F14 -> "温度报警延时"
        ParameterKey.F15 -> "柜温升高1℃显示延时"
    }

val ParameterKey.unit: String
    get() = when (this) {
        ParameterKey.F01 -> "℃"
        ParameterKey.F02 -> "℃"
        ParameterKey.F03 -> "℃"
        ParameterKey.F04 -> "℃"
        ParameterKey.F05 -> "℃"
        ParameterKey.F06 -> "Min"
        ParameterKey.F07 -> "Hour"
        ParameterKey.F08 -> "Min"
        ParameterKey.F09 -> "Min"
        ParameterKey.F10 -> "Min"
        ParameterKey.F11 -> "/"
        ParameterKey.F12 -> "℃"
        ParameterKey.F13 -> "℃"
        ParameterKey.F14 -> "Min"
        ParameterKey.F15 -> "Sec"
    }

val ParameterKey.assci: ByteArray
    get() = this.name.fold(byteArrayOf()) { acc: ByteArray, c: Char ->
        acc + byteArrayOf(c.code.toByte())
    }

val String.toAscii: ByteArray
    get() = this.fold(byteArrayOf()) { acc: ByteArray, c: Char ->
        acc + byteArrayOf(c.code.toByte())
    }




