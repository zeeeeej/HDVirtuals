package yunext.kotlin.rtc.procotol


class RTCCmdData(val cmd: RTCCmd, val serviceUUID: String, val characteristicsUUID: String)

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

val ParameterData.payload:ByteArray
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

val ParameterKey.toAscii :ByteArray
    get() = this.name.fold(byteArrayOf()){
        acc: ByteArray, c: Char ->
        acc + byteArrayOf(c.code.toByte())
    }


