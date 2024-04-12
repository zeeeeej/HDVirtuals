package com.yunext.virtuals.ui.screen.devicedetail.bottomsheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.nameAndKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.specDisplay
import com.yunext.kmp.resource.color.app_gray
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.virtuals.ui.common.DividerBlock
import com.yunext.virtuals.ui.common.EditTextCenterBlock
import com.yunext.virtuals.ui.screen.devicedetail.TslEditor

// <editor-fold desc="[修改text,date]">
// 修改text,date a
@Composable
internal fun TextPropertyBottomSheet(
    property: PropertyValue<*>,
    edit: Boolean = true,
    onClose: () -> Unit,
    onCommitted: (PropertyValue<*>) -> Unit,
) {
    val title = if (edit) "修改" else "添加"
    val key = property.key
    val msg = key.nameAndKey
    val spec = key.specDisplay
    var value: String by remember {
        mutableStateOf("")
    }
    LaunchedEffect(Unit) {
        value = when (property) {
            is TextPropertyValue -> property.value ?: ""
//           is DatePropertyValue -> property.value?:""
            else -> throw IllegalStateException("TextPropertyBottomSheet 不支持的属性类型:$property")
        }
    }
    TslEditor(title, msg, enable = true, onClose, onCommit = {
        val newProperty = when (property) {
            is TextPropertyValue -> TextPropertyValue(property.key, value)
//            is DatePropertyValue -> DatePropertyValue(property.key, value)
            else -> throw IllegalStateException("TextPropertyBottomSheet 不支持的属性类型:$property")
        }
        onCommitted.invoke(newProperty)
    }) {
        EditTextCenterBlock(
            text = value,
            hint = "请输入",
            onValueChange = {
                value = it
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
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
}
// 修改text,date z
// </editor-fold>