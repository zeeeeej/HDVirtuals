package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work

actual class BleSlaveResponse:Work

actual fun generateBleSlaveResponse(
    requestId: Int,
    offset: Int,
    value: ByteArray?,
    success: Boolean,
): BleSlaveResponse {
    TODO()
//   return BleSlaveResponse(requestId,offset,value,success)
}