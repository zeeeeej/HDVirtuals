package yunext.kotlin.rtc.procotol

import com.yunext.kmp.common.util.hdMD5
import korlibs.encoding.hexLower
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.XCharacteristicsProperty
import kotlin.experimental.and

interface RTCScope {

    /**
     * 是否是RTC设备广播
     */
    fun createBroadcastContent(mac: String): String

    /**
     * 找出匹配的ServiceAndCharacteristics for write notify
     */
    fun tryGetService(list: List<XBleService>): ServiceAndCharacteristics?
    fun checkBroadcast(data: ByteArray): Boolean

    fun checkBroadcastByName(deviceName: String): Boolean

    /**
     * 设备回复
     */
    fun parseDataFromSlave(data: ByteArray): RTCData

    /**
     * 解析广播
     */
    fun parseMacFromBroadcast(data: ByteArray): String?

    /**
     * 发送鉴权
     */
    fun rtcDataForAuthenticationWrite(accessKey: String, mac: String): RTCData


    /**
     * 发送鉴权结果
     */
    fun rtcDataForAuthenticationNotify(success: Boolean): RTCData

    /**
     * 验证鉴权结果
     */
    fun rtcDataForAuthenticationNotify(authed: ByteArray, accessKey: String, mac: String): Boolean

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

    /**
     * 解析参数
     */
    fun parseParameterPacketListFromPayload(
        payload: ByteArray,
    ): List<ParameterPacket>
}

data class ServiceAndCharacteristics(
    val service: XBleService,
    val write: XBleCharacteristics,
    val notify: XBleCharacteristics,

    )

internal class RTCScopeImpl() : RTCScope {
    override fun createBroadcastContent(mac: String): String {
        return "${RTC_PREFIX}$mac"
    }

    private fun List<XCharacteristicsProperty>.eq(dest: Array<XCharacteristicsProperty>): Boolean {
        return this.toTypedArray().sortedArray().contentEquals(dest.sortedArray())
    }

    override fun tryGetService(list: List<XBleService>): ServiceAndCharacteristics? {
        // 当前实现的为4G双模蓝牙的实现
        // 当属性为read&notify时为notify
        // 当属性为read&write&write-without-response时为write
        // 且在同一个service内
        fun tryGetCharacteristicsNotify(service: XBleService): XBleCharacteristics? {
            if (service.characteristics.isEmpty()) return null
            service.characteristics.forEach { xBleCharacteristics ->
                if (xBleCharacteristics.properties.eq(CHANNEL_Notify)) {
                    return xBleCharacteristics
                }
            }
            return null
        }

        fun tryGetCharacteristicsWrite(service: XBleService): XBleCharacteristics? {
            if (service.characteristics.isEmpty()) return null
            service.characteristics.forEach { xBleCharacteristics ->
                if (xBleCharacteristics.properties.eq(CHANNEL_Write)) {
                    return xBleCharacteristics
                }
            }
            return null
        }

        var destService: XBleService? = null
        var destWrite: XBleCharacteristics? = null
        var destNotify: XBleCharacteristics? = null
        list.forEach { service ->
            val tryGetCharacteristicsNotify = tryGetCharacteristicsNotify(service)
            val tryGetCharacteristicsWrite = tryGetCharacteristicsWrite(service)
            if (tryGetCharacteristicsNotify != null && tryGetCharacteristicsWrite != null) {
                destService = service
                destWrite = tryGetCharacteristicsWrite
                destNotify = tryGetCharacteristicsNotify
            }
        }
        val service = destService
        val write = destWrite
        val notify = destNotify
        if (write != null && notify != null && service != null) {
            return ServiceAndCharacteristics(service, write, notify)
        }
        return null
    }

    override fun checkBroadcast(data: ByteArray): Boolean {
        return data.decodeToString().startsWith(RTC_PREFIX)
    }

