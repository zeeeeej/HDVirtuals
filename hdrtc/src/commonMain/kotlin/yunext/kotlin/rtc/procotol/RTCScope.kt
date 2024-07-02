package yunext.kotlin.rtc.procotol

import com.yunext.kmp.common.util.hdMD5

interface RTCScope {

    /**
     * 是否是RTC设备广播
     */
    fun createBroadcastContent(mac: String): String
    fun checkBroadcast(data: ByteArray): Boolean

    /**
     * 解析广播
     */
    fun parseMacFromBroadcast(data: ByteArray): String?

    /**
     * 发送鉴权
     */
    fun rtcDataForAuthenticationWrite(accessKey: String, mac: String): RTCData

    /**
     * 解析鉴权结果
     */
    fun rtcDataForAuthenticationNotify(data: ByteArray): Boolean

    /**
     * 发送参数设置
     */
    fun rtcDataForParameterWrite(
        parameterPackets: List<ParameterPacket>,
    ): RTCData

    /**
     * 发送时间戳
     */
    fun rtcDataForTimestampWrite(
        timestamps: Long,
    ): RTCData
}


internal class RTCScopeImpl : RTCScope {
    override fun createBroadcastContent(mac: String): String {
        return "${RTC_PREFIX}$mac"
    }

    override fun checkBroadcast(data: ByteArray): Boolean {
        return data.decodeToString().startsWith(RTC_PREFIX)
    }

    override fun parseMacFromBroadcast(data: ByteArray): String? {
        val str = data.decodeToString()
        return if (str.startsWith(RTC_PREFIX)) {
            str.substring(RTC_PREFIX.length, str.length)
        } else null
    }

    override fun rtcDataForParameterWrite(
        parameterPackets: List<ParameterPacket>,
    ): RTCData {
        return rtcData(RTCCmd.ParameterWrite, parameterPackets.toPayload())
    }

    override fun rtcDataForTimestampWrite(
        timestamps: Long,
    ): RTCData {
        val payload = timestamps.toPayload()
        return rtcData(RTCCmd.TimestampWrite, payload)
    }

    override fun rtcDataForAuthenticationWrite(accessKey: String, mac: String): RTCData {
        val authentication = createAuthentication(accessKey, mac) ?: ""
        check(authentication.isNotEmpty()) {
            "authentication为空. accessKey:$accessKey mac:$mac"
        }
        return rtcData(RTCCmd.AuthenticationWrite, authentication.encodeToByteArray())
    }

    override fun rtcDataForAuthenticationNotify(data: ByteArray): Boolean {
        require(data.size >= 6) {
            "数据错误$data"
        }
        require(byteArrayOf(data[0], data[1]).contentEquals(HEAD)) {
            "数据头错误$data"
        }
        val length = data[4]
        require(length.toInt() == 0 && data.size == 6) {
            "数据长度错误$data"
        }
        val crc = (data.dropLast(1).sum() % 256).toByte()
        require(crc == data[data.size - 1]) {
            "crc错误$data"
        }

        require(data[2] == RTCCmd.AuthenticationNotify.byte) {
            "cmd错误$data"
        }

        require(length.toInt() == 1) {
            "数据长度错误$data"
        }
        return data[5].toInt() != 0
    }

    private fun createAuthentication(accessKey: String, mac: String): String? {
        return hdMD5("#${mac}#$accessKey")
    }

    private fun rtcData(cmd: RTCCmd, payload: ByteArray): RTCData {
        val head = HEAD
        val cmdData = cmd.byte
        val reverse = REVERSE
        val length = payload.size.toByte()
        val total = (head + cmdData + reverse + payload + length).sum()
        val crc = (total % 256).toByte()
        return RTCData(
            head = head,
            cmd = cmdData, reverse = reverse, payload = payload, length = length, crc = crc
        )
    }

    @Suppress("unused")
    private fun List<ParameterPacket>.toPayload(): ByteArray {
        if (this.isEmpty()) {
            return byteArrayOf()
        }
        // val keys = ParameterKey.entries
        return this.fold(byteArrayOf()) { acc: ByteArray, parameterPacket: ParameterPacket ->
            val length = parameterPacket.length
            val data = parameterPacket.data
            val key = data.key
            //check(key in keys) //检查key
            check(key.size >= 3) {
                "key>=3错误 $key"
            }
            val value = data.value
            acc + byteArrayOf(length) + key + value
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    private fun Long.toPayload(): ByteArray {
        val second = this / 1000
        val toHexString = second.toHexString()
        val encodeToByteArray = toHexString.encodeToByteArray()
        return when (END) {
            End.Big -> encodeToByteArray
            End.Small -> encodeToByteArray.reversedArray()
        }
    }

    private fun ByteArray.byteArrayToTimestamp(): Long {
        require(this.size == 4) {
            "size不等于4 $this"
        }
        return when (END) {
            End.Big -> (this[0].toInt() shl (3 * 8)) +
                    (this[1].toInt() shl (2 * 8)) +
                    (this[2].toInt() shl (1 * 8)) +
                    (this[3].toInt()).toLong()

            End.Small -> (this[3].toInt() shl (3 * 8)) +
                    (this[2].toInt() shl (2 * 8)) +
                    (this[1].toInt() shl (1 * 8)) +
                    (this[0].toInt()).toLong()
        }

    }

    companion object {
        private const val RTC_PREFIX = "RTC_"

        private val HEAD by lazy {
            byteArrayOf(0x5a, 0xa5.toByte())
        }

        private val REVERSE: Byte by lazy {
            0x00
        }

        val END = End.Big

    }
}

internal const val MAX_MTU = 240

enum class End {
    Big, Small;
}

