package com.yunext.virtuals.ui.screen.devicelist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.color.app_textColor_999999

@Composable
fun TwinsVersion(modifier: Modifier, version: String) {
    Text(
        version,
        color = app_textColor_999999.copy(.5f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Light,
        modifier = modifier.wrapContentSize().padding(16.dp)
    )
}