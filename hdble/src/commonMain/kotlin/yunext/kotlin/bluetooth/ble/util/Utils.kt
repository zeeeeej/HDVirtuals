package yunext.kotlin.bluetooth.ble.util

private val UUID_END_BYTE = byteArrayOf(
    0x00, 0x00, 0x10, 0x00,
    0x80.toByte(), 0x00, 0x00, 0x80.toByte(), 0x5f, 0x9b.toByte(), 0x34, 0xfb.toByte()
)
private val UUID_START_BYTE = byteArrayOf(0x00, 0x00)
private const val UUID_END = "-0000-1000-8000-00805f9b34fb"
private const val UUID_START = "0000"
 fun uuidFromShort(short: String) = "${UUID_START}${short.lowercase()}${UUID_END}"
 fun parseShortUuid(uuid: String) = uuid.substring(UUID_START.length, UUID_START.length + 4)

internal fun uuidFromShort(short: ByteArray) = UUID_START_BYTE + short + UUID_END_BYTE
internal fun parseShortUuid(uuid: ByteArray) =
    uuid.copyOfRange(UUID_START_BYTE.size, UUID_START_BYTE.size + 2)