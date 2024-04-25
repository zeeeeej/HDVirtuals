package com.yunext.virtuals.ui.screen.devicedetail.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.resource.color.app_gray
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.virtuals.ui.common.DividerBlock
import com.yunext.virtuals.ui.common.StableValue
import com.yunext.virtuals.ui.common.stable
import com.yunext.virtuals.ui.screen.devicedetail.BottomSheetState
import com.yunext.virtuals.ui.screen.devicedetail.TslEditor
import com.yunext.virtuals.ui.screen.devicedetail.deviceevent.EventKeyValue

@Composable
internal fun PropertyValueListBottomSheetV2(
    state: BottomSheetState = remember { BottomSheetState() },
    value: StableValue<List<PropertyValue<*>>>,
    onClose: () -> Unit,
    onCommitted: (List<PropertyValue<*>>) -> Unit,
) {
    val eventKeyValue = value.value
    val title = state.title
    val spec = state.desc
    val msg = state.msg

    // 属性值
    var list: List<StableValue<PropertyValue<*>>> by remember {
        mutableStateOf(emptyList())
    }
    LaunchedEffect(value) {
        // 初始化list
        list = eventKeyValue.map { v ->
            v.stable()
        }.sortDefault()
    }

    TslEditor(title, msg, enable = true, onClose, onCommit = {
        val newValue = list.associate { item ->
            item.value.key to item.value
        }.values.toList()
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