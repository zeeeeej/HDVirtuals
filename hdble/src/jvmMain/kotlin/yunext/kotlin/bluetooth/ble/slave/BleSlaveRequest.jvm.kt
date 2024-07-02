package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics

actual class BleSlaveRequest: Work

actual fun generateBleSlaveRequest(
    characteristics: XBleCharacteristics,
    value: ByteArray,
    confirm: Boolean,
): BleSlaveRequest {
    TODO("Not yet implemented")
}