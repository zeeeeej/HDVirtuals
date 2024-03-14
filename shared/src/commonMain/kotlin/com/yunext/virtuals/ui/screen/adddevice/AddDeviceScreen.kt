package com.yunext.virtuals.ui.screen.adddevice

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.data.Effect
import com.yunext.virtuals.ui.data.processing
import com.yunext.virtuals.ui.screen.devicelist.DeviceListScreenModel

class AddDeviceScreen : Screen {

    @Composable
    override fun Content() {


        Box(Modifier.fillMaxSize().hdBackground()) {
            HDDebugText("设备添加")
            val navigator = LocalNavigator.currentOrThrow

            val screenModel: AddDeviceScreenModel = rememberScreenModel {
                AddDeviceScreenModel()
            }

            val state by screenModel.state.collectAsState()

            if (state.addResult) {
                navigator.pop()
            } else {
                TwinsAddDevicePage(onLeft = {
                    navigator.pop()
                }, onDeviceCommit = { name, type, id, model ->
                    screenModel.doAddDevice(name, type, id, model)
                })
            }
            if(state.effect.processing){
                CHLoadingDialog("Loading",dimAmount=.5f){

                }
            }

        }
    }
}