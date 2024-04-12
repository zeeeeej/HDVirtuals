package serializationdemo.basicserialitation

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

// https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md

@Serializable
private class Project(val name: String, val language: String)

class Case01 {
    @Test
    fun testProject(){
        println("start...")

        println("----------------")

        val data = Project("kotlinx.serialization", "Kotlin")
        println(Json.encodeToString(data))

        val json  = """
                {"name":"kotlinx.serialization","language":"Kotlin"}
        """.trimIndent()

//        println(Json.decodeFromString(json)) // error
        println(Json.decodeFromString<Project>(json))

        println("----------------")

        println("end.")
    }
}