package com.yunext.virtuals.ui.screen.devicedetail.deviceproperty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.valueStr
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_blue_light
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.virtuals.ui.data.PropertyValueWrapper
import com.yunext.virtuals.ui.theme.ItemDefaults

@Composable
internal fun StructItemList(list: List<PropertyValueWrapper>) {
    LazyColumn(
        modifier = Modifier
            .heightIn(max = ItemDefaults.contentValueMaxHeight)
    ) {
        itemsIndexed(list, key = { _, data ->
            data.value.key.identifier
        }) { index, data ->
            StructItem(data, showLine = index < list.size)
        }

    }
}

/**
 * 参考[StructItemOnlyKey]
 */
@Composable
private fun StructItem(data: PropertyValueWrapper, showLine: Boolean) {
    val type by remember(data) {
        mutableStateOf(data.value.key.type.text)
    }

    val name by remember(data) {
        mutableStateOf(data.value.key.name)
    }

    val key by remember(data) {
        mutableStateOf(data.value.key.identifier)
    }

    val value by remember(data) {
        mutableStateOf(data.value.valueStr)
    }
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 17.dp, end = 26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    color = app_textColor_333333,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = key,
                    fontSize = 11.sp,
                    color = app_textColor_999999,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.width(55.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                textAlign = TextAlign.Right,
                color = app_textColor_333333,
                fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            LabelPart(type, app_appColor, app_blue_light, TextAlign.Right)


        }
        if (showLine) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(.5.dp)
                    .background(ItemDefaults.contentBorderColor)
            )
        }
    }

}

@Composable
internal fun LabelPart(
    label: String,
    fontColor: Color,
    background: Color,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = label,
        fontSize = 11.sp,
        color = fontColor,
        modifier = Modifier
            .background(color = background, shape = ItemDefaults.labelShape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .widthIn(min = 36.dp), textAlign = textAlign
    )
}

@Composable
internal fun BottomPart(desc: String) {
    if (desc.isNotEmpty()) {
        Text(
            text = desc,
            fontSize = 12.sp,
            color = app_textColor_999999
        )
    }

}


@Composable
internal fun StructItemListFix(list: List<PropertyKey>) {
    LazyColumn(modifier = Modifier.heightIn(max = ItemDefaults.contentValueMaxHeight)) {
        itemsIndexed(list, key = { _, data ->
            data.toString()
        }) { index, data ->
            StructItemOnlyKey(data, index < list.size)
        }

    }
}

/**
 * 参考[StructItem]
 */
@Composable
private fun StructItemOnlyKey(propertyKey: PropertyKey, showLine: Boolean) {
    val type by remember(propertyKey) {
        mutableStateOf(propertyKey.type.text)
    }

    val name by remember(propertyKey) {
        mutableStateOf(propertyKey.name)
    }

    val key by remember(propertyKey) {
        mutableStateOf(propertyKey.identifier)
    }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 17.dp, end = 26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    color = app_textColor_333333,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = key,
                    fontSize = 11.sp,
                    color = app_textColor_999999,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.width(55.dp))
            Text(
                text = "",
                fontSize = 16.sp,
                textAlign = TextAlign.Right,
                color = app_textColor_333333,
                fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            LabelPart(type, app_appColor, app_blue_light, TextAlign.Right)


        }
        if (showLine) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(.5.dp)
                    .background(ItemDefaults.contentBorderColor)
            )
        }
    }

}

