package yunext.kotlin.bluetooth.ble.core

import android.bluetooth.BluetoothGattCharacteristic

class AndroidBleCharacteristics internal constructor(
    override val serviceUUID: String,
    override val uuid: String,
    override val descriptors: List<XBleDescriptor>,
    override val properties: List<XCharacteristicsProperty>,
    override val permissions: List<XCharacteristicsPermission>,
    override val value: ByteArray,
) : XBleCharacteristics

actual fun generateXBleCharacteristics(
    serviceUUID: String,
    uuid: String,
    descriptors: List<XBleDescriptor>,
    properties: List<XCharacteristicsProperty>,
    permissions: List<XCharacteristicsPermission>,
    value: ByteArray,
): XBleCharacteristics {
    return AndroidBleCharacteristics(serviceUUID, uuid, descriptors, properties, permissions, value)
}




internal fun BluetoothGattCharacteristic.asCharacteristics() = generateXBleCharacteristics(
    serviceUUID = this.service.uuid.toString(),
    uuid = this.uuid.toString(),
    descriptors = emptyList(),// TODO bluetoothGattCharacteristic.descriptors.
    properties = emptyList(),// TODO properties
    permissions = emptyList(),// TODO permissions
    value = this.value ?: byteArrayOf()
)


internal fun XCharacteristicsProperty.toProperty() = when (this) {
    XCharacteristicsProperty.READ -> BluetoothGattCharacteristic.PROPERTY_READ
    XCharacteristicsProperty.WRITE -> BluetoothGattCharacteristic.PROPERTY_WRITE
    XCharacteristicsProperty.INDICAte -> BluetoothGattCharacteristic.PROPERTY_INDICATE
    XCharacteristicsProperty.WriteWithoutResponse -> BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
    XCharacteristicsProperty.Notify -> BluetoothGattCharacteristic.PROPERTY_NOTIFY
}

internal fun XCharacteristicsPermission.toPermission() = when (this) {
    XCharacteristicsPermission.READ -> BluetoothGattCharacteristic.PERMISSION_READ
    XCharacteristicsPermission.WRITE -> BluetoothGattCharacteristic.PERMISSION_WRITE
    XCharacteristicsPermission.NONE -> 0
}