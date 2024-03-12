import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.yunext.virtuals.ui.screen.VoyagerApp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
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
        VoyagerApp()
    }
}