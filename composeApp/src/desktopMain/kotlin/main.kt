import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yunext.virtuals.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "HDVirtuals") {
        App()
    }
}