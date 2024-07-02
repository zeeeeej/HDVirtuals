package com.yunext.virtuals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.resource.HDRes
import com.yunext.kmp.resource.initHDRes
import com.yunext.virtuals.bridge.OrientationType
import com.yunext.virtuals.bridge.changeKeyBoardType
import com.yunext.virtuals.bridge.orientationTypeStateFlow
import com.yunext.virtuals.bridge.text
import com.yunext.virtuals.module.devicemanager.initDeviceManager
import com.yunext.virtuals.ui.common.DrawableResourceFactory
import com.yunext.virtuals.ui.common.HDImage
import com.yunext.virtuals.ui.screen.debug.DemoScreen
import com.yunext.virtuals.ui.screen.LocalPaddingValues
import com.yunext.virtuals.ui.screen.SplashPage
import com.yunext.virtuals.ui.screen.VoyagerApp
import hdvirtuals.composeapp.generated.resources.Res
import hdvirtuals.composeapp.generated.resources.ic_app
import hdvirtuals.composeapp.generated.resources.ic_launcher
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

private val initApp: CoroutineScope.() -> Unit = {
    launch {
        Napier.i {
            "[initApp]"
        }
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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {


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
        Napier.w {
            "App::initApp $hasInit"
        }
        HDLogger.d("App", "App::initApp $hasInit")
        if (!hasInit) {
            initApp()
            hasInit = true
        }
    }
    Scaffold { padding ->
        SideEffect {
            Napier.w {
                "App::padding:$padding"
            }
            // LocalPaddingValues.provides(padding)
        }

        /*        AnimatedVisibility(hasInit, modifier = Modifier.padding(padding)
        //            .safeContentPadding()
                ) {
        //            if (hasInit){
                        VoyagerApp()
        //            }
                }*/

        if (hasInit) {
            Box(modifier = Modifier.padding(padding)) { VoyagerApp() }
        } else {
//            Box(Modifier.fillMaxSize().background(Color.Red)) {
//                SplashPage(true) {
//                    Image(
//                        painterResource(Res.drawable.ic_app),
//                        null,
//                    )
//                }
//            }
        }


        //Text("$padding")
    }

    //region App级别测试
    val appDebug by remember { mutableStateOf(false) }
    if (appDebug) {
        val orientationType by orientationTypeStateFlow.collectAsState()
        var curOrientationType: OrientationType by remember { mutableStateOf(OrientationType.Port) }

        LaunchedEffect(orientationType) {
            curOrientationType = orientationType
        }

        Column(
            modifier = Modifier.padding(start = 50.dp, top = 50.dp).verticalScroll(
                rememberScrollState()
            )
        ) {

            // 横竖屏
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

            // 测试
            var showDebug by remember { mutableStateOf(false) }
            Button(onClick = {
                showDebug = !showDebug

            }, modifier = Modifier) {
                Text("测试")
            }

            if (showDebug) {
                DemoScreen()
            }

            // 蓝牙
            var showBle by remember { mutableStateOf(false) }
            Button(onClick = {
                showBle = !showBle

            }, modifier = Modifier) {
                Text("ble")
            }
            if (showBle) {

            }
        }
    }
    //endregion
}



