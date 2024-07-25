package com.yunext.virtuals.ui.screen.debug
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.kmp.mqtt.interop.DebugInterOp
import com.yunext.kmp.mqtt.interop.debugSendByteArrayWrapper
import com.yunext.kmp.mqtt.virtuals.test.MQTTVirtualsDemo
import io.ktor.utils.io.core.toByteArray
private val testHDMQTT2: MQTTVirtualsDemo by lazy { MQTTVirtualsDemo() }
@Composable
internal fun DebugMqttBlock() {
    Column(modifier = Modifier.padding(start = 100.dp, top = 100.dp)) {


        Button(onClick = {
            testHDMQTT2.init()
        }, modifier = Modifier) {
            Text("KMQTT 连接")

        }

        Button(onClick = {
            testHDMQTT2.register()
        }, modifier = Modifier) {
            Text("KMQTT 注册")

        }

        Button(onClick = {
            testHDMQTT2.publish()
        }, modifier = Modifier) {
            Text("KMQTT 发消息")

        }

        Button(onClick = {
            testHDMQTT2.disconnect()
        }, modifier = Modifier) {
            Text("KMQTT 关闭")
        }

        Button(onClick = {
            println("kotlin 传递 byteArray 到 swift 开始")
            val data = "hello ydd".toByteArray()
            DebugInterOp.interOp_Out_ByteArray?.invoke(data)
            println("kotlin 传递 byteArray 到 swift 结束")
        }, modifier = Modifier) {
            Text("kotlin 传递 byteArray 到 swift")
        }

        Button(onClick = {
            println("kotlin 传递 byteArray 到 swift 开始 2")
            val data = "hello ydd".toByteArray()
            debugSendByteArrayWrapper(data)
            println("kotlin 传递 byteArray 到 swift 结束 2")
        }, modifier = Modifier) {
            Text("kotlin 传递 byteArray 到 swift 2")
        }

    }
}