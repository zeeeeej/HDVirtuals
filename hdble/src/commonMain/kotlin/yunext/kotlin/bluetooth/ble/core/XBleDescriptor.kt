package yunext.kotlin.bluetooth.ble.core

interface XBleDescriptor {
    val characteristicsUUID:String
    val uuid:String
    val value:ByteArray
}