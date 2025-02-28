package com.yunext.virtuals.ui.screen.configwifi

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.kmp.ui.compose.clickablePure
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.demo.PullRefreshSample
import com.yunext.virtuals.ui.demo.PullToRefreshSampleCustomState
import com.yunext.virtuals.ui.demo.rememberstate.TestRememberState

class ConfigWiFiScreen :Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenViewModel  = rememberScreenModel {
            ConfigWiFiScreenViewModel("123")
        }
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.clickablePure {
                navigator.pop()
            }){
                HDDebugText("配网")
            }



        }

//        PullRefreshSample()
//        PullToRefreshSampleCustomState()
        TestRememberState()
    }
}