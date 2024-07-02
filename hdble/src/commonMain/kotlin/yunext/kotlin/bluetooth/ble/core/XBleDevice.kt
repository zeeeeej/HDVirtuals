package yunext.kotlin.bluetooth.ble.core

interface XBleDevice {
    val deviceName: String?
    val address: String
}

expect fun generateXBleDevice(deviceName:String,address:String) :XBleDevice