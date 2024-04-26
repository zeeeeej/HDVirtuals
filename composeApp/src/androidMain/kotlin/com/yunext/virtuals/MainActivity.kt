package com.yunext.virtuals

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.bridge.OrientationType
import com.yunext.virtuals.bridge.updateOrientationTypeFlow
import com.yunext.virtuals.theme.HDThemeInternal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

//        private val launcher =
//        this.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            println("isGranted:$isGranted")
//            if (isGranted) {
//                //onPermissionStateChanged(PermissionState.GRANTED)
//
//            } else {
////                if (shouldShowRequestPermissionRationale(permission)) {
////                    onPermissionStateChanged(PermissionState.PROHIBITED)
////                } else {
////                    onPermissionStateChanged(PermissionState.DENIED)
////                }
//            }
//        }

    override fun onStart() {
        super.onStart()

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HDThemeInternal{
                App()
            }
        }
//        launcher.launch(   android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                delay(1000)
//                val requestPermission =
//                    requestPermission(
//                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
////                        this@MainActivity,
//                        shouldShowRequestPermissionRationale = { true },
//
//                    )
//                println("requestPermission result : $requestPermission")
//            }
//        }


//        lifecycleScope.launch {
//            delay(1000)
//            val requestPermission =
//                requestPermission2(
//                    this@MainActivity,
//                    shouldShowRequestPermissionRationale = { true },
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//            println("requestPermission result : $requestPermission")
//        }

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

    fun Activity.hideSystemUI() {
        hideSystemUIInternal()
    }

    private fun Activity.hideSystemUIInternal() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private fun Activity.showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}