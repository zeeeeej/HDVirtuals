package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.core.XBleEvent
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.logger.XBleRecord

typealias BleSlaveCallback = (BleSlaveServerEvent) -> Unit

typealias BleSlaveStatusCallback = (BleSlaveStatusEvent) -> Unit

sealed interface BleSlaveStatusEvent : XBleEvent

//class BleSlaveConfigurationDeviceName(val deviceName: String) : BleSlaveConfigurationEvent
//class BleSlaveConfigurationAddress(val address: String) : BleSlaveConfigurationEvent
class BleSlaveConfigurationBroadcasting(val broadcasting: BroadcastStatus,val services:List<XBleService>) :
    BleSlaveStatusEvent

class BleSlaveConfigurationConnectedDevice(val connectedDevice: ConnectStatus,val services:List<XBleService>) :
    BleSlaveStatusEvent