package com.yunext.virtuals.ui.screen.debug

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.common.dialog.CHAlertDialog
import io.ktor.websocket.Frame

@Composable
internal fun DebugDialog() {
    // 弹窗
    Debug("TwinsHomePage-内容-弹窗")
    var show by remember {
        mutableStateOf(false)
    }
    var index by remember {
        mutableStateOf(0)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                index += 1
                show = !show


//                val a = A1("Greeting hello")
//                val a = A3
//                println("Greeting a = $a")
//                val json = Json.encodeToString(A.serializer(), a)
//                println("Greeting json = $json")
//                val r = Json.decodeFromString(A.serializer(), json)
//                println("Greeting r = $r")

//                val abstractContext = SerializersModule {
//                    polymorphic(A::class) {
//                        subclass(A1::class)
//                        subclass(A2::class)
//                        // no need to register ProtocolWithAbstractClass.ErrorMessage
//                    }
//
//                }
//                val jsonER = Json {
//                    serializersModule = abstractContext
//                }
//                val json = jsonER.encodeToString(A3)
//                println("Greeting json = $json")
//                val r = jsonER.decodeFromString(A1.serializer(),json)
//                println("Greeting r = $r")

            },
            modifier = Modifier.Companion
                .align(Alignment.BottomStart)
                .padding(vertical = 16.dp)
        ) {
            Frame.Text(text = "DebugDialog")
        }
        if (show) {
//        if (index % 2 == 0) {
////                CHLoadingDialog(dimAmount = 0.1f) {
////                    show = false
////                }
//            NewsDialog() {
//                show = false
//            }
//        } else {
            //TODO("底部弹窗")
            CHAlertDialog("haha", "天生我才必有用") {
                show = false
            }
//        }

//        AlertDialog(onDismissRequest = { show = false }, buttons = {
//            Text(text = "I am dialog", modifier = Modifier.size(200.dp))
//        })

        }
    }
}