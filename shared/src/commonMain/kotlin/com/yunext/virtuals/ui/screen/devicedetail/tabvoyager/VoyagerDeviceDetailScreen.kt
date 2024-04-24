package com.yunext.virtuals.ui.screen.devicedetail.tabvoyager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.MenuData
import com.yunext.virtuals.ui.processing
import com.yunext.virtuals.ui.screen.configwifi.ConfigWiFiScreen
import com.yunext.virtuals.ui.screen.devicedetail.vm.DeviceDetailScreenModel
import com.yunext.virtuals.ui.screen.devicedetail.vm.DeviceDetailState
import com.yunext.virtuals.ui.screen.logger.LoggerScreen
import com.yunext.virtuals.ui.screen.setting.SettingScreen

@Deprecated("详细见注释")
/**
 * 关于Screen的参数，必须支持kotlin.serialization。State<T>不行，回调也不行。
 * 关于Screen下的Tab共享数据，ScreenModel不支持。
 */
data class VoyagerDeviceDetailScreen(
    private val deviceAndState: DeviceAndStateViewData,
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
        Debug("[recompose_test_01] DeviceDetailScreen property size = ${state.device.propertyList.size} ")
        // 内容
        VoyagerDeviceDetailScreenImpl(device = state.device, onLeft = {
            navigator.pop()
        }, onMenuClick = {
            when (it) {
                MenuData.ConfigWiFi -> navigator.push(ConfigWiFiScreen())
                MenuData.Setting -> navigator.push(SettingScreen())
                MenuData.Logger -> navigator.push(LoggerScreen())
                MenuData.UpdateTsl -> {
                    // TODO update tsl
                }
            }
        }, onPropertyEdit = {
            screenModel.changeProperty(it)
        })

        // 弹窗 loading 等
        var loading by remember { mutableStateOf(true) }
        LaunchedEffect(state) {
            val effect = state.effect
            loading = effect.processing
        }
        if (loading) {
            CHLoadingDialog("数据加载中...") {
                loading = false
            }
        }
    }
}