package com.yunext.virtuals.ui.screen.devicelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.color.app_button_brush
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.common.TwinsBackgroundBlock
import com.yunext.virtuals.ui.common.TwinsEmptyViewForDevice
import com.yunext.virtuals.ui.common.dialog.CHAlertDialog
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.screen.LocalPaddingValues
import com.yunext.virtuals.ui.screen.debug.DebugBle
import com.yunext.virtuals.ui.screen.debug.DebugDialog
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun DeviceListScreenImpl(
    modifier: Modifier = Modifier,
    list: List<DeviceAndStateViewData>,
//    effect: Effect,
    onRefresh: () -> Unit,
    onDeviceSelected: (DeviceAndStateViewData) -> Unit,
    onDeviceDelete: (DeviceAndStateViewData) -> Unit,
    onActionAdd: () -> Unit,
    onDisconnect: (DeviceAndStateViewData) -> Unit,
) {
    /* 背景 */
    @OptIn(ExperimentalResourceApi::class)
    TwinsBackgroundBlock()

//    val refreshing = effect.processing
//    val pullRefreshState = rememberPullRefreshState(
//        refreshing = refreshing, onRefresh = { onRefresh() })
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(LocalPaddingValues.current)
            .statusBarsPadding()
//            .pullRefresh(pullRefreshState)
    ) {
        // 版本信息
        TwinsVersion(modifier = Modifier.align(Alignment.TopCenter), "HD孪生设备KMP v0.0.1")

        Column(Modifier.fillMaxSize()) {
            Debug("TwinsHomePage-内容")
            Spacer(modifier = Modifier.height(36.dp))
            // 项目信息
            Text(
                text = "KMM孪生设备",//stringResource(id = R.string.app_name),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                // fontStyle = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "ssl://emqtt-test.yunext.com:8881",//stringResource(id = R.string.app_virtual_device),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.titleSmall.copy(app_textColor_999999),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                // fontStyle = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 设备列表标题
            Row(verticalAlignment = Alignment.CenterVertically) {
                Debug("TwinsHomePage-内容-设备列表标题")
                Text(
                    text = "虚拟设备",//stringResource(id = R.string.app_virtual_device),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    // fontStyle = MaterialTheme.typography.titleLarge
                )
                Text(

                    text = "1",//stringResource(id = R.string.app_name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    // fontStyle = MaterialTheme.typography.titleLarge
                )
            }

            // 设备列表
            val s = @Composable {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Debug("TwinsHomePage-内容-设备列表")
                    if (list.isEmpty()) {
                        TwinsEmptyViewForDevice()
                    } else {
                        TwinsDeviceList(
                            Modifier
//                            .captionBarPadding()
                            , list, onDeviceSelected, onDeviceDelete, onDisconnect
                        )
                    }

//                    PullRefreshIndicator(
//                        refreshing = refreshing,
//                        state = pullRefreshState,
//                        scale = true,
//                        modifier = Modifier.align(
//                            Alignment.TopCenter
//                        )
//                    )

                    //DebugCompositionLayoutByMeasureAndPlaceDrawing()
                }
            }

            s()


        }


        // 悬浮按钮
        FloatingActionButton(
            onClick = {
                onActionAdd.invoke()
            },
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(4.dp),
            modifier = Modifier
                .padding(end = 16.dp, bottom = 16.dp)
                .align(Alignment.BottomEnd),
//            contentColor = Color.Red,
//            containerColor = China.r_luo_xia_hong

        ) {
            Debug("TwinsHomePage-内容-悬浮按钮")
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(brush = app_button_brush)
            ) {

                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        //DebugDialog()
    }
}





