package com.yunext.virtuals.ui.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.yunext.kmp.common.util.hdUUID
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_blue_light
import com.yunext.kmp.resource.color.app_orange
import com.yunext.kmp.resource.color.app_orange_light
import com.yunext.kmp.resource.color.app_red
import com.yunext.kmp.resource.color.app_red_light
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.random.Random

@Serializable
@Stable
data class EventData(
    val name: String,
    val key: String,
    val required: Boolean,
    val eventType: EventType,
    val output: List<JsonElement>,
    val desc: String,
) {

    enum class EventType(val text: String, val color: Pair<Color, Color>) {
        Alert("alert", app_orange to app_orange_light),
        Info("info", app_appColor to app_blue_light),
        Fault("fault", app_red to app_red_light)
        ;
    }

    companion object {
        internal fun random() = EventData(
            name = hdUUID(4),
            key = randomText(),
            required = Random.nextBoolean(),
            eventType = EventType.values().random(),
            output = List(Random.nextInt(4)) { JsonPrimitive(it) },
            desc = randomText(),
        )
    }
}

internal fun randomText(length: Int = 4) = hdUUID(length)