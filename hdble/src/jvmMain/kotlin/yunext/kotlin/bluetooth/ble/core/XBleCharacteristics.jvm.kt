package yunext.kotlin.bluetooth.ble.core

actual fun generateXBleCharacteristics(
    serviceUUID: String,
    uuid: String,
    descriptors: List<XBleDescriptor>,
    properties: List<XCharacteristicsProperty>,
    permissions: List<XCharacteristicsPermission>,
    value: ByteArray,
): XBleCharacteristics {
    TODO("Not yet implemented")
}