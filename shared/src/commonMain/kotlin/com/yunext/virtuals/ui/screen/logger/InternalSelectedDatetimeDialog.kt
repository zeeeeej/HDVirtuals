package com.yunext.virtuals.ui.screen.logger

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yunext.kmp.common.util.datetimeFormat
import com.yunext.kmp.resource.color.app_gray_light
import com.yunext.kmp.ui.compose.CHItemShadowShape
import com.yunext.virtuals.ui.common.picker.HDDateTimeWheelPicker
import com.yunext.virtuals.ui.common.picker.rememberHDDateTimeWheelPickerState
import com.yunext.virtuals.ui.screen.devicedetail.TslEditor
import com.yunext.virtuals.ui.screen.logger.data.TimeSetter


@Composable
internal fun InternalSelectedDatetimeDialog(
    dateTime: TimeSetter,
    title:String,
    onSelected: (TimeSetter) -> Unit,
    onDismiss: () -> Unit,
) {
    val state =
        rememberHDDateTimeWheelPickerState(dateTime = dateTime, borderColor = app_gray_light)
    CHItemShadowShape {
        TslEditor("选择${title}日期时间", "", enable = true, onDismiss, onCommit = {
            onSelected.invoke(state.dateTime)
        }) {
            HDDateTimeWheelPicker(modifier = Modifier.fillMaxWidth(), state = state)

        }
    }
}