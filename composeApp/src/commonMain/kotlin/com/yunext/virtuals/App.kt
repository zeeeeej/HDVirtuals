package com.yunext.virtuals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.bridge.OrientationType
import com.yunext.virtuals.bridge.changeKeyBoardType
import com.yunext.virtuals.bridge.orientationTypeStateFlow
import com.yunext.virtuals.bridge.text
import com.yunext.virtuals.ui.initHDRes
import com.yunext.virtuals.ui.screen.VoyagerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val initApp: CoroutineScope.() -> Unit = {
    launch {
        HDLogger.debug = true
        initHDRes(HDResProviderImpl)
    }
}

@Composable
fun App() {

    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//
//            DemoScreen()
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                    //Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Image(painterResource(DrawableResource("compose-multiplatform.xml")), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }
//        VoyagerDemoApp()
//        SplashScreen()
        var hasInit by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(Unit) {
            initApp()
            hasInit = true
        }
        AnimatedVisibility(hasInit) {
            VoyagerApp()
        }

        val orientationType by orientationTypeStateFlow.collectAsState()
        var curOrientationType: OrientationType by remember { mutableStateOf(OrientationType.Port) }

        LaunchedEffect(orientationType) {
            curOrientationType = orientationType
        }

        Column(modifier = Modifier.padding(start = 100.dp, top = 50.dp)) {

            Button(onClick = {
                changeKeyBoardType(
                    when (curOrientationType) {
                        OrientationType.Port -> OrientationType.Land
                        OrientationType.Land -> OrientationType.Port
                    }, true
                )
            }, modifier = Modifier) {
                Text("->${curOrientationType.text}")
            }
        }


    }
}

