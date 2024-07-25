package yunext.kotlin.bluetooth.ble.core

interface XBleDevice {
    val deviceName: String?
    val address: String
}

val XBleDevice.display: String
    get() = "[${this.address}](${this.deviceName})"

fun XBleDevice.same(other: XBleDevice): Boolean {
    return this.address.equals(other.address, true) && this.deviceName == other.deviceName
}

fun XBleDevice.same(address: String,deviceName: String): Boolean {
    return this.address.equals(address, true) && this.deviceName == deviceName
}

expect fun generateXBleDevice(deviceName: String, address: String): XBleDevice