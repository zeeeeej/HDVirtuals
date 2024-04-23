package com.yunext.virtuals.ui.screen.devicedetail.bottomsheet

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
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
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.longValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.nameAndKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.specDisplay
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.unitStr
import com.yunext.kmp.resource.color.app_gray
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.virtuals.ui.common.DividerBlock
import com.yunext.virtuals.ui.common.EditTextCenterBlock
import com.yunext.virtuals.ui.common.StableValue
import com.yunext.virtuals.ui.screen.devicedetail.TslEditor

// <editor-fold desc="[修改int float double]">
// 修改int float double a
@Composable
internal fun NumberPropertyBottomSheet(
    wrapper: StableValue<PropertyValue<*>>,
    edit:Boolean = true,
    onClose: () -> Unit,
    onCommitted: (PropertyValue<*>) -> Unit,
) {
    val property = wrapper.value
    val title = if (edit) "修改" else "添加"
    val key = property.key
    val msg = key.nameAndKey
    val spec = key.specDisplay
    val unit = key.unitStr
    var value: Number? by remember {
        mutableStateOf(null)
    }
    LaunchedEffect(Unit) {
        value = when (property) {
            is DoublePropertyValue -> property.value
            is FloatPropertyValue -> property.value
            is IntPropertyValue -> property.value
            is DatePropertyValue -> property.longValue
            else -> throw IllegalStateException("NumberPropertyBottomSheet不支持的属性类型:$property")
        }
    }
    TslEditor(title, msg, enable = true, onClose, onCommit = {
        val newProperty = when (property) {
            is DoublePropertyValue -> DoublePropertyValue(property.key, value?.toDouble() ?: 0.0)
            is FloatPropertyValue -> FloatPropertyValue(property.key, value?.toFloat() ?: 0f)
            is IntPropertyValue -> IntPropertyValue(property.key, value?.toInt() ?: 0)
            is DatePropertyValue -> DatePropertyValue(property.key, value?.toLong().toString())
            else -> throw IllegalStateException("NumberPropertyBottomSheet不支持的属性类型:$property")
        }

        onCommitted.invoke(newProperty)
    }) {
        val keyboardType = when (property) {
            is DoublePropertyValue -> KeyboardType.Decimal
            is FloatPropertyValue -> KeyboardType.Decimal
            is IntPropertyValue -> KeyboardType.Number
            is DatePropertyValue -> KeyboardType.Number
            else -> throw IllegalStateException("NumberPropertyBottomSheet不支持的属性类型:$property")
        }

        val calculate by remember {
            mutableStateOf(
                when (property) {
                    is DoublePropertyValue -> { source: String -> source.toDouble() }
                    is FloatPropertyValue -> { source: String -> source.toFloat() }
                    is IntPropertyValue -> { source: String -> source.toInt() }
                    is DatePropertyValue -> { source: String -> source.toLong() }
                    else -> throw IllegalStateException("NumberPropertyBottomSheet不支持的属性类型:$property")
                }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            EditTextCenterBlock(
                text = "${value ?: ""}",
                hint = "请输入",
                onValueChange = {
                    try {
                        value = if (it.isEmpty()) null else calculate(it)
                    } catch (e: Throwable) {
                        HDLogger.w("NumberPropertyBottomSheet", "${e.message}")
                    }
                },
                modifier = Modifier
                    .weight(1f, true)
                    .widthIn(max = 200.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType)
            )
            Text(
                text = unit,
                color = app_textColor_999999,
                fontSize = 11.sp,
                modifier = Modifier
            )
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
// 修改int float double z
// </editor-fold>