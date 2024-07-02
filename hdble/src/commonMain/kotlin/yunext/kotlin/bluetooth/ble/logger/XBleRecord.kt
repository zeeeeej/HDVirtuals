package yunext.kotlin.bluetooth.ble.logger

import com.yunext.kmp.common.logger.XLogType
import com.yunext.kmp.common.util.datetimeFormat

interface IXBleRecord {
    val index: Long
    val tag: String
    val msg: String
    val type: XBleRecordType
    val timestamp: Long
}

data class XBleRecord(
    override val index: Long,
    override val tag: String,
    override val msg: String,
    override val type: XBleRecordType,
    override val timestamp: Long,
) : IXBleRecord {
    override fun toString(): String {
        return "[${datetimeFormat { timestamp.toStr("mm:ss SSS") }}]${
            when (type) {
                XBleRecordType.I -> "I"
                XBleRecordType.D -> "D"
                XBleRecordType.W -> "W"
                XBleRecordType.E -> "E"
            }
        }$tag$msg"
    }
}


typealias XBleRecordType = XLogType

typealias BleRecordCallback = (XBleRecord) -> Unit