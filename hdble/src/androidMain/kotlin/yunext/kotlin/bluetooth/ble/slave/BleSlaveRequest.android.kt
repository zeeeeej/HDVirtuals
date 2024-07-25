package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleService

actual class BleSlaveRequest   (
    val service: XBleService,
    val notifyCharacteristic: XBleCharacteristics,
    val value: ByteArray,
    val confirm: Boolean = false,
):Work

actual fun generateBleSlaveRequest(
    service: XBleService,
    characteristics: XBleCharacteristics,
    value: ByteArray,
    confirm: Boolean
) :BleSlaveRequest{
    return BleSlaveRequest(service,characteristics,value,confirm)
}

