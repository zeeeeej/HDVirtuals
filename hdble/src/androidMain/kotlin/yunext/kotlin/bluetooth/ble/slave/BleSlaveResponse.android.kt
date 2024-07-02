package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work

actual class BleSlaveResponse(val requestId: Int, val offset: Int, val value: ByteArray?,val success:Boolean):Work

actual fun generateBleSlaveResponse(
    requestId: Int,
    offset: Int,
    value: ByteArray?,
    success: Boolean,
): BleSlaveResponse {
    return BleSlaveResponse(requestId,offset,value,success)
}