package com.yunext.virtuals.ui.data

import androidx.compose.runtime.Stable
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.random.Random

@Serializable
@Stable
 data class ServiceData(
    val name: String,
    val key: String,
    val required: Boolean,
    val async: Boolean,
    val input: List<PropertyKey>,
    val output: List<PropertyKey>,
    val desc: String,
) {
    companion object {
        internal fun random() = ServiceData(
            name = randomText(),
            key = randomText(),
            required = Random.nextBoolean(),
            async = Random.nextBoolean(),
            input = emptyList(),//List(Random.nextInt(4)) { JsonPrimitive(it) },
            output = emptyList(),//List(Random.nextInt(4)) { JsonPrimitive(it)  },
            desc = randomText(),
        )
    }
}