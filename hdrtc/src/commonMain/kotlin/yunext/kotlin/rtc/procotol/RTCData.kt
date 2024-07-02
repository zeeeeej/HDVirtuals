package yunext.kotlin.rtc.procotol

class RTCData internal constructor(
    /* 帧头 2BYTE */
    val head: ByteArray,
    /* 命令 1BYTE */
    val cmd: Byte,
    /* 预留 1BYTE */
    val reverse: Byte,
    /* payload长度 1BYTE */
    val length: Byte,
    /* payload nBYTE n <=240 - 6（其他）= 234  */
    val payload: ByteArray,
    /* CRC 1BYTE 各BYTE之和%256 */
    val crc: Byte,
) {
    init {
        require(head.size == 2) {
            "错误的帧头：${head}"
        }
        require(payload.size in (0..MAX_MTU - 6)) {
            "错误的预留：${payload}"
        }
    }
}

fun RTCData.toByteArray() =
    (this.head + this.cmd + this.reverse + this.length).let { cur ->
        if (this.payload.isEmpty()) {
            cur + crc
        } else {
            cur + this.payload + crc
        }
    }



