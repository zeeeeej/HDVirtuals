package yunext.kotlin.bluetooth.ble.core

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.clj.fastble.data.BleDevice

class AndroidBleDevice internal constructor(
    override val deviceName: String?,
    override val address: String,

) : XBleDevice

actual fun generateXBleDevice(deviceName: String, address: String): XBleDevice {
    return AndroidBleDevice(deviceName = deviceName, address = address)
}

internal fun BleDevice.asDevice() = AndroidBleDevice(
    deviceName = this.name, address = this.mac,
)

@SuppressLint("MissingPermission")
internal fun BluetoothDevice.asDevice() = AndroidBleDevice(
    deviceName = this.name,
    address = this.address
)
