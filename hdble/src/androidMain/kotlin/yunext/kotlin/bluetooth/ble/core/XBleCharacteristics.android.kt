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
    properties = convertProperties(),// TODO properties
    permissions = emptyList(),// TODO permissions
    value = this.value ?: byteArrayOf()
)

private typealias BluetoothGattCharacteristicProperties = Int

private fun BluetoothGattCharacteristic.convertProperties(): List<XCharacteristicsProperty> {
    val properties: BluetoothGattCharacteristicProperties = this.properties
    val list = properties.convertProperties()
    println("uuid : ${this.uuid} ,properties : $properties ,list:${list.joinToString { it.name }}")
    return list
}

internal fun BluetoothGattCharacteristicProperties.convertProperties(): List<XCharacteristicsProperty> {
    //
    // Read ：0x02
    // WriteWithoutResponse ：0x04
    // Write ：0x08
    // Notify ：0x10
    // Indicate ：0x20
    // 14  N W  WN  R
    // 000 0 1  1   1 0
    // 18
    // 000 1 0  0   1 0
    // 30
    // 000 1 1  1   1 0
    val list = mutableListOf<XCharacteristicsProperty>()
    if ((this and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
        list.add(XCharacteristicsProperty.Notify)
    }
    if ((this and BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
        list.add(XCharacteristicsProperty.READ)
    }
    if ((this and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
        list.add(XCharacteristicsProperty.WRITE)
    }
    if ((this and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
        list.add(XCharacteristicsProperty.WriteWithoutResponse)
    }
    if ((this and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
        list.add(XCharacteristicsProperty.INDICAte)
    }
    return list
}

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