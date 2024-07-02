package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics

actual class BleSlaveRequest(
    val notifyCharacteristic: XBleCharacteristics,
    val value: ByteArray,
    val confirm: Boolean = false,
):Work

actual fun generateBleSlaveRequest(
    characteristics: XBleCharacteristics,
    value: ByteArray,
    confirm: Boolean
) :BleSlaveRequest{
    return BleSlaveRequest(characteristics,value,confirm)
}