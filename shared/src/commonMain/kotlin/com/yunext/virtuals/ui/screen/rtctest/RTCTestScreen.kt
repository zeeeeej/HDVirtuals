package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal typealias OnStartBroadcast = ()->Unit
internal typealias OnDeviceNameChanged = (String)->Unit
internal typealias OnStopBroadcast = ()->Unit
internal typealias OnUpdateProperty = (ParameterDataVo)->Unit
internal typealias OnConnect = ()->Unit

class RTCTestScreen(private val master: Boolean) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        if (master) {
            val screenViewModel = rememberScreenModel {
                RTCMasterViewModel(master)
            }

            val state by screenViewModel.state.collectAsState()
            @OptIn(ExperimentalResourceApi::class) (RTCMasterPage(
                state = state,
                onLeft = navigator::popUntilRoot,
                onStartScan = screenViewModel::startScan,
                onStopScan = screenViewModel::stopScan,
                onConnect = screenViewModel::connect,
                onEnableNotify = screenViewModel::enableNotify,
            ))
        } else {
            val screenViewModel = rememberScreenModel {
                RTCSlaveViewModel()
            }
            val state by screenViewModel.state.collectAsState()
            @OptIn(ExperimentalResourceApi::class)
            (RTCSlavePage(
                state = state,
                onLeft = navigator::popUntilRoot,
                onStartBroadcast = screenViewModel::startBroadcast,
                onDeviceNameChanged = screenViewModel::config,
                onStopBroadcast = screenViewModel::stopBroadcast,
                onUpdateProperty = screenViewModel::updateProperty,
                onConnect = { },
            ))
        }

    }
}




