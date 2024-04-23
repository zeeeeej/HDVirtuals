package com.yunext.virtuals.ui.screen.devicedetail.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.BoolPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoubleArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.nameAndKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.specDisplay
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.unitStr
import com.yunext.kmp.resource.color.app_gray
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.virtuals.ui.common.DividerBlock
import com.yunext.virtuals.ui.common.EditTextBlock
import com.yunext.virtuals.ui.data.StructPropertyValueWrapper
import com.yunext.virtuals.ui.data.PropertyValueWrapper
import com.yunext.virtuals.ui.screen.devicedetail.TslEditor
import com.yunext.virtuals.ui.theme.Twins

// <editor-fold desc="[修改struct]">
// struct a

@Composable
internal fun StructPropertyBottomSheet(
    wrapper: StructPropertyValueWrapper,
    edit:Boolean = true,
    onClose: () -> Unit,
    onCommitted: (StructPropertyValue) -> Unit,
) {
    val property = wrapper.value
    val title = if (edit) "修改" else "添加"
    val key = property.key
    val spec = key.specDisplay
    val msg = key.nameAndKey
    // 属性值
    var list: List<PropertyValueWrapper> by remember {
        mutableStateOf(emptyList())
    }
    LaunchedEffect(wrapper) {
        // 初始化list
        list = property.itemValues.map { (_, v) ->
            PropertyValueWrapper(v)
        }.sortDefault()
    }

    TslEditor(title, msg, enable = true, onClose, onCommit = {
        val newValue = StructPropertyValue(property.key, list.associate { item ->
            item.value.key to item.value
        })
        onCommitted.invoke(newValue)
    }) {

        LazyColumn(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .heightIn(max = 200.dp), verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(
                items = list,
                key = { _, item -> item.value.key.identifier }) { index, item ->
                EditStructItem(item) { edit ->
                    list = (list - list[index] + edit).sortDefault()
                }
            }
        }
        DividerBlock(color = app_gray)
        Text(
            text = spec,
            color = app_textColor_999999,
            fontSize = 13.sp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun EditStructItem(
    value: PropertyValueWrapper,
    onValueChanged: (PropertyValueWrapper) -> Unit,
) {
    val key = value.value.key.nameAndKey
    when (val propertyValue = value.value) {
        is DoubleArrayPropertyValue -> Struct_Not_Support_Array_Exception
        is FloatArrayPropertyValue -> Struct_Not_Support_Array_Exception
        is IntArrayPropertyValue -> Struct_Not_Support_Array_Exception
        is StructArrayPropertyValue -> Struct_Not_Support_Array_Exception
        is TextArrayPropertyValue -> Struct_Not_Support_Array_Exception
        is BoolPropertyValue -> {
            var v: BoolPropertyValue? by remember {
                mutableStateOf(null)
            }
            LaunchedEffect(Unit) {
                v = propertyValue
            }
            StructItemForBoolean(key, property = propertyValue) {
                onValueChanged.invoke(PropertyValueWrapper(it))
            }
        }

        is DatePropertyValue -> {
            var dateValue: Long? by remember {
                mutableStateOf(null)
            }
            LaunchedEffect(Unit) {
                dateValue = propertyValue.value?.toLong()
            }
            StructItemForNumber(
                propertyValue.key,
                key,
                desc = propertyValue.key.nameAndKey,
                dateValue,
                "ms"
            ) {
                dateValue = it
                onValueChanged.invoke(
                    PropertyValueWrapper(
                        DatePropertyValue(
                            propertyValue.key,
                            it.toString()
                        )
                    )
                )
            }
        }

        is DoublePropertyValue -> {
            var doubleValue: Double? by remember {
                mutableStateOf(null)
            }
            LaunchedEffect(Unit) {
                doubleValue = propertyValue.value
            }
            val unit = value.value.key.unitStr
            StructItemForNumber(
                propertyValue.key,
                key,
                desc = propertyValue.key.nameAndKey,
                doubleValue,
                unit
            ) {
                doubleValue = it
                onValueChanged.invoke(
                    PropertyValueWrapper(
                        DoublePropertyValue(
                            propertyValue.key,
                            it
                        )
                    )
                )
            }
        }

        is FloatPropertyValue -> {
            var floatValue: Float? by remember {
                mutableStateOf(null)
            }
            LaunchedEffect(Unit) {
                floatValue = propertyValue.value
            }
            val unit = value.value.key.unitStr
            StructItemForNumber(
                propertyValue.key,
                key,
                desc = propertyValue.key.nameAndKey,
                floatValue,
                unit
            ) {
                floatValue = it
                onValueChanged.invoke(
                    PropertyValueWrapper(
                        FloatPropertyValue(
                            propertyValue.key,
                            it
                        )
                    )
                )
            }
        }

        is IntEnumPropertyValue -> {
            StructItemForEnumInt(key, property = propertyValue) {
                onValueChanged.invoke(PropertyValueWrapper(it))
            }
        }

        is IntPropertyValue -> {
            var intValue: Int? by remember {
                mutableStateOf(null)
            }
            LaunchedEffect(Unit) {
                intValue = propertyValue.value
            }
            val unit = propertyValue.key.unitStr
            StructItemForNumber(
                propertyValue.key,
                key,
                desc = propertyValue.key.desc,
                intValue,
                unit
            ) {
                intValue = it

                onValueChanged.invoke(PropertyValueWrapper(IntPropertyValue(propertyValue.key, it)))
            }
        }

        is StructPropertyValue -> Struct_Not_Support_Struct_Exception
        is TextEnumPropertyValue -> {
            StructItemForEnumText(key, property = propertyValue) {
                onValueChanged.invoke(PropertyValueWrapper(it))
            }
        }

        is TextPropertyValue -> {
            var textValue by remember {
                mutableStateOf("")
            }
            LaunchedEffect(Unit) {
                textValue = propertyValue.value ?: ""
            }
            StructItemForText(key, textValue) {
                textValue = it
                onValueChanged.invoke(
                    PropertyValueWrapper(
                        TextPropertyValue(
                            propertyValue.key,
                            it
                        )
                    )
                )
            }
        }
    }

}

internal fun Number.isZero() = when (this) {
    is Float -> (this == 0f)
    is Double -> (this == 0.0)
    is Int -> (this == 0)
    is Long -> (this == 0L)
    is Byte -> (this == 0.toByte())
    is Short -> (this == 0.toShort())
    else -> false
}

@Composable
private fun <T : Number?> StructItemForNumber(
    type: PropertyKey,
    key: String,
    desc: String,
    value: T,
    unit: String,
    onValueChanged: (T) -> Unit,
) {
    val keyboardType = when (type) {
        is DatePropertyKey -> KeyboardType.Number
        is FloatPropertyKey -> KeyboardType.Decimal
        is DoublePropertyKey -> KeyboardType.Decimal
        is IntPropertyKey -> KeyboardType.Number

        else -> throw Throwable("错误的value $value")
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(key, style = Twins.twins_title)
        Spacer(modifier = Modifier.width(8.dp))
        EditTextBlock(
            modifier = Modifier
                .weight(1f),
//                .background(China.g_zhu_lv),
            text = if (value == null || value.isZero()) "" else "$value",
            hint = desc.ifEmpty { "请输入" },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType)
        ) { text ->
            try {
                println("-->text:$text")
                @Suppress("UNCHECKED_CAST") val result = when (type) {
                    is DatePropertyKey -> text.toLong()
                    is FloatPropertyKey -> text.toFloat()
                    is DoublePropertyKey -> text.toDouble()
                    is IntPropertyKey -> text.toInt()
                    else -> throw Throwable("错误的value $value")
                } as T
                onValueChanged.invoke(result)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = unit, style = Twins.twins_title.copy(color = app_textColor_999999))
    }
}

@Composable
private fun StructItemForText(key: String, value: String, onValueChanged: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(key, style = Twins.twins_title)
        Spacer(modifier = Modifier.width(8.dp))
        EditTextBlock(
            modifier = Modifier
                .weight(1f),
//                .background(China.g_zhu_lv),
            text = value,
            hint = "请输入",
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        ) { text ->
            onValueChanged.invoke(text)
        }
    }
}


@Composable
private fun StructItemForEnumText(
    key: String,
    property: TextEnumPropertyValue,
    onValueChanged: (TextEnumPropertyValue) -> Unit,
) {
    var selectedIndex: Int by remember {
        mutableStateOf(-1)
    }
    var list: List<Pair<String, String>> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(Unit) {
        val specs = property.generateEnumTextList()
        list = specs.first
        selectedIndex = specs.second
    }
    Column(
        Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(key, style = Twins.twins_title)
        Spacer(modifier = Modifier.height(12.dp))
        EnumTextListPart(list, selectedIndex) {
            selectedIndex = it
            val (value, desc) = list[selectedIndex]
            val newValue =
                TextEnumPropertyValue(property.key, TextEnumPropertyValue.KeyValue(value, desc))
            onValueChanged.invoke(newValue)
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun StructItemForEnumInt(
    key: String,
    property: IntEnumPropertyValue,
    onValueChanged: (IntEnumPropertyValue) -> Unit,
) {
    var selectedIndex: Int by remember {
        mutableStateOf(-1)
    }
    var list: List<Pair<Int?, String>> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(Unit) {
        val specs = property.generateEnumIntList()
        list = specs.first
        selectedIndex = specs.second
    }
    Column(
        Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(key, style = Twins.twins_title)
        Spacer(modifier = Modifier.height(12.dp))
        EnumIntListPart(list, selectedIndex) {
            selectedIndex = it
            val (value, desc) = list[selectedIndex]
            val newValue =
                IntEnumPropertyValue(property.key, IntEnumPropertyValue.KeyValue(value, desc))
            onValueChanged.invoke(newValue)
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun StructItemForBoolean(
    key: String,
    property: BoolPropertyValue,
    onValueChanged: (BoolPropertyValue) -> Unit,
) {
    var selectedIndex: Int by remember {
        mutableStateOf(-1)
    }
    var list: List<Pair<Int?, String>> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(Unit) {
        val (newList, newIndex) = property.generateEnumIntList()
        list = newList
        selectedIndex = newIndex
    }
    Column(
        Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(key, style = Twins.twins_title)
        Spacer(modifier = Modifier.height(12.dp))
        EnumIntListPart(list, selectedIndex) {
            selectedIndex = it
            val (value, _) = list[selectedIndex]
            val newValue = BoolPropertyValue(property.key, value)
            onValueChanged.invoke(newValue)
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}


// struct z
// </editor-fold>