package com.yunext.virtuals.ui.screen.logger

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.color.app_gray_light
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.ui.compose.hdClip
import com.yunext.virtuals.ui.screen.logger.data.TimeSetter
import com.yunext.virtuals.ui.screen.logger.data.TimeSetterType
import com.yunext.virtuals.ui.screen.logger.data.ZERO
import com.yunext.virtuals.ui.screen.logger.data.checkShow

@Composable
internal fun InternalDateTimeSetterBlock(
    modifier: Modifier,
    start: TimeSetter? = ZERO,
    end: TimeSetter? = ZERO,
    onClick: (TimeSetterType) -> Unit,
) {
    Row(
        modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        DateTimeSetter(
            modifier = Modifier.wrapContentHeight().weight(1f),
            text = (start ?: ZERO).checkShow("选择开始时间")
        ) {
            onClick.invoke(TimeSetterType.Start)
        }
        Text(
            "-",
            style = TextStyle.Default.copy(color = app_textColor_333333, fontSize = 14.sp),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        DateTimeSetter(
            modifier = Modifier.wrapContentHeight().weight(1f),
            text = (end ?: ZERO).checkShow("选择结束时间")
        ) {
            onClick.invoke(TimeSetterType.End)
        }
    }
}

@Composable
private fun DateTimeSetter(modifier: Modifier, text: String, onClick: () -> Unit) {
    Text(
        text,
        style = TextStyle.Default.copy(
            color = app_textColor_333333,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        ),
        modifier = modifier
            .border(1.dp, color = app_gray_light, shape = RoundedCornerShape(8.dp))
            .hdClip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp, horizontal = 12.dp)

    )
}