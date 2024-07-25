package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleService

expect class BleSlaveRequest : Work

expect fun generateBleSlaveRequest(
    service: XBleService,
    characteristics: XBleCharacteristics,
    value: ByteArray,
    confirm: Boolean = false,
): BleSlaveRequest


