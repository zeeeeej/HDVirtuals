import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.App
import com.yunext.virtuals.bridge.OrientationType
import com.yunext.virtuals.bridge.curOrientationType
import com.yunext.virtuals.bridge.updateOrientationTypeFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private val port_size = DpSize(600.dp, 800.dp)
private val land_size = DpSize(800.dp, 600.dp)

fun main() = application {
    val type by curOrientationType.collectAsState()
    val state = rememberWindowState(
        size = when (type) {
            OrientationType.Port -> port_size
            OrientationType.Land -> land_size
        },
        position = WindowPosition(Alignment.BottomCenter)
    )
    LaunchedEffect(Unit) {
        snapshotFlow {
            type
        }.onEach {
            HDLogger.d("bridges", "snapshotFlow:$it")
            updateOrientationTypeFlow(it)
            state.size = when (type) {
                OrientationType.Port -> port_size
                OrientationType.Land -> land_size
            }
        }.launchIn(this)
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = "HDVirtuals",
//        icon = painterResource("icon.png"),
        alwaysOnTop = true,
        onKeyEvent = {
//            if (isKeyTyped(it)) {
//                val btnIndex = asciiCode2BtnIndex(it.utf16CodePoint)
//                if (btnIndex != -1) {
//                    if (Config.boardType.value == KeyboardTypeStandard) {
//                        standardChannel.trySend(StandardAction.ClickBtn(btnIndex))
//                    }
//                    else {
//                        programmerChannel.trySend(ProgrammerAction.ClickBtn(btnIndex))
//                    }
//                }
//            }
            true
        }
    ) {
        MaterialTheme {
            App()
        }
    }
}



