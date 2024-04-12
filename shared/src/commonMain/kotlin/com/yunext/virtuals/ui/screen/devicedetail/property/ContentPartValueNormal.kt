package com.yunext.virtuals.ui.screen.devicedetail.property

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.color.app_textColor_333333

@Composable
internal fun ContentPartValueNormal(text: String) {
    ContentPartCurrentValue {
        Text(text = text, fontSize = 18.sp, color = app_textColor_333333)
    }
}