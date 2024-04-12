package com.yunext.virtuals.ui.screen.devicedetail.bottomsheet

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.ArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoubleArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.asPropertyValueList
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.nameAndKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.specDisplay
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.valueStr
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_gray
import com.yunext.kmp.resource.color.app_red
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.kmp.ui.compose.CHPressedView
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.common.DividerBlock
import com.yunext.virtuals.ui.data.wrapStruct
import com.yunext.virtuals.ui.screen.devicedetail.TslEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// <editor-fold desc="[修改array]">
// 修改array a
@Composable
internal fun ArrayPropertyBottomSheet(
    property: ArrayPropertyValue,
    edit: Boolean = true,
    onClose: () -> Unit,
    onCommitted: (ArrayPropertyValue) -> Unit,
) {
    var loading by remember { mutableStateOf(false) }

    val title by remember {
        derivedStateOf {
            val title = if (edit) "修改" else "添加"
            val tail = if (loading) "(loading...)" else ""
            title + tail
        }
    }
    val key = property.key
    val spec = key.specDisplay
    val msg = key.nameAndKey

    var isAdding: Boolean by remember {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    /* 当前item集合 */
    var itemList: List<PropertyValue<*>> by remember {
        mutableStateOf(emptyList())
    }
    /* 当前item集合 映射的显示内容 */
    val list: List<Pair<String, String>> by remember(itemList) {
        derivedStateOf {
            itemList.map {
                it.key.identifier to it.valueStr
            }
        }
    }

    // 第一次解析
    LaunchedEffect(Unit) {
        loading = true
        itemList =
            withContext(Dispatchers.IO) {
                delay(1000) // for test
                property.asPropertyValueList
            }
        loading = false

    }

    // 弹窗
    TslEditor(title, msg, enable = !loading, onClose, onCommit = {
        coroutineScope.launch {
            loading = true
            val newPropertyValue = withContext(Dispatchers.IO) {
                val tempList = itemList
                val tempKey = property.key
                ArrayPropertyValue.createValue(tempKey, tempList)
            }
            loading = false
            onCommitted.invoke(newPropertyValue)
        }


    }) {
        ArrayItemList(list) { index ->
            // 删除
            itemList = itemList - itemList[index]
        }

        Spacer(modifier = Modifier.heightIn(12.dp))
        CHPressedView(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), content = { isPressed ->
            val alpha = if (isPressed) .5f else 1f
            Text(
                text = "添加",
                color = app_appColor.copy(alpha = alpha),
                fontSize = 13.sp,
                modifier = Modifier


//                    .wrapContentWidth()
//                    .wrapContentHeight()
                    .border(
                        1.dp, app_appColor.copy(alpha = alpha), RoundedCornerShape(4.dp)
                    )
                    .fillMaxWidth()
                    .padding(vertical = 11.dp),
                textAlign = TextAlign.Center
            )
        }, onClick = {
            // 添加
            isAdding = true
        }, enable = !loading
        )

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

    if (isAdding) {
        when (property) {
            is DoubleArrayPropertyValue -> {
                val empty = DoublePropertyValue.createValue(null)
                NumberPropertyBottomSheet(empty, edit = false, onClose = {
                    isAdding = false
                }, onCommitted = {
                    itemList = itemList + it
                    isAdding = false
                })
            }

            is FloatArrayPropertyValue -> {
                val empty = FloatPropertyValue.createValue(null)
                NumberPropertyBottomSheet(empty, edit = false, onClose = {
                    isAdding = false
                }, onCommitted = {
                    itemList = itemList + it
                    isAdding = false
                })
            }

            is IntArrayPropertyValue -> {
                val empty = IntPropertyValue.createValue(null)
                NumberPropertyBottomSheet(empty, edit = false, onClose = {
                    isAdding = false
                }, onCommitted = {
                    itemList = itemList + it
                    isAdding = false
                })
            }

            is StructArrayPropertyValue -> {
                val empty = StructPropertyValue.createFakePropertyKeyForAdd(property)
                StructPropertyBottomSheet(empty.wrapStruct(), edit = false, onClose = {
                    isAdding = false
                }, onCommitted = {
                    itemList = itemList + it
                    isAdding = false
                })
            }

            is TextArrayPropertyValue -> {
                val empty = TextPropertyValue.createValue(null)
                TextPropertyBottomSheet(empty, edit = false, onClose = {
                    isAdding = false
                }, onCommitted = {
                    itemList = itemList + it
                    isAdding = false
                })
            }
        }

    }
}

/**
 * 比较差异：[com.yunext.virtuals.ui.screen.devicedetail.property.ArrayItemList]
 */
@Composable
private fun ArrayItemList(list: List<Pair<String, String>>, onDelete: (Int) -> Unit) {
    Debug {
        "ArrayItemList = ${
            list.joinToString("\n") { (identifier, valueStr) ->
                "$identifier - $valueStr"
            }
        }"
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp)
    ) {
        itemsIndexed(
            items = list,
            key = { _, (identifier, valueStr) -> "$identifier->$valueStr" }) { index, (identifier, valueStr) ->
            ArrayItem(index, valueStr) {
                onDelete.invoke(index)
            }
        }
    }
}

@Composable
private fun ArrayItem(index: Int, valueStr: String, onDelete: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            color = app_textColor_333333,
            fontSize = 14.sp,
            modifier = Modifier

        )
        Spacer(modifier = Modifier.width(32.dp))


        Text(
            text = valueStr,
            color = app_textColor_333333,
            fontSize = 14.sp,
            modifier = Modifier

                .weight(1f)
        )
        Spacer(modifier = Modifier.width(32.dp))
        CHPressedView(modifier = Modifier, content = { isPressed ->
            val alpha = if (isPressed) .5f else 1f
            Text(
                text = "删除",
                color = app_red.copy(alpha = alpha),
                fontSize = 13.sp,
                modifier = Modifier


//                    .wrapContentWidth()
//                    .wrapContentHeight()
                    .border(
                        1.dp, app_red.copy(alpha = alpha), RoundedCornerShape(4.dp)
                    )
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
        }, onClick = {
            onDelete()
        })

    }
}
// 修改array z
// </editor-fold>