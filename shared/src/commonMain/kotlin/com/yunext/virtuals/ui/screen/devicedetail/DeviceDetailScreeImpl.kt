package com.yunext.virtuals.ui.screen.devicedetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

import cafe.adriel.voyager.transitions.SlideTransition
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.valueStr
import com.yunext.kmp.resource.color.app_background_70
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.ui.compose.CHItemShadowShape
import com.yunext.kmp.ui.compose.Debug
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.HDImage
import com.yunext.virtuals.ui.common.TwinsBackgroundBlock
import com.yunext.virtuals.ui.common.TwinsTitle
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.common.dialog.XPopContainer
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.MenuData
import com.yunext.virtuals.ui.data.iconRes
import com.yunext.virtuals.ui.screen.devicelist.DeviceCommunicationIdAndModel
import com.yunext.virtuals.ui.theme.ItemDefaults
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.resources.ExperimentalResourceApi

//@OptIn(ExperimentalResourceApi::class)
//@Composable
//@Deprecated("见DeviceDetailScreenImplNew", ReplaceWith("DeviceDetailScreenImplNew"))
//internal fun DeviceDetailScreenImpl(
//    device: DeviceAndStateViewData,
//    onMenuClick: (MenuData) -> Unit,
//    onLeft: () -> Unit,
//) {
//
//    TwinsBackgroundBlock()
//    var showMenu by remember { mutableStateOf(false) }
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // 1 标题
//        TwinsTitle(modifier = Modifier
//            .hdBackground { app_background_70 },
//            text = device.name,
//            icon = device.status.iconRes,
//            leftClick = {
//                onLeft()
//            },
//            rightClick = {
//                showMenu = !showMenu
//            })
//
//        // 2 内容
//        Box(Modifier.fillMaxWidth()) {
//            Column() {
//                // 2.1 top
//                Column(
//                    Modifier
//                        .fillMaxWidth()
//                        .hdBackground { app_background_70 }
//                        .padding(vertical = 0.dp, horizontal = 12.dp),
//                ) {
//                    Top(device = device)
//                    Spacer(Modifier.height(12.dp))
//                }
//
//                // 2.2 tab+content
//                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
//                    TabNavigator(
//                        PropertiesTab,
//                        tabDisposable = {
//                            TabDisposable(
//                                navigator = it,
//                                tabs = listOf(PropertiesTab, EventsTab, ServicesTab)
//                            )
//                        },
//
//                        )/*{
//                        Column {
//                            Text("=>${it.current.options.title}")
//                            CurrentTab()
//                            Navigator(when(it.current){
//                                is PropertiesTab->DeviceDetailSubPropertyScreen()
//                                is EventsTab->DeviceDetailSubEventScreen()
//                                is ServicesTab->DeviceDetailSubServiceScreen()
//                                else-> error("error tab")
//                            })
//                        }
//                    }*/
//                }
//            }
//
//            // pop
//            if (showMenu) {
//                MenuList(
//                    modifier = Modifier
//                        .padding(horizontal = 12.dp)
//                        .align(Alignment.TopEnd), onDismiss = { showMenu = false }) {
//                    onMenuClick(it)
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun DeviceDetailScreenImplNew(
    device: DeviceAndStateViewData,
    onMenuClick: (MenuData) -> Unit,
    onPropertyEdit: (PropertyValue<*>) -> Unit,
    onLeft: () -> Unit,
) {
    TwinsBackgroundBlock()
    var showMenu by remember { mutableStateOf(false) }
//    val propertyListSize by remember {
//        derivedStateOf{
//            device.propertyList.size
//        }
//    }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1 标题
        TwinsTitle(modifier = Modifier
            .hdBackground { app_background_70 },
            text = device.name,
            icon = device.status.iconRes,
            leftClick = {
                onLeft()
            },
            rightClick = {
                showMenu = !showMenu
            })

        // 2 内容
        Box(Modifier.fillMaxWidth()) {
            Column() {
                // 2.1 top
                Column(
                    Modifier
                        .fillMaxWidth()
                        .hdBackground { app_background_70 }
                        .padding(vertical = 0.dp, horizontal = 12.dp),
                ) {
                    Top(device = device)
                    Spacer(Modifier.height(12.dp))
                }

                // 2.2 tab+content
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column {
                        var curTab: HDDeviceTab by remember() { mutableStateOf(PropertiesTab) }
                        DeviceDetailTabs(curTab, deviceDetailTabs) {
                            curTab = it
                        }
                        when (curTab) {
                            PropertiesTab -> DeviceDetailSubPropertyScreenImpl(device.propertyList) {
                                onPropertyEdit.invoke(it.value.real)
                            }

                            EventsTab -> DeviceDetailSubEventScreenImpl(device.propertyList)
                            ServicesTab -> DeviceDetailSubServiceScreenImpl()
                        }

                        // 暂且不用 voyager tab 实现
                        //Navigator(DeviceDetailTabsScreen(curTab,device))
                    }
                }
            }

            // pop
            if (showMenu) {
                MenuList(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .align(Alignment.TopEnd), onDismiss = { showMenu = false }) {
                    onMenuClick(it)
                }
            }
        }
    }
}


@Deprecated("暂且不用 voyager tab 实现")
private class DeviceDetailTabsScreen(
    private val tab: HDDeviceTab,
    private val device: DeviceAndStateViewData,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(tab) {
            val innerScreen = when (tab) {
                EventsTab -> DeviceDetailSubPropertyScreen(device)
                PropertiesTab -> DeviceDetailSubEventScreen()
                ServicesTab -> DeviceDetailSubServiceScreen()
            }
            navigator.replace(innerScreen)
        }
    }
}

@Composable
private fun DeviceDetailTabs(
    selected: HDDeviceTab,
    tabs: List<HDDeviceTab>,
    onClick: (HDDeviceTab) -> Unit,
) {
    Row(Modifier.fillMaxWidth()) {
        tabs.forEach { tab ->
            key(tab.key) {
                DeviceDetailTab(tab, selected = tab == selected) {
                    onClick(tab)
                }
            }
        }
    }
}

@Composable
private fun RowScope.DeviceDetailTab(tab: HDDeviceTab, selected: Boolean, onClick: () -> Unit) {
    Box(Modifier.weight(1f)) {
        SelectedHDDeviceTab(tab, selected, onClick)
    }
}

@Composable
private fun Top(
    device: DeviceAndStateViewData,
    modifier: Modifier = Modifier,
) {
    DeviceCommunicationIdAndModel(modifier, device.communicationId, device.model)
}

@Composable
private fun MenuList(
    modifier: Modifier = Modifier,
    onDismiss: () -> kotlin.Unit,
    onMenuClick: (MenuData) -> Unit,
) {
    val list: Array<MenuData> by remember {
        mutableStateOf(MenuData.values())
    }
    XPopContainer(onDismiss = {
        onDismiss()
    }) {
        CHItemShadowShape(elevation = 16.dp, modifier = modifier) {
            Column(
                modifier = Modifier
//                .size(300.dp)
//                .wrapContentWidth()
                    .width(180.dp)
//                .widthIn(max = 300.dp, min = 100.dp)
//                .wrapContentHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .hdBackground { ItemDefaults.itemBackground }
//                .padding(16.dp)
            ) {
                list.forEach {
                    MenuItem(it) {
                        onMenuClick(it)
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun MenuItem(menuData: MenuData, onClick: () -> Unit) {
    Row(modifier = Modifier
        .height(49.dp)
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        HDImage(
            resource = menuData.iconRes,
//            painter = painterResource(menuData.icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = (menuData.text),
            fontSize = 14.sp,
            color = app_textColor_333333
        )

    }
}