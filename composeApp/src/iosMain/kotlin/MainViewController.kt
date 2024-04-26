import androidx.compose.ui.window.ComposeUIViewController
import com.yunext.virtuals.App
import com.yunext.virtuals.theme.HDTheme

fun MainViewController() = ComposeUIViewController {
    HDTheme {
        App()
    }

}