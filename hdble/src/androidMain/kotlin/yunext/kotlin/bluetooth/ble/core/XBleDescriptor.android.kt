package yunext.kotlin.bluetooth.ble.core

import android.bluetooth.BluetoothGattDescriptor

actual fun generateXBleDescriptor(
    characteristicsUUID: String,
    uuid: String,
    value: ByteArray,
): XBleDescriptor {
    return AndroidBleDescriptor(characteristicsUUID, uuid, value)
}

class AndroidBleDescriptor internal constructor(
    override val characteristicsUUID: String,
    override val uuid: String,
    override val value: ByteArray,
) : XBleDescriptor

internal fun BluetoothGattDescriptor.asDescriptor() = generateXBleDescriptor(
    characteristicsUUID = this.characteristic.uuid.toString(),
    uuid = this.uuid.toString(),
    value = this.value ?: byteArrayOf()
)