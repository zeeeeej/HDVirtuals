package serializationdemo.basicserialitation

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

@Serializable
private class Project2(
    // name is a property with backing field -- serialized
    var name: String
) {
    var stars: Int = 0 // property with a backing field -- serialized

    val path: String // getter only, no backing field -- not serialized
        get() = "kotlin/$name"

    var id by ::name // delegated property -- not serialized
}

class Case02 {
    @Test
    fun testProject(){
        println("start...")

        println("----------------")

        val data = Project2("kotlinx.serialization").apply {
            // stars = 1234
        }
        println(Json.encodeToString(data))

        val json  = """
                {"name":"kotlinx.serialization"}
        """.trimIndent()

//        println(Json.decodeFromString(json)) // error
        println(Json.decodeFromString<Project2>(json))

        println("----------------")

        println("end.")
    }
}