package com.yunext.kmp.ui.compose//package com.yunext.twins.ui.compoents.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.kmp.resource.color.China

@Composable
fun PreviewPart(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(China.w_qian_shi_bai)
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        content()
    }
}

