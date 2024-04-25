package com.yunext.virtuals.ui.screen.devicedetail.deviceproperty

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.ArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.asPropertyValueList
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.valueStr
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_blue_light
import com.yunext.kmp.resource.color.app_gray_light
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.theme.ItemDefaults
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@Composable
internal fun ContentPartValueArrayForStruct(
    data: ArrayPropertyValue,
    onLook: (List<PropertyValue<*>>) -> Unit = {},
) {
    ContentPartValueArray(data, onLook)
}

@Composable
internal fun ContentPartValueArrayForOther(
    data: ArrayPropertyValue,
) {
    ContentPartValueArray(data) {
        // ignore
    }
}


@Composable
private fun ContentPartValueArray(
    data: ArrayPropertyValue,
    onLook: (List<PropertyValue<*>>) -> Unit,
) {
    Debug {
         "ContentPartValueArray => LaunchedEffect $data"
    }

    ContentPartCurrentValue {
        Box(modifier = ItemDefaults.borderModifier) {
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
            LaunchedEffect(data) {
                Napier.w { "ContentPartValueArray => LaunchedEffect old size = ${itemList.size}" }
                itemList =
                    withContext(Dispatchers.IO) {
                        data.asPropertyValueList
                    }
                Napier.w { "ContentPartValueArray => LaunchedEffect size = ${itemList.size}" }
            }
            if (list.isEmpty()) {
                Text("空")
            } else {
                ArrayItemList(list, false/*data is StructArrayPropertyValue*/) { index ->
                    // 查看
                    if (data is StructArrayPropertyValue) {
                        val valueList = data.value
                        val propertyValue = valueList[index]
                        onLook.invoke(propertyValue.values.toList())
                    }
                }
            }
        }
    }
}

/**
 * 比较差异[com.yunext.virtuals.ui.screen.devicedetail.bottomsheet.ArrayItemList]
 */
@Composable
private fun ArrayItemList(
    list: List<Pair<String, String>>,
    look: Boolean = false,
    onLook: (Int) -> Unit,
) {
    LazyColumn(modifier = Modifier.heightIn(max = ItemDefaults.contentValueMaxHeight)) {
        itemsIndexed(list, key = { _, (id, valueStr) ->
            "$id->$valueStr"
        }) { index, (id, valueStr) ->

            ArrayItem(index.toString(), valueStr, look) {
                if (look) {
                    onLook.invoke(index)
                }
            }
            if (index < list.size) {
                Spacer(
                    modifier = Modifier.fillMaxWidth().height(.5.dp)
                        .background(ItemDefaults.contentBorderColor)
                )
            }
        }

    }
}

@Composable
private fun ArrayItem(index: String, data: String, look: Boolean, onLook: () -> Unit) {
    val type = ItemDefaults.valueTypes.random()
    Row(Modifier.fillMaxWidth().height(35.dp).run {
        if (look) {
            this.clickable {
                onLook()
            }
        } else this
    }.padding(start = 17.dp, end = 26.dp), verticalAlignment = Alignment.CenterVertically) {
        IndexText(index)
        Spacer(modifier = Modifier.width(55.dp))
        if (look) {
            Text(
                text = "查看",
                fontSize = 16.sp,
                color = app_appColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier

                    .fillMaxHeight().wrapContentHeight()

                    .weight(1f),
            )
        } else {
            Text(
                text = data,
                fontSize = 16.sp,
                color = app_textColor_333333,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        LabelPart(type, app_appColor, app_blue_light)
    }
}

@Composable
private fun IndexText(index: String) {
    Text(
        text = index,
        fontSize = 11.sp,
        color = app_textColor_999999,
        modifier = Modifier.size(14.dp).clip(CircleShape)
            .border(.5.dp, color = app_gray_light, shape = CircleShape).wrapContentHeight()
            .wrapContentWidth()
    )
}