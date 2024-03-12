package com.yunext.virtuals.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.virtuals.ui.screen.devicelist.DeviceListScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class SplashScreen : Screen {

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            var show by remember {
                mutableStateOf(false)
            }
            val navigator = LocalNavigator.currentOrThrow
            LaunchedEffect(Unit) {
                launch {
                    delay(1000)
                    show = true
                    delay(1000)
                    navigator.replace(DeviceListScreen())
                }
            }
            AnimatedVisibility(show) {
                Text("欢迎使用孪生App", modifier = Modifier.wrapContentSize())
            }
        }

    }
}