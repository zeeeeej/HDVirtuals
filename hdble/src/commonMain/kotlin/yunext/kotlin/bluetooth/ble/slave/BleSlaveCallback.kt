package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.core.XBleEvent
import yunext.kotlin.bluetooth.ble.logger.XBleRecord

typealias BleSlaveCallback = (BleSlaveServerEvent) -> Unit

typealias BleSlaveStatusCallback = (BleSlaveStatusEvent) -> Unit

sealed interface BleSlaveStatusEvent : XBleEvent

//class BleSlaveConfigurationDeviceName(val deviceName: String) : BleSlaveConfigurationEvent
//class BleSlaveConfigurationAddress(val address: String) : BleSlaveConfigurationEvent
class BleSlaveConfigurationBroadcasting(val broadcasting: BroadcastStatus) :
    BleSlaveStatusEvent

class BleSlaveConfigurationConnectedDevice(val connectedDevice: ConnectStatus) :
    BleSlaveStatusEvent