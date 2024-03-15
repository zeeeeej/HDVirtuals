package com.yunext.virtuals.ui.screen.devicedetail

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.virtuals.ui.data.DeviceAndState
import com.yunext.virtuals.ui.data.MenuData
import com.yunext.virtuals.ui.screen.configwifi.ConfigWiFiScreen
import com.yunext.virtuals.ui.screen.logger.LoggerScreen
import com.yunext.virtuals.ui.screen.setting.SettingScreen

data class DeviceDetailScreen(private val deviceAndState: DeviceAndState) : Screen {


    @Composable
    override fun Content() {
        // Old() // 传统TabRow实现
        // TabNavigationScreen() // 参考嵌套tab
        val navigator = LocalNavigator.currentOrThrow
        DeviceDetailScreeImpl(device = deviceAndState, onLeft = {
            navigator.pop()
        }, onMenuClick = {
                 when(it){
                     MenuData.ConfigWiFi -> navigator.push(ConfigWiFiScreen())
                     MenuData.Setting ->  navigator.push(SettingScreen())
                     MenuData.Logger ->  navigator.push(LoggerScreen())
                     MenuData.UpdateTsl -> {
                         // TODO update tsl
                     }
                 }
        })
    }
}