package com.yunext.virtuals.ui.screen.devicedetail.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.BoolPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.nameAndKey
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_gray_f4f5f7
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.common.StableValue
import com.yunext.virtuals.ui.screen.devicedetail.TslEditor

// <editor-fold desc="[修改enum]">
// enum a
/**
 * enum int、bool
 */
@Composable
internal fun EnumIntPropertyBottomSheet(
    wrapper: StableValue<PropertyValue<*>>,
    edit:Boolean = true,
    onClose: () -> Unit,
    onSelected: (PropertyValue<*>) -> Unit,
) {
    Debug {
        "EnumIntPropertyBottomSheet property:$wrapper"
    }
    val property = wrapper.value
    val title = if (edit) "修改" else "添加"
    val key = property.key
    val msg = key.nameAndKey
    // val spec = key.specDisplay
    var selectedIndex: Int by remember {
        mutableStateOf(-1)
    }
    var list: List<Pair<Int?, String>> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(Unit) {
        val specs: Pair<List<Pair<Int?, String>>, Int> = when (property) {
            is BoolPropertyValue -> {
                val tempList = property.key.specs.map {
                    it.value to it.desc
                }
                val temIndex = tempList.indexOfFirst {
                    it.first == property.value
                }
                tempList to temIndex
            }

            is IntEnumPropertyValue -> {
                val tempList = property.key.specs.map {
                    it.value to it.desc
                }
                val temIndex = tempList.indexOfFirst {
                    it.first == property.keyValue.value
                }
                tempList to temIndex
            }

            else -> emptyList<Pair<Int?, String>>() to -1
        }
        list = specs.first
        selectedIndex = specs.second
    }


    TslEditor(title, msg,enable = true, onClose, onCommit = {

        if (selectedIndex == -1) return@TslEditor
        val (selValue, newDesc) = if (selectedIndex == -1) throw IllegalStateException("selectedIndex==-1") else list[selectedIndex]
        val newProperty = when (property) {
            is BoolPropertyValue -> {
                BoolPropertyValue(property.key, selValue)
            }

            is IntEnumPropertyValue -> {
                val intEnumPropertyValue = IntEnumPropertyValue.KeyValue(selValue, newDesc)
                IntEnumPropertyValue(property.key, intEnumPropertyValue)
            }

            else -> throw IllegalStateException("EnumIntPropertyBottomSheet 不支持的属性类型:$property")
        }
        onSelected.invoke(newProperty)
    }) {
        EnumIntListPart(list, selectedIndex) {
            selectedIndex = it
        }
    }
}

/**
 * enum int 选择列表
 */
@Composable
internal fun EnumIntListPart(
    list: List<Pair<Int?, String>>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(list, { _, item -> item.toString() }) { index, (value, desc) ->
            EnumItem(index, "$value=$desc", selectedIndex == index) {
                onSelected.invoke(index)
            }
        }
    }
}

/**
 * enum text
 */
@Composable
internal fun EnumTextPropertyBottomSheet(
    property: PropertyValue<*>,
    onClose: () -> Unit,
    onSelected: (PropertyValue<*>) -> Unit,
) {
    val title = "修改"
    val key = property.key
    val msg = key.nameAndKey
    // val spec = key.specDisplay
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


    TslEditor(title, msg, enable = true, onClose, onCommit = {

        if (selectedIndex == -1) return@TslEditor
        val (selValue, newDesc) = if (selectedIndex == -1) throw IllegalStateException("selectedIndex==-1") else list[selectedIndex]
        val newProperty = when (property) {


            is TextEnumPropertyValue -> {
                val intEnumPropertyValue = TextEnumPropertyValue.KeyValue(selValue, newDesc)
                TextEnumPropertyValue(property.key, intEnumPropertyValue)
            }

            else -> throw IllegalStateException("EnumTextPropertyBottomSheet 不支持的属性类型:$property")
        }
        onSelected.invoke(newProperty)
    }) {
        EnumTextListPart(list, selectedIndex) {
            selectedIndex = it
        }
    }
}

/**
 * enum text 选择列表
 */
@Composable
internal fun EnumTextListPart(
    list: List<Pair<String, String>>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(list, { _, item -> item.toString() }) { index, (value, desc) ->
            EnumItem(index, "$value=$desc", selectedIndex == index) {
                onSelected.invoke(index)
            }
        }
    }
}

private val enumShape = RoundedCornerShape(22.dp)

// enum int text 、bool公共item格式
@Composable
private fun EnumItem(index: Int, desc: String, selected: Boolean, onSelected: () -> Unit) {
    val textColor = if (selected) app_appColor else app_textColor_333333
    fun Modifier.selected(): Modifier {
        return if (selected) {
            this
                .clip(enumShape)
                .border(1.dp, textColor, enumShape)
                .clickable {
                    onSelected()
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        } else {
            this
                .clip(enumShape)
                .background(app_gray_f4f5f7)
                .clickable {
                    onSelected()
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        }
    }

    Text(
        text = "$index - $desc",
        fontSize = 14.sp,
        color = textColor,
        modifier = Modifier.selected()
    )
}
// enum z
// </editor-fold>