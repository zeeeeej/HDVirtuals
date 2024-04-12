package com.yunext.virtuals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.resource.initHDRes
import com.yunext.virtuals.bridge.OrientationType
import com.yunext.virtuals.bridge.changeKeyBoardType
import com.yunext.virtuals.bridge.orientationTypeStateFlow
import com.yunext.virtuals.bridge.text
import com.yunext.virtuals.module.devicemanager.initDeviceManager
import com.yunext.virtuals.ui.screen.DemoScreen
import com.yunext.virtuals.ui.screen.VoyagerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val initApp: CoroutineScope.() -> Unit = {
    launch {
        HDLogger.debug = true
        initHDRes(HDResProviderImpl)
        initDeviceManager()
    }
}


//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun Test() {
//    val coroutineScope = rememberCoroutineScope()
//    val state = rememberPagerState() {
//        3
//    }
//    A(state)
//    B() {
//        coroutineScope.launch {
//            state.animateScrollToPage(1)
//        }
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun A(state: PagerState) {
//    val coroutineScope = rememberCoroutineScope()
//    TestBox {
//        coroutineScope.launch {
//            state.animateScrollToPage(1)
//        }
//    }
//}
//
//@Composable
//fun B(onClick: () -> Unit) {
//    TestBox(onClick = onClick)
//}
//
//@Composable
//fun TestBox(modifier: Modifier = Modifier, onClick: () -> Unit) {
//    val coroutineScope = rememberCoroutineScope()
//    Box(modifier = modifier.clickable {
//        coroutineScope.launch {
//            onClick()
//        }
//    }) {
//        Text("测试")
//    }
//}

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

        Column(
            modifier = Modifier.padding(start = 100.dp, top = 50.dp).verticalScroll(
                rememberScrollState()
            )
        ) {

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

            var showDebug  by remember { mutableStateOf(false) }
            Button(onClick = {
                showDebug = !showDebug

            }, modifier = Modifier) {
                Text("测试")
            }

            if (showDebug){
                DemoScreen()
            }


        }


    }
}

