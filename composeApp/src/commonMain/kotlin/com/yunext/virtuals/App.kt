package com.yunext.virtuals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.ui.initHDRes
import com.yunext.virtuals.ui.screen.VoyagerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val initApp:CoroutineScope.()->Unit = {
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
    }
}