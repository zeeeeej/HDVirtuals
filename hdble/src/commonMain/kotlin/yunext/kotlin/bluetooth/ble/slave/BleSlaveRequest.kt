package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics

expect class BleSlaveRequest: Work


expect fun generateBleSlaveRequest(characteristics: XBleCharacteristics,value:ByteArray,   confirm: Boolean):BleSlaveRequest


