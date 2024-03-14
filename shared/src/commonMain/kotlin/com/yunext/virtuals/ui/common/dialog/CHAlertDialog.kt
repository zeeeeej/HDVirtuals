package com.yunext.virtuals.ui.common.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.color.China
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.ui.compose.Debug
import com.yunext.kmp.ui.compose.clickableX
import com.yunext.virtuals.ui.common.DividerBlock

@Composable
fun CHAlertDialog(
    title: String? = null,
    msg: String,
    left: String? = null,
    right: String? = null,
    onLeft: () -> Unit = {},
    onRight: () -> Unit = {},
    properties: DialogProperties = DialogProperties(
        decorFitsSystemWindows = true,
        usePlatformDefaultWidth = true,
        dismissOnBackPress = false,
        dismissOnClickOutside = true,
        securePolicy = SecureFlagPolicy.Inherit
    ),
    debug: Boolean = false,
    dimAmount: Float = .5f,
    onDismissRequest: () -> Unit,
) {
    Debug("TwinsHomePage-内容-弹窗2")
    CHDialog(properties, dimAmount = dimAmount, onDismissRequest = onDismissRequest) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(DialogDefaults.DEFAULT_Padding_hor)
                .aspectRatio(16 / 9f)
                .clip(DialogDefaults.DEFAULT_SHAPE)
                .background(China.w_xue_bai)
                .debugShape(debug)
//                .padding(16.dp)
                .debugShape(debug), contentAlignment = Alignment.Center

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(DialogDefaults.DEFAULT_Padding))
                if (title?.isNotEmpty() == true) {
                    Text(
                        text = title,
                        color = app_textColor_333333,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .wrapContentSize()
                    )
                }

                Text(
                    text = msg,
                    color = app_textColor_333333,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = DialogDefaults.DEFAULT_Padding)
                )
                Spacer(modifier = Modifier.height(12.dp))
                DividerBlock()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight()
                            .padding(vertical = 16.dp).clickableX {
                                onDismissRequest()
                                onLeft()
                            }, text = left ?: "取消",textAlign = TextAlign.Center
                    )
                    DividerBlock(horizontal = false)
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight()
                            .padding(vertical = 16.dp).clickableX {
                                onDismissRequest()
                                onRight()
                            }, text = right ?: "确定", textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}