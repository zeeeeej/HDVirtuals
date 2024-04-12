package com.yunext.virtuals.ui.data

import androidx.compose.runtime.Stable
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslPropertyType
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class PropertyValueWrapper internal constructor(val real: PropertyValue<*>)

@Stable
data class StructPropertyValueWrapper internal constructor(val real: StructPropertyValue)

fun PropertyValue<*>.wrap() = PropertyValueWrapper(this)
fun StructPropertyValue.wrapStruct() = StructPropertyValueWrapper(this)


@Serializable
@Stable
data class PropertyData(
    val name: String = "",
    val key: String = "",
    val required: Boolean = false,
    val readWrite: ReadWrite = ReadWrite.R,
    val type: TslPropertyType,
    val innerType: List<TslPropertyType> = emptyList(),
    val desc: String,
    val value: PropertyValueWrapper,
) {
    @Serializable
    enum class ReadWrite {
        R, W, RW, UnKnow;
    }
}

val PropertyData.typeStr: String
    get() = when (type) {
        TslPropertyType.INT -> type.text
        TslPropertyType.FLOAT -> type.text
        TslPropertyType.DOUBLE -> type.text
        TslPropertyType.TEXT -> type.text
        TslPropertyType.DATE -> type.text
        TslPropertyType.BOOL -> type.text
        TslPropertyType.ENUM -> "${type.text}/${innerType.single().text}"
        TslPropertyType.STRUCT -> type.text
        TslPropertyType.ARRAY -> "${type.text}/${innerType.single().text}"
    }