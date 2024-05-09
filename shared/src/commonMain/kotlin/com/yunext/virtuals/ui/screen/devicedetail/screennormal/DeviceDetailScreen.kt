package com.yunext.virtuals.ui.screen.devicedetail.screennormal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.Effect
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.common.dialog.CHTipsDialog
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.MenuData
import com.yunext.virtuals.ui.processing
import com.yunext.virtuals.ui.screen.configwifi.ConfigWiFiScreen
import com.yunext.virtuals.ui.screen.devicedetail.vm.DeviceDetailScreenModel
import com.yunext.virtuals.ui.screen.devicedetail.vm.DeviceDetailState
import com.yunext.virtuals.ui.screen.logger.LoggerScreen
import com.yunext.virtuals.ui.screen.setting.SettingScreen

data class DeviceDetailScreen(private val deviceAndState: DeviceAndStateViewData
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            DeviceDetailScreenModel(
                DeviceDetailState(
                    device = deviceAndState,
                    effect = Effect.Idle
                )
            )
        }

        val state by screenModel.state.collectAsState()
        Debug { "[recompose_test_01] DeviceDetailScreen ${state.hashCode()} " }
        DeviceDetailScreenImplNew(device = state.device, onLeft = {
            navigator.pop()
        }, onMenuClick = {
            when (it) {
                MenuData.ConfigWiFi -> navigator.push(ConfigWiFiScreen())
                MenuData.Setting -> navigator.push(SettingScreen())
                MenuData.Logger -> navigator.push(LoggerScreen(deviceAndState))
                MenuData.UpdateTsl -> {
                    // TODO update tsl
                }
            }
        }, onPropertyEdit = {
            screenModel.changeProperty(it)
        }, onEventTrigger = { key, value ->
            screenModel.triggerEvent(key, value)
        }, onServiceListener = {key,input->
            screenModel.triggerService(key,input)
        })

        var loading by remember { mutableStateOf(true) }
        var alert: String? by remember { mutableStateOf(null) }
        LaunchedEffect(state) {
            val effect = state.effect
            loading = effect.processing
            alert = state.alert
        }
        if (loading) {
            CHLoadingDialog("数据加载中...") {
                loading = false
            }
        }

        val t by remember {
            derivedStateOf { alert ?: "" }
        }
        if (t.isNotEmpty()) {
            CHTipsDialog(text = t) {
                alert = null
            }
        }
    }
}