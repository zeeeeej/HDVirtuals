package com.yunext.virtuals

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.bridge.OrientationType
import com.yunext.virtuals.bridge.updateOrientationTypeFlow
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

        setContent {
            App()
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
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}