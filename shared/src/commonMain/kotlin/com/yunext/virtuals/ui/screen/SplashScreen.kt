package com.yunext.virtuals.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.kmp.resource.HDRes
import com.yunext.virtuals.ui.common.HDImage
import com.yunext.virtuals.ui.screen.devicelist.DeviceListScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
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
                    delay(500)
                    show = true
                    delay(500)
                    navigator.replace(DeviceListScreen())
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedVisibility(show) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hello Kotlin Multiplatform", modifier = Modifier.wrapContentSize())
                    }
                }

                Spacer(Modifier.height(12.dp))
                HDImage(resource = {
                    HDRes.drawable.ic_app
                }, contentDescription = null)
            }
        }
    }
}