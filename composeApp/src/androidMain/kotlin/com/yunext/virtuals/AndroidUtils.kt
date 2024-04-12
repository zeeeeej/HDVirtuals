package com.yunext.virtuals

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.yunext.kmp.common.util.hdUUID
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.Permission
import kotlin.coroutines.resume

enum class PermissionState {
    GRANTED,
    DENIED,
    PROHIBITED
    ;
}

//
//interface DelegatePermission {
//    fun request()
//}
//
//class DelegatePermissionImpl(
//    private val componentActivity: ComponentActivity,
//    private val permission: String,
//    private val shouldShowRequestPermissionRationale: (String) -> Boolean = { false },
//    private val onPermissionStateChanged: (PermissionState) -> Unit,
//) : DelegatePermission {
//    private val launcher =
//        componentActivity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                onPermissionStateChanged(PermissionState.GRANTED)
//
//            } else {
//                if (shouldShowRequestPermissionRationale(permission)) {
//                    onPermissionStateChanged(PermissionState.PROHIBITED)
//                } else {
//                    onPermissionStateChanged(PermissionState.DENIED)
//                }
//            }
//        }
//
//    override fun request() {
//        launcher.launch(permission)
//    }
//
//
//}
//
suspend fun ComponentActivity.requestPermission(
    permission: String,
    shouldShowRequestPermissionRationale: (String) -> Boolean = { false },
): PermissionState {
    return suspendCancellableCoroutine { con ->
        var launcher: ActivityResultLauncher<String>? = null


        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        launcher =
                            this@requestPermission.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                                if (isGranted) {
                                    con.resume(PermissionState.GRANTED)
                                } else {
                                    if (shouldShowRequestPermissionRationale(permission)) {
                                        con.resume(PermissionState.PROHIBITED)
                                    } else {
                                        con.resume(PermissionState.DENIED)
                                    }
                                }
                            }
                    }

                    Lifecycle.Event.ON_START -> {


                    }

                    Lifecycle.Event.ON_RESUME -> {}
                    Lifecycle.Event.ON_PAUSE -> {}
                    Lifecycle.Event.ON_STOP -> {

                        launcher?.unregister()
                        launcher = null
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        //this@requestPermission.lifecycle.removeObserver(this)
                    }

                    Lifecycle.Event.ON_ANY -> {}
                }
            }


        }

        this.lifecycle.addObserver(observer)


        con.invokeOnCancellation {
            this.lifecycle.removeObserver(observer)
//            launcher.unregister()
        }

        launcher?.launch(permission)


    }
}


suspend fun requestPermission2(
    registry: ComponentActivity,
    shouldShowRequestPermissionRationale: (String) -> Boolean,
    permission: String,
): PermissionState = suspendCancellableCoroutine { continuation ->
    val launcher =
        registry.activityResultRegistry.register(
            hdUUID(10),
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                continuation.resume(PermissionState.GRANTED)
            } else {
                if (shouldShowRequestPermissionRationale(permission)) {
                    continuation.resume(PermissionState.PROHIBITED)
                } else {
                    continuation.resume(PermissionState.DENIED)
                }
            }
        }

    launcher.launch(permission)

    val observer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    // launcher.launch(permission)
                }

                Lifecycle.Event.ON_START -> {


                }

                Lifecycle.Event.ON_RESUME -> {}
                Lifecycle.Event.ON_PAUSE -> {}
                Lifecycle.Event.ON_STOP -> {}
                Lifecycle.Event.ON_DESTROY -> {
                    //registry.lifecycle.removeObserver(this)
                    //launcher.unregister()
                }

                Lifecycle.Event.ON_ANY -> {}
            }
        }


    }

    registry.lifecycle.addObserver(observer)


    continuation.invokeOnCancellation {
        registry.lifecycle.removeObserver(observer)
    }
}