    override fun checkBroadcastByName(deviceName: String): Boolean {
        return deviceName.startsWith(RTC_PREFIX)
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

    @OptIn(ExperimentalStdlibApi::class)
    override fun parseParameterPacketListFromPayload(payload: ByteArray): List<ParameterPacket> {
        println("解析的payload数据：${payload.toHexString()}")
        if (payload.isEmpty()) return emptyList()
        var pos = 0
        val list: MutableList<ParameterPacket> = mutableListOf()
        while (pos < payload.size) {
            val length = (payload[pos] and 0xFF.toByte()).toInt()
            println(" -----------------")
            println("   ->解析的payload数据0：pos=${pos} length=${length.toHexString()}")
            val dataByteArray = payload.copyOfRange(pos, pos + length)
            println("   ->解析的payload数据1：dataByteArray=${dataByteArray.toHexString()}")
            val data = ParameterData(
                dataByteArray.copyOfRange(0, 3), dataByteArray.copyOfRange(3, dataByteArray.size)
            )
            println("   ->解析的payload数据2：k=${data.key.toHexString()} v=${data.value.toHexString()}")
            val packet = ParameterPacket(data)
            list.add(packet)
            pos += length + 1
        }
        println("解析的payload数据 结束：${list.size}")
        return list
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun rtcDataForAuthenticationWrite(accessKey: String, mac: String): RTCData {
        val authentication = createAuthentication(accessKey, mac) ?: ""
        println("accessKey:$accessKey ,mac:$mac ,authentication:$authentication <>${authentication.length}")
        check(authentication.isNotEmpty()) {
            "authentication为空. accessKey:$accessKey mac:$mac"
        }
        return rtcData(RTCCmd.AuthenticationWrite, authentication.hexToByteArray())
    }


    override fun rtcDataForAuthenticationNotify(success: Boolean): RTCData {
        return rtcData(
            RTCCmd.AuthenticationNotify, if (success) byteArrayOf(0x01) else byteArrayOf(0x00)
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun rtcDataForAuthenticationNotify(
        authed: ByteArray,
        accessKey: String,
        mac: String,
    ): Boolean {
        val internalAuthed =
            createAuthentication(accessKey, mac)?.hexToByteArray() ?: return false
        return internalAuthed.contentEquals(authed)
    }

    private fun createAuthentication(accessKey: String, mac: String): String? {
        return hdMD5("#${mac}#$accessKey")?.lowercase()
    }

    // 5aa5 b1 00 20 6336633033323233313137393732 32
    // 5aa5b100203366616531623837363833373163653330336234363234633333663061646561b4
    // 5aa5b10020336661653162383736383337316365 20//
    // 5aa5 b1 00 20 3263613066383430636638646561636461393763653664376431313430613164 74
    override fun parseDataFromSlave(data: ByteArray): RTCData {
        require(data.size >= 6) {
            "数据错误$data"
        }
        require(byteArrayOf(data[0], data[1]).contentEquals(HEAD)) {
            "数据头错误$data"
        }
        val length = data[4]
        if (length.toInt() == 0) {
            require(data.size == 6) {
                "数据长度错误2$data"
            }
        } else {
            require(data.size - 6 == length.toInt()) {
                "数据长度错误1$data"
            }
        }

        val crc = (data.dropLast(1).sum() % 256).toByte()
        require(crc == data[data.size - 1]) {
            "crc错误$data"
        }
        val cmd = data[2]
        val payload = if (length.toInt() == 0) byteArrayOf() else data.copyOfRange(5, 5 + length)
        return RTCData(
            head = HEAD, cmd = cmd, reverse = 0x00, length = length, payload = payload, crc = crc
        )
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
            cmd = cmdData,
            reverse = reverse,
            payload = payload,
            length = length,
            crc = crc
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
            End.Big -> (this[0].toInt() shl (3 * 8)) + (this[1].toInt() shl (2 * 8)) + (this[2].toInt() shl (1 * 8)) + (this[3].toInt()).toLong()

            End.Small -> (this[3].toInt() shl (3 * 8)) + (this[2].toInt() shl (2 * 8)) + (this[1].toInt() shl (1 * 8)) + (this[0].toInt()).toLong()
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

        private val CHANNEL_Notify = arrayOf(
            XCharacteristicsProperty.READ, XCharacteristicsProperty.Notify
        )

        private val CHANNEL_Write = arrayOf(
            XCharacteristicsProperty.READ,
            XCharacteristicsProperty.WRITE,
            XCharacteristicsProperty.WriteWithoutResponse
        )


    }
}

internal const val MAX_MTU = 240

enum class End {
    Big, Small;
}

