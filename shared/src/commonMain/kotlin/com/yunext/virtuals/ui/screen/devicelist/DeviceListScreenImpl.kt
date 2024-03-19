package com.yunext.virtuals.ui.screen.devicelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
                        TwinsDeviceList(Modifier, list,{
                            onDeviceSelected.invoke(it)
                        }){
                            onDeviceDelete.invoke(it)
                        }
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


        DebugDialog()
    }

}

@Composable
private  fun DebugDialog() {
    // 弹窗
    Debug("TwinsHomePage-内容-弹窗")
    var show by remember {
        mutableStateOf(false)
    }
    var index by remember {
        mutableStateOf(0)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                index += 1
                show = !show


//                val a = A1("Greeting hello")
//                val a = A3
//                println("Greeting a = $a")
//                val json = Json.encodeToString(A.serializer(), a)
//                println("Greeting json = $json")
//                val r = Json.decodeFromString(A.serializer(), json)
//                println("Greeting r = $r")

//                val abstractContext = SerializersModule {
//                    polymorphic(A::class) {
//                        subclass(A1::class)
//                        subclass(A2::class)
//                        // no need to register ProtocolWithAbstractClass.ErrorMessage
//                    }
//
//                }
//                val jsonER = Json {
//                    serializersModule = abstractContext
//                }
//                val json = jsonER.encodeToString(A3)
//                println("Greeting json = $json")
//                val r = jsonER.decodeFromString(A1.serializer(),json)
//                println("Greeting r = $r")

            },
            modifier = Modifier.Companion
                .align(Alignment.BottomStart)
                .padding(vertical = 16.dp)
        ) {
            Text(text = "Debug")
        }
        if (show) {
//        if (index % 2 == 0) {
////                CHLoadingDialog(dimAmount = 0.1f) {
////                    show = false
////                }
//            NewsDialog() {
//                show = false
//            }
//        } else {
            //TODO("底部弹窗")
            CHAlertDialog("haha", "天生我才必有用") {
                show = false
            }
//        }

//        AlertDialog(onDismissRequest = { show = false }, buttons = {
//            Text(text = "I am dialog", modifier = Modifier.size(200.dp))
//        })

        }
    }
}



