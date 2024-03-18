package com.yunext.virtuals

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.bridge.OrientationType
import com.yunext.virtuals.bridge.updateOrientationTypeFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        val windowManager = getSystemService(Context.WINDOW_SERVICE) as
//                WindowManager
//        val rotation = windowManager.defaultDisplay?.rotation
//        HDLogger.d("Bridges", "onConfigurationChanged rotation=${rotation}")
//        val type = when (rotation) {
//            Surface.ROTATION_0, Surface.ROTATION_180 -> OrientationType.Port
//            Surface.ROTATION_90, Surface.ROTATION_270 -> OrientationType.Land
//            else -> error("wrong rotation $rotation")
//        }
        HDLogger.d("Bridges", "onConfigurationChanged orientation=${newConfig.orientation}")
        val type =
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) OrientationType.Land
            else OrientationType.Port
        updateOrientationTypeFlow(type)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}