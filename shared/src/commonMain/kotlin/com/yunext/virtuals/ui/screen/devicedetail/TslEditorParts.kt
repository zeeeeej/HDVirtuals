package com.yunext.virtuals.ui.screen.devicedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.yunext.kmp.resource.color.China
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_666666
import com.yunext.kmp.ui.compose.CHItemShadowShape
import com.yunext.kmp.ui.compose.clickableX
import com.yunext.virtuals.ui.common.CommitButtonBlock
import com.yunext.virtuals.ui.theme.ItemDefaults

// <editor-fold desc="[base]">
@Composable
internal fun TslEditor(
    title: String,
    desc: String,
    enable :Boolean = true,
    onClose: () -> Unit,
    onCommit: () -> Unit,
    content: @Composable () -> Unit,
) {
    CHItemShadowShape(elevation = 32.dp) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(China.w_xue_bai)
                .padding(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    color = app_textColor_333333,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )

                Icon(imageVector = Icons.Filled.Close, contentDescription = null,
                    Modifier
                        .size(24.dp)
                        .clickableX {
                            onClose()
                        }
                        .align(Alignment.CenterEnd))


            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = desc,
                fontSize = 14.sp,
                color = app_textColor_333333,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
            Spacer(modifier = Modifier.height(48.dp))
            CommitButtonBlock(
                text = "确定",
                enable = enable
            ) {
                onCommit.invoke()
            }
            Spacer(modifier = Modifier.height(12.dp))

        }
    }

}
// </editor-fold>

@Composable
internal fun InputPart(label: String, list: List<*>) {
    Column(modifier = ItemDefaults.borderModifier.padding(12.dp)) {
        Text(text = label, color = app_textColor_666666, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = ItemDefaults.border4Modifier) {
            StructItemListFix(list)
        }
    }
}