package yunext.kotlin.bluetooth.ble.core

interface XBleCharacteristics {
    val serviceUUID: String
    val uuid: String
    val descriptors: List<XBleDescriptor>
    val properties: List<XCharacteristicsProperty>
    val permissions: List<XCharacteristicsPermission>
    val value:ByteArray
}

expect fun generateXBleCharacteristics(
     serviceUUID: String,
     uuid: String,
     descriptors: List<XBleDescriptor>,
     properties: List<XCharacteristicsProperty>,
     permissions: List<XCharacteristicsPermission>,
     value:ByteArray
):XBleCharacteristics

enum class XCharacteristicsProperty {
    READ, WRITE, INDICAte, WriteWithoutResponse, Notify;
}

enum class XCharacteristicsPermission {
    READ, WRITE, NONE;
}

 fun generateXBleCharacteristicsOnlyByUUID(uuid: String) =
    generateXBleCharacteristics(
        serviceUUID = "",
        uuid = uuid,
        descriptors = emptyList(),
        properties = emptyList(),
        permissions = emptyList(),
        value = byteArrayOf()
    )