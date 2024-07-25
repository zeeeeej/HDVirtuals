import yunext.kotlin.bluetooth.ble.core.convertProperties
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConvertPropertiesTestCase {

    @Test
    fun `convertProperties`() {
        val read = 0x02
        val write = 0x08
        val writeNo = 0x04
        val notify = 0x10

        // 000 0 1  1   1 0
        val value1 = 14

        assertTrue {
            (value1 and read) != 0
        }
        assertTrue {
            (value1 and write) != 0
        }
        assertTrue {
            (value1 and writeNo) != 0
        }

        assertFalse {
            (value1 and notify) != 0
        }
        //000 1 0  0   1 0
        val value2 = 18
        assertTrue {
            (value2 and read) != 0
        }
        assertFalse {
            (value2 and write) != 0
        }
        assertFalse {
            (value2 and writeNo) != 0
        }

        assertTrue {
            (value2 and notify) != 0
        }

        //000 1 1  1   1 0
        val value3 = 30
        assertTrue {
            (value3 and read) != 0
        }
        assertTrue {
            (value3 and write) != 0
        }
        assertTrue {
            (value3 and writeNo) != 0
        }

        assertTrue {
            (value3 and notify) != 0
        }

        assertTrue(value1.convertProperties().size==3)
        assertTrue(value2.convertProperties().size==2)
        assertTrue(value3.convertProperties().size==4)

    }
}