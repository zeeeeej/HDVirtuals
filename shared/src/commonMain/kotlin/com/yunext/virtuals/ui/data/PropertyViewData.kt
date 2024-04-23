package com.yunext.virtuals.ui.data

import androidx.compose.runtime.Stable
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslPropertyType
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import com.yunext.virtuals.ui.common.StableValue
import com.yunext.virtuals.ui.common.stable
import kotlinx.serialization.Serializable

//@Serializable
//@Stable
//data class PropertyValueWrapper internal constructor(val real: PropertyValue<*>)
@Deprecated("StableValue<T>替代",ReplaceWith("StableValue<PropertyValue<*>>"))

typealias PropertyValueWrapper = StableValue<PropertyValue<*>>
@Deprecated("StableValue<T>替代",ReplaceWith("StableValue<StructPropertyValue>"))
typealias StructPropertyValueWrapper = StableValue<StructPropertyValue>
//@Stable
//data class StructPropertyValueWrapper internal constructor(val real: StructPropertyValue)

@Deprecated("StableValue<T>替代",ReplaceWith("this.stable()"))
fun PropertyValue<*>.wrap() = this.stable()//PropertyValueWrapper(this)
@Deprecated("StableValue<T>替代",ReplaceWith("this.stable()"))
fun StructPropertyValue.wrapStruct() = this.stable()//StructPropertyValueWrapper(this)


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