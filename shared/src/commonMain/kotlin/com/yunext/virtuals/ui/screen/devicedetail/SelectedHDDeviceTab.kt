package com.yunext.virtuals.ui.screen.devicedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.yunext.kmp.resource.color.China
import com.yunext.kmp.ui.compose.clickablePure

@Composable
internal fun SelectedHDDeviceTab(
    tab: HDDeviceTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().height(44.dp).clickablePure {
        onClick()
    }, contentAlignment = Alignment.Center) {
        Text(
            tab.options.title,
            style = TextStyle(color = if (selected) China.r_luo_xia_hong else Color.Black),
            modifier = Modifier

        )
    }
}