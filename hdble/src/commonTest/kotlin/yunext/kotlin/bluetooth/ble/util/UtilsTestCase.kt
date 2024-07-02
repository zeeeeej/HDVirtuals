package yunext.kotlin.bluetooth.ble.util

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class UtilsTestCase {

    @Test
    fun `测试uuidFromShort`() {
        val short = "a001"
        assertEquals(uuidFromShort(short), "0000a001-0000-1000-8000-00805f9b34fb")
    }

    @Test
    fun `测试parseShortUuid`() {
        val uuid = "0000a001-0000-1000-8000-00805f9b34fb"
        assertEquals(parseShortUuid(uuid), "a001")
    }


    @Test
    fun `测试uuidFromShortByteArray`() {
        val short = byteArrayOf(0xa0.toByte(), 0x01)
        assertContentEquals(uuidFromShort(short),   byteArrayOf(
            0x00, 0x00,
            0xa0.toByte(), 0x01,
            0x00, 0x00, 0x10, 0x00,
            0x80.toByte(), 0x00, 0x00, 0x80.toByte(), 0x5f, 0x9b.toByte(), 0x34, 0xfb.toByte()
        ))
    }

    @Test
    fun `测试parseShortUuidByteArray`() {
        val uuid = byteArrayOf(
            0x00, 0x00,
            0xa0.toByte(), 0x01,
            0x00, 0x00, 0x10, 0x00,
            0x80.toByte(), 0x00, 0x00, 0x80.toByte(), 0x5f, 0x9b.toByte(), 0x34, 0xfb.toByte()
        )
        assertContentEquals( parseShortUuid(uuid),byteArrayOf(0xa0.toByte(), 0x01))
    }
}