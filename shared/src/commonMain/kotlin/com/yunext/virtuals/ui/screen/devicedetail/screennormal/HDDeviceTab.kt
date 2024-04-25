package com.yunext.virtuals.ui.screen.devicedetail.screennormal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TabRow
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
import com.yunext.virtuals.ui.screen.devicedetail.SelectedHDDeviceTab

internal sealed interface HDDeviceTab : Tab

internal val HDDeviceTab.index: Int
    get() = when (this) {
        EventsTab -> 1
        PropertiesTab -> 0
        ServicesTab -> 2
    }

internal val deviceDetailTabs by lazy {
    listOf(PropertiesTab, EventsTab, ServicesTab)
}

internal data object PropertiesTab : HDDeviceTab {

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
        DeviceTabContent()
    }
}

internal data object EventsTab : HDDeviceTab {

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
        DeviceTabContent()
    }
}

internal data object ServicesTab : HDDeviceTab {

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
        DeviceTabContent()
    }
}

//<editor-fold desc="使用原生TabRow实现，参考用">
@Deprecated("使用原生TabRow")
@Composable
@Suppress("DEPRECATION")
private fun InnerTabNavigation() {
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

@Deprecated("使用原生TabRow")
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