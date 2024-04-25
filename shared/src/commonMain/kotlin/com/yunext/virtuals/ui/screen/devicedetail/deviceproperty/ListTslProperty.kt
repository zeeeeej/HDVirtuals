package com.yunext.virtuals.ui.screen.devicedetail.deviceproperty

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.BoolPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoubleArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.specDisplay
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_blue_light
import com.yunext.kmp.resource.color.app_brush_item_content_spec
import com.yunext.kmp.resource.color.app_orange
import com.yunext.kmp.resource.color.app_orange_light
import com.yunext.kmp.resource.color.app_red
import com.yunext.kmp.resource.color.app_red_light
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_666666
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.kmp.ui.compose.CHItemShadowShape
import com.yunext.kmp.ui.compose.hdBackgroundBrush
import com.yunext.virtuals.ui.data.PropertyData

import com.yunext.virtuals.ui.data.typeStr
import com.yunext.virtuals.ui.theme.ItemDefaults

// ----------------- properties ----------------- //

@Composable
internal fun ListTslProperty(list: List<PropertyData>, onClick: (PropertyData) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items = list, key = { _, it ->
            it.key
        }) { index, it ->
            PropertyItem(it) {
                onClick(list[index])
            }
        }
    }
}

@Composable
private fun PropertyItem(data: PropertyData, onClick: () -> Unit) {
    val desc = data.desc
    CHItemShadowShape {
        Column(
            modifier = Modifier.fillMaxWidth().clip(ItemDefaults.itemShape)
                .background(ItemDefaults.itemBackground).padding(16.dp)
        ) {
            // 头部基本信息
            HeaderPart(data = data) {
                onClick()
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 内容
            ContentPart(data)
            Spacer(modifier = Modifier.height(16.dp))
            // desc
            BottomPart(desc)
        }
    }
}

@Composable
private fun HeaderPart(data: PropertyData, onClick: () -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(modifier = Modifier.weight(1f, true)) {

            Row(modifier = Modifier) {
                Text(
                    text = data.name.run { this.ifEmpty { "未知" } },
                    fontSize = 16.sp,
                    color = app_textColor_333333,
                    fontWeight = FontWeight.Bold
                )
                Text(text = data.key.run {
                    if (this.isEmpty()) "-" else "(${this})"
                }, fontSize = 16.sp, color = app_textColor_999999, fontWeight = FontWeight.Bold)

            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier) {
                // required ： 比如必填
                val required = if (data.required) "必填" else "非必填"
                val (requiredFontColor, requiredBackgroundColor) = if (data.required) app_red to app_red_light else app_appColor to app_blue_light

                LabelPart(
                    required, requiredFontColor, requiredBackgroundColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                // readWrite : 比如只读
                val label = when (data.readWrite) {
                    PropertyData.ReadWrite.R -> "只读"
                    PropertyData.ReadWrite.W -> "只写"
                    PropertyData.ReadWrite.RW -> "读写"
                    PropertyData.ReadWrite.UnKnow -> "未知"
                }
                val (fontColor, backgroundColor) = when (data.readWrite) {
                    PropertyData.ReadWrite.R -> app_orange to app_orange_light
                    PropertyData.ReadWrite.W -> app_orange to app_orange_light
                    PropertyData.ReadWrite.RW -> app_appColor to app_blue_light
                    PropertyData.ReadWrite.UnKnow -> Color.White to app_red
                }
                LabelPart(label, fontColor, backgroundColor)
                Spacer(modifier = Modifier.width(8.dp))
                // type : 比如text
                val (labelFontColor, labelBackgroundColor) = app_appColor to app_blue_light
                LabelPart(data.typeStr, labelFontColor, labelBackgroundColor)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 修改
        Text(
            modifier = Modifier.clip(ItemDefaults.editShape)
                .border(width = 1.dp, color = app_appColor, shape = ItemDefaults.editShape)
                .clickable {
                    onClick()
                }.padding(horizontal = 8.dp, vertical = 6.dp),
            text = "修改",
            fontSize = 13.sp,
            color = app_appColor
        )
    }
}

@Composable
private fun ContentPart(data: PropertyData) {
    val spec = data.value.value.key.specDisplay
    Column(
        modifier = ItemDefaults.borderModifier
    ) {
        ContentPartCurrentValue(data)
        ContentPartSpec(spec)
    }
}

/**
 * 当前值
 */
@Composable
private fun ContentPartCurrentValue(data: PropertyData) {
    when (val value = data.value.value) {
        is DoubleArrayPropertyValue -> {
            ContentPartValueArrayForOther(value)
        }

        is FloatArrayPropertyValue -> {
            ContentPartValueArrayForOther(value)
        }

        is IntArrayPropertyValue -> {
            ContentPartValueArrayForOther(value)
        }

        is StructArrayPropertyValue -> {
            ContentPartValueArrayForStruct(value) {
                   // 查看
            }
        }

        is TextArrayPropertyValue -> {
            ContentPartValueArrayForOther(value)
        }

        is BoolPropertyValue -> {
            ContentPartValueNormal(value.displayValue)
        }

        is DatePropertyValue -> {
            ContentPartValueNormal(value.displayValue)
        }

        is DoublePropertyValue -> {
            ContentPartValueNormal(value.displayValue)
        }

        is FloatPropertyValue -> {
            ContentPartValueNormal(value.displayValue)
        }

        is IntEnumPropertyValue -> {
            ContentPartValueNormal(value.displayValue)
        }

        is IntPropertyValue -> {
            ContentPartValueNormal(value.displayValue)
        }

        is StructPropertyValue -> {
            ContentPartValueStruct(data.value)
        }

        is TextEnumPropertyValue -> {
            ContentPartValueNormal(value.displayValue)
        }

        is TextPropertyValue -> {
            ContentPartValueNormal(value.displayValue)
        }
    }
}

@Composable
internal fun ContentPartCurrentValue(value: @Composable () -> Unit) {
    Column(
        modifier = Modifier.clip(ItemDefaults.contentValueShape)
            .background(ItemDefaults.itemBackground).padding(16.dp)
    ) {
        Text(text = "当前值", fontSize = 13.sp, color = app_textColor_666666)
        Spacer(modifier = Modifier.height(16.dp))
        value()

    }
}


/**
 * 约束
 */
@Composable
private fun ContentPartSpec(value: String) {

    Column(
        modifier = Modifier.fillMaxWidth().clip(ItemDefaults.contentSpecShape)
            .hdBackgroundBrush { app_brush_item_content_spec }.padding(16.dp)
    ) {
        Text(text = "约束", fontSize = 11.sp, color = app_textColor_666666)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = app_textColor_333333,
            modifier = Modifier.heightIn(12.dp, 200.dp).verticalScroll(
                rememberScrollState()
            )
        )
    }
}




