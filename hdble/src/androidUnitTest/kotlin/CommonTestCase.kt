import korlibs.encoding.toHexStringUpper
import org.junit.Test
import kotlin.test.assertContentEquals

class CommonTestCase {


    @Test
    fun test(){
        val content = "hadlinks".toByteArray()
        println("content:${content.toHexStringUpper()}")
        assertContentEquals(content, byteArrayOf(0x68,0x61,0x64,0x6c,0x69,0x6e,0x6b,0x73))
    }
}