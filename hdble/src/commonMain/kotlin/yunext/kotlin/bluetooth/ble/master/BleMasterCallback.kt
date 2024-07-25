package yunext.kotlin.bluetooth.ble.master

class XBleMasterScanResult(val device: yunext.kotlin.bluetooth.ble.core.XBleDevice, val rssi: Int)
typealias XBleMasterConnectCallback = (BleMasterEvent) -> Unit
typealias XBleMasterNotifyCallback = (BleMasterNotifyEvent) -> Unit
typealias XBleMasterReadCallback = (BleMasterReadEvent) -> Unit
typealias XBleMasterWriteCallback = (BleMasterWriteEvent) -> Unit



typealias OnXBleMasterScanningStatusChanged = (BleMasterScanningStatus)->Unit
typealias OnXBleMasterConnectedStatusChanged = (BleMasterConnectedStatus)->Unit