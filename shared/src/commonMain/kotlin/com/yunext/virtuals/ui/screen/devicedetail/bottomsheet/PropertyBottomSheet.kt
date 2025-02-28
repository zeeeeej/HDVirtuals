package com.yunext.virtuals.ui.screen.devicedetail.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslPropertyType
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.ArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.BoolPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextPropertyValue
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.common.dialog.XPopContainer
import com.yunext.virtuals.ui.common.stable
import com.yunext.virtuals.ui.data.PropertyData
import com.yunext.virtuals.ui.data.PropertyValueWrapper
import com.yunext.virtuals.ui.data.wrap
import com.yunext.virtuals.ui.data.wrapStruct

@Composable
fun PropertyBottomSheet(
    source: PropertyData,
    onClose: () -> Unit,
    onCommitted: (PropertyData) -> Unit,
) {
    Debug {
        "PropertyBottomSheet source:$source"
    }
    val propertyValue by remember {
        mutableStateOf(source.value.value)
    }

    val onCloseWrapper by rememberUpdatedState(onClose)
    val onCommittedWrapper by rememberUpdatedState(onCommitted)

    XPopContainer(Alignment.BottomCenter) {

        when (val temp = propertyValue) {
            is ArrayPropertyValue,
            -> {
                ArrayPropertyBottomSheet(
                    temp.stable(),
                    onClose = onCloseWrapper,
                    onCommitted = { value ->
                        val dest = source.copy(value = value.stable())
                        onCommittedWrapper.invoke(dest)
                    },
                )
            }

            is BoolPropertyValue -> EnumIntPropertyBottomSheet(
                propertyValue.stable(),
                onClose = onClose,
                onSelected = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })

            is DatePropertyValue -> NumberPropertyBottomSheet(
                propertyValue.stable(),
                onClose = onClose,
                onCommitted = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })

            is DoublePropertyValue -> NumberPropertyBottomSheet(
                propertyValue.stable(),
                onClose = onClose,
                onCommitted = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })

            is FloatPropertyValue -> NumberPropertyBottomSheet(
                propertyValue.stable(),
                onClose = onClose,
                onCommitted = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })

            is IntEnumPropertyValue -> EnumIntPropertyBottomSheet(
                propertyValue.stable(),
                onClose = onClose,
                onSelected = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })

            is IntPropertyValue -> NumberPropertyBottomSheet(
                propertyValue.stable(),
                onClose = onClose,
                onCommitted = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })

            is StructPropertyValue -> StructPropertyBottomSheet(
                temp.stable(),
                onClose = onClose,
                onCommitted = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })

            is TextEnumPropertyValue -> EnumTextPropertyBottomSheet(
                propertyValue,
                onClose = onClose,
                onSelected = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })

            is TextPropertyValue -> TextPropertyBottomSheet(
                propertyValue,
                onClose = onClose,
                onCommitted = { value ->
                    val dest = source.copy(value = value.wrap())
                    onCommitted.invoke(dest)
                })
        }


    }
}

//<editor-fold desc="common">
/** common a **/

internal val WrapperPropertyValue_Sort = Comparator<PropertyValueWrapper> { p1, p2 ->
    p1.value.key.identifier.compareTo(p2.value.key.identifier)
}

internal fun List<PropertyValueWrapper>.sortDefault(): List<PropertyValueWrapper> {
    return this.sortedWith(WrapperPropertyValue_Sort)
}

internal val Struct_Not_Support_Struct_Exception =
    IllegalStateException("struct not support struct value .")
internal val Struct_Not_Support_Array_Exception =
    IllegalStateException("struct not support array value .")

internal typealias EnumTextItem = Pair<
        /* value : desc */
        List<Pair<String, String>>,
        /* selected index */
        Int>

internal typealias EnumIntItem = Pair<
        /* value : desc */
        List<Pair<Int?, String>>,
        /* selected index */
        Int>


internal fun PropertyValue<*>.generateEnumTextList(): EnumTextItem {
    val specs: Pair<List<Pair<String, String>>, Int> = when (val property = this) {
        is TextEnumPropertyValue -> {
            val tempList = property.key.specs.map {
                it.value to it.desc
            }
            val temIndex = tempList.indexOfFirst { (value, _/*desc*/) ->
                value == property.keyValue.value
            }
            tempList to temIndex
        }

        else -> emptyList<Pair<String, String>>() to -1
    }
    return specs
}

internal fun PropertyValue<*>.generateEnumIntList(): EnumIntItem {
    val specs: Pair<List<Pair<Int?, String>>, Int> = when (val property = this) {
        is BoolPropertyValue -> {
            val tempList = property.key.specs.map {
                it.value to it.desc
            }
            val temIndex = tempList.indexOfFirst { (value, _/*desc*/) ->
                value == property.value
            }
            tempList to temIndex
        }

        is IntEnumPropertyValue -> {
            val tempList = property.key.specs.map {
                it.value to it.desc
            }
            val temIndex = tempList.indexOfFirst { (value, _/*desc*/) ->
                value == property.keyValue.value
            }
            tempList to temIndex
        }

        else -> emptyList<Pair<Int?, String>>() to -1
    }
    return specs
}

/** common z **/
//</editor-fold>