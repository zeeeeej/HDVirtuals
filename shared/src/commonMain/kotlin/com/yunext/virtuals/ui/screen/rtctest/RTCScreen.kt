package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal typealias OnStartBroadcast = () -> Unit
internal typealias OnDeviceNameChanged = (String) -> Unit
internal typealias OnStopBroadcast = () -> Unit
internal typealias OnUpdateProperty = (ParameterDataVo) -> Unit
internal typealias OnConnect = () -> Unit

internal typealias OnMasterStartScan = () -> Unit
internal typealias OnMasterStopScan = () -> Unit
internal typealias OnMasterConnect = (BleDeviceVo) -> Unit
internal typealias OnSelectedDevice = (BleDeviceVo) -> Unit
internal typealias OnMasterDisconnect = () -> Unit
internal typealias OnMasterEnableNotify = () -> Unit
internal typealias OnMasterWrite = () -> Unit
internal typealias OnMasterRead = () -> Unit
internal typealias OnAuth = (String) -> Unit
internal typealias OnSetProperty = (String, String) -> Unit
//internal typealias OnSetProperty = (ParameterDataVo)->Unit
internal typealias OnSetTimestamp = () -> Unit
internal typealias OnTestAuthed = () -> Unit
internal typealias OnTestUnAuthed = () -> Unit
internal typealias OnStartConnected = () -> Unit
internal typealias OnTestCase = (RTCTestCase) -> Unit


class RTCScreen(private val master: BleMenu,val app:Boolean  = false) : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        when (master) {
            BleMenu.Master -> {
                val screenViewModel = rememberScreenModel {
                    RTCMasterViewModel()
                }

                val state by screenViewModel.state.collectAsState()
                @OptIn(ExperimentalResourceApi::class) (RTCMasterPage(
                    state = state,
                    onLeft = navigator::popUntilRoot,
                    onStartScan = screenViewModel::startScan,
                    onStopScan = screenViewModel::stopScan,
                    onConnect = screenViewModel::connect,
                    onDisconnect = screenViewModel::disconnect,
                    onEnableNotify = screenViewModel::enableNotify,
                    onWrite = screenViewModel::write,
                    onRead = screenViewModel::read,
                    onAuth = screenViewModel::auth,
                    onSetProperty = screenViewModel::setProperty,
                    onSetTimestamp = screenViewModel::setTimestamp,
                    onTestAuthed = screenViewModel::authedTestCase,
                    onTestUnAuthed = screenViewModel::unAuthedTestCase,
                ))
            }

            BleMenu.Slave -> {
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

            BleMenu.RTCTestCase -> {
                val screenViewModel = rememberScreenModel {
                    RTCTestCaseViewModel()
                }

                val state by screenViewModel.state.collectAsState()
                @OptIn(ExperimentalResourceApi::class) (RTCTestCasePage(
                    state = state,
                    onLeft = navigator::popUntilRoot,
                    onStartScan = screenViewModel::startScan,
                    onStopScan = screenViewModel::stopScan,
                    onConnect = screenViewModel::connect,
                    onDisconnect = screenViewModel::disconnect,
                    onEnableNotify = screenViewModel::enableNotify,
                    onWrite = screenViewModel::write,
                    onRead = screenViewModel::read,
                    onAuth = screenViewModel::auth,
                    onSetProperty = screenViewModel::setProperty,
                    onSetTimestamp = screenViewModel::setTimestamp,
                    onTestAuthed = screenViewModel::authedTestCase,
                    onTestUnAuthed = screenViewModel::unAuthedTestCase,
                    onSelected = screenViewModel::onSelected,
                    onStartConnected = screenViewModel::onStartConnected,
                    onTestCase = screenViewModel::onTestCase,
                    app = app
                ))
            }
        }


    }
}




