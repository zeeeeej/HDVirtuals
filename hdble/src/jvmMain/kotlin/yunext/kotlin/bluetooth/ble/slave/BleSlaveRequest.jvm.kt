package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleService

actual class BleSlaveRequest: Work

actual fun generateBleSlaveRequest(
    service: XBleService,
    characteristics: XBleCharacteristics,
    value: ByteArray,
    confirm: Boolean,
): BleSlaveRequest {
    TODO("Not yet implemented")
}