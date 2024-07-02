package yunext.kotlin.bluetooth.ble.core

interface XBleService {
    val uuid: String
    val characteristics: List<XBleCharacteristics>
}

expect fun generateBleService(uuid:String, characteristics: List<XBleCharacteristics> ):XBleService

fun generateBleServiceOnlyByUUID(uuid: String) = generateBleService(uuid, emptyList())