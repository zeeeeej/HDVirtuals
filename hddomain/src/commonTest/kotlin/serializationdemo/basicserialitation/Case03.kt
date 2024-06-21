package serializationdemo.basicserialitation

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

//@Serializable
//class Project3(path: String) {
//    val owner: String = path.substringBefore('/')
//    val name: String = path.substringAfter('/')
//}

@Serializable
class Project3 private constructor(val owner: String, val name: String) {
    constructor(path: String) : this(
        owner = path.substringBefore('/'),
        name = path.substringAfter('/')
    )

    val path: String
        get() = "$owner/$name"
}

class Case03 {
    @Test
    fun testProject(){
        println("start...")

        println("----------------")

        val data = Project3("kotlin/kotlinx.serialization").apply {
            // stars = 1234
        }
        println(Json.encodeToString(data))

        println("----------------")

        println("end.")
    }
}