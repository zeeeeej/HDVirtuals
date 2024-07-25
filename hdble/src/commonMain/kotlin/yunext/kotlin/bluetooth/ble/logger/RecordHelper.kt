package yunext.kotlin.bluetooth.ble.logger

import com.yunext.kmp.common.util.currentTime
import yunext.kotlin.bluetooth.ble.BluetoothConstant

 internal class RecordHelper(private val threshold: Long = 2000 ) {

    private val loggerList: MutableList<XBleRecord> = mutableListOf()
    private var loggerIndex: Long = 0
    val list: List<XBleRecord>
        get() = loggerList.toList()

     private var callback:BleRecordCallback? = null

     @Deprecated("delete")
     fun register(callback:BleRecordCallback){
         this.callback =callback
     }

    internal fun add(msg: String, type: XBleRecordType):XBleRecord{
        loggerIndex++
        if (loggerIndex >= threshold) {
            clear()
        }
        val newLog = XBleRecord(
            index = loggerIndex,
            tag = BluetoothConstant.TAG,
            msg = msg,
            type = type,
            timestamp = currentTime()
        )
        loggerList.add(newLog)
        callback?.invoke(newLog)
        return newLog
    }

    fun clear() {
        callback = null
        loggerIndex = 0
        loggerList.clear()
    }
}