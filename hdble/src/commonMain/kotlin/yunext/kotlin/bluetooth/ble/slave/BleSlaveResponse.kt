package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.Work

expect class BleSlaveResponse: Work


expect fun generateBleSlaveResponse( requestId: Int,  offset: Int,  value: ByteArray?, success:Boolean):BleSlaveResponse