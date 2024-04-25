package com.yunext.virtuals.ui.screen.devicedetail.screenvoyager

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.yunext.kmp.ui.compose.clickablePure
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.data.randomText
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.EventsTab
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.HDDeviceTab
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.PropertiesTab
import com.yunext.virtuals.ui.screen.devicedetail.SelectedHDDeviceTab
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.ServicesTab
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Deprecated("详细见VoyagerDeviceDetailScreen说明")
sealed interface HDDeviceTabForVoyager : Tab {
//    val msg: State<String>

    //    val msg: String
//    @property:Transient
//    val onMsgChanged: (String) -> Unit
//    @property:Transient
//    val onButtonClick: () -> Unit
}

@Deprecated("详细见VoyagerDeviceDetailScreen说明")
@Serializable
internal class PropertiesTabVoyager(
//    private val list: State<List<PropertyData>>,
//    override val msg: State<String>,
//    override val msg: String,
    @property:Transient
    private val onMsgChanged: (String) -> Unit = {},
    @property:Transient
    private val onButtonClick: () -> Unit = {},

    ) : HDDeviceTabForVoyager {

    override val options: TabOptions
        @Composable
        get() {
            return remember {
                TabOptions(
                    index = 0u,
                    title = "属性",
                    icon = null
                )
            }
        }

    @Composable
    override fun Content() {
//        DeviceDetailSubPropertyScreenImpl(list.value) {}
//        //DeviceTabContent()
////        val cur = LocalTabNavigator.current
//        Column(Modifier.fillMaxSize().hdBackground()) {
//            Text("属性")
//            Button({
//                onButtonClick.invoke()
////                cur.current = EventsTabVoyager(msg, onMsgChanged)
//            }) {
//
//                Text("click")
//            }
//            Box(modifier = Modifier.clickablePure {
//                onMsgChanged.invoke("属性 ${randomText()}")
//
//            }) {
//                HDDebugText("${options.title}-${msg.value}")
//
//
//            }
//
//        }
    }
}

@Deprecated("详细见VoyagerDeviceDetailScreen说明")
@Serializable
sealed interface OnMsgChanged{
    fun onChanged(m:String)
}

interface MyOnMsgChanged:OnMsgChanged


@Deprecated("详细见VoyagerDeviceDetailScreen说明")
@Serializable
internal class EventsTabVoyager(
//    private val list: State<List<EventData>>,
//    private val msg: State<String>,
//    override val msg: String,
    private val onMsgChanged:OnMsgChanged
) : HDDeviceTabForVoyager {
//    private val onMsgChanged: (String) -> Unit = {}
    private val onButtonClick: () -> Unit = {}
    override val options: TabOptions
        @Composable
        get() {
            return remember {
                TabOptions(
                    index = 1u,
                    title = "事件",
                    icon = null
                )
            }
        }

    @Composable
    override fun Content() {
//        DeviceTabContent()

        Column(Modifier.fillMaxSize().hdBackground()) {
            //val cur = LocalTabNavigator.current
            Text("事件")
            Button(onClick = {
                //cur.current = ServicesTabVoyager(msg, onMsgChanged)
                onButtonClick.invoke()
            }) {
                Text("click")
            }

            Box(modifier = Modifier.clickablePure {
                onMsgChanged.onChanged("事件 ${randomText()}")
            }) {
//                HDDebugText("${options.title}-${msg.value}")
                HDDebugText("${options.title}-")
            }

        }
    }
}

@Deprecated("详细见VoyagerDeviceDetailScreen说明")
@Serializable
internal class ServicesTabVoyager(
//    private val list: State<List<ServiceData>>,
//    override val msg: State<String>,
//    override val msg: String,
    @property:Transient
    private val onMsgChanged: (String) -> Unit = {},
    @property:Transient
    private val onButtonClick: () -> Unit = {},
) : HDDeviceTabForVoyager {

    override val options: TabOptions
        @Composable
        get() {
            return remember {
                TabOptions(
                    index = 2u,
                    title = "服务",
                    icon = null
                )
            }
        }

    @Composable
    override fun Content() {
//        DeviceTabContent()
        //val cur = LocalTabNavigator.current
        Column(Modifier.fillMaxSize().hdBackground()) {
            Text("服务")
            Button(onClick = {
                // cur.current = PropertiesTabVoyager(msg, onMsgChanged)
                onButtonClick.invoke()
            }) {
                Text("click")
            }
            Box(modifier = Modifier.clickablePure {
                onMsgChanged.invoke("服务 ${randomText()}")
            }) {
//                HDDebugText("${options.title}-${msg.value}")
                HDDebugText("${options.title}-")
            }
        }
    }
}

//<editor-fold desc="使用原生TabRow实现，参考用">
@Deprecated("详细见VoyagerDeviceDetailScreen说明")
@Composable
fun InnerTabNavigation2222() {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {

        TabNavigationImpl(PropertiesTab)

        Spacer(modifier = Modifier.weight(.05f))

        TabNavigationImpl(EventsTab)

        Spacer(modifier = Modifier.weight(.05f))

        TabNavigationImpl(ServicesTab)
    }
}

// 每个Tab对应的页面
// tab+content

@Composable
private fun Tab.DeviceTabContent() {
    val tabList by remember { mutableStateOf(listOf(PropertiesTab, EventsTab, ServicesTab)) }
//    val tabTitle = options.title
//    LifecycleEffect(
//        onStarted = { HDLogger.d("Navigator", "Start tab $tabTitle") },
//        onDisposed = { HDLogger.d("Navigator", "Dispose tab $tabTitle") }
//    )
    Column {

        //<editor-fold desc="【1.Tab部分】">
        // -InnerTabNavigation() // 实现1
        // -使用TabRow
        val tabs: @Composable () -> Unit = {
            val tabNavigator = LocalTabNavigator.current
            tabList.forEach { cur ->
                SelectedHDDeviceTab(
                    cur.options.title,
                    this@DeviceTabContent == cur
                ) {
                    tabNavigator.current = cur
                }
            }
        }

        TabRow(
            backgroundColor = Color.Transparent,
            selectedTabIndex = this@DeviceTabContent.options.index.toInt(),
            tabs = tabs
        )
        //</editor-fold>

        //<editor-fold desc="【2.内容部分】">
//        Navigator(
//            when (this@DeviceTabContent) {
//                is PropertiesTab -> DeviceDetailSubPropertyScreen()
//                is EventsTab -> DeviceDetailSubEventScreen()
//                is ServicesTab -> DeviceDetailSubServiceScreen()
//                else -> error("error tab $this@DeviceTabContent")
//            }
//        ) { innerNav ->
//            SlideTransition(innerNav)
//            // 可使用默认
//            /*
//            { innerScreen ->
//                 HDLogger.d("Navigator", "--innerScreen:$innerScreen innerNav.key:${innerNav}")
//                 innerScreen.Content()
//             }
//             */
//        }
        //</editor-fold>
    }
}

@Composable
private fun RowScope.TabNavigationImpl(
    tab: HDDeviceTab,
) {
    val tabNavigator = LocalTabNavigator.current
    Box(Modifier.weight(1F)) {
        SelectedHDDeviceTab(
            tab = tab.options.title,
            selected = tabNavigator.current.key == tab.key
        ) {
            tabNavigator.current = tab
        }
    }
}
//</editor-fold>