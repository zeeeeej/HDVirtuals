package com.yunext.virtuals.ui.demo.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.Activity
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.application
import com.yunext.kmp.context.hdContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

actual fun checkPermission(
    context: HDContext,
    permission: XPermission,
): XPermissionStatus {
    val ctx = context.application
    return when (permission) {
        XPermission.BluetoothScan -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val scanPermission = Manifest.permission.BLUETOOTH_SCAN
                val granted = ContextCompat.checkSelfPermission(
                    ctx, scanPermission
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    XPermissionStatus.Granted
                } else {
                    XPermissionStatus.DENIED
                }
            } else {
                XPermissionStatus.Granted
            }
        }

        XPermission.BluetoothConnect -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val scanPermission = Manifest.permission.BLUETOOTH_CONNECT
                val granted = ContextCompat.checkSelfPermission(
                    ctx, scanPermission
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    XPermissionStatus.Granted
                } else {
                    XPermissionStatus.DENIED
                }
            } else {
                XPermissionStatus.Granted
            }
        }

        XPermission.BluetoothAdvertise -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val scanPermission = Manifest.permission.BLUETOOTH_ADVERTISE
                val granted = ContextCompat.checkSelfPermission(
                    ctx, scanPermission
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    XPermissionStatus.Granted
                } else {
                    XPermissionStatus.DENIED
                }
            } else {
                XPermissionStatus.Granted
            }
        }
    }
}


@Composable
actual fun PermissionRequestView(
    permission: XPermission,
    key: Any?,
    onStatusChanged: (XPermissionStatus) -> Unit,
    shouldShowRequestPermissionRationale: () -> Unit,
) {
//    LaunchedEffect(permission, key) {
//        HDLogger.d("PermissionRequestView", "requestPermission $permission - $key")
//    }
    val onStatusChangedUpdateState by rememberUpdatedState(onStatusChanged)
    val shouldShowRequestPermissionRationaleUpdateState by rememberUpdatedState(
        shouldShowRequestPermissionRationale
    )
    //<editor-fold desc="方式一 使用rememberLauncherForActivityResult">
    val requestBlock: @Composable (String) -> Unit = {

        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted: Boolean ->
                HDLogger.d(
                    "PermissionRequestView",
                    "请求权限结果: $permission -> $granted"
                )
                onStatusChangedUpdateState(if (granted) XPermissionStatus.Granted else XPermissionStatus.DENIED)
            }
        LaunchedEffect(permission, key) {
            key ?: return@LaunchedEffect
            launcher.launch(it)
            HDLogger.i("PermissionRequestView", "开始请求权限:$permission")

        }
    }

    val ctx = LocalContext.current
    when (permission) {
        XPermission.BluetoothScan -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val scanPermission = Manifest.permission.BLUETOOTH_SCAN
                if (ContextCompat.checkSelfPermission(
                        ctx, scanPermission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // ignore Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            ctx as Activity,
                            scanPermission
                        )
                    ) {
                        shouldShowRequestPermissionRationaleUpdateState()
                    } else {
                        requestBlock(scanPermission)
                    }
                }
            } else {
                // ignore Granted
            }
        }

        XPermission.BluetoothConnect -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val curPermission = Manifest.permission.BLUETOOTH_CONNECT
            if (ContextCompat.checkSelfPermission(
                    ctx, curPermission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // ignore Build.VERSION.SDK_INT < Build.VERSION_CODES.S
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        ctx as Activity,
                        curPermission
                    )
                ) {
                    shouldShowRequestPermissionRationaleUpdateState()
                } else {
                    requestBlock(curPermission)
                }
            }
        } else {
            // ignore Granted
        }

        XPermission.BluetoothAdvertise -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val curPermission = Manifest.permission.BLUETOOTH_ADVERTISE
            if (ContextCompat.checkSelfPermission(
                    ctx, curPermission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // ignore Build.VERSION.SDK_INT < Build.VERSION_CODES.S
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        ctx as Activity,
                        curPermission
                    )
                ) {
                    shouldShowRequestPermissionRationaleUpdateState()
                } else {
                    requestBlock(curPermission)
                }
            }
        } else {
            // ignore Granted
        }
    }
    //</editor-fold>

    //<editor-fold desc="方式二 主动请求">
//    val coroutineScope = rememberCoroutineScope()
//    Button(onClick = {
//        HDLogger.w("PermissionRequestView","requestPermission开始：$permission")
//        coroutineScope.launch {
//            val requestPermission =
//                requestPermission(ctx as Activity, permission)
//            HDLogger.w("PermissionRequestView","requestPermission结束:$requestPermission")
//            onStatusChanged(requestPermission)
//        }
//    }){
//        Text("申请权限")
//    }
    //</editor-fold>
}

val XPermission.androidPermission: String?
    get() = when (this) {
        XPermission.BluetoothScan -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_SCAN
        } else {
            null
        }

        XPermission.BluetoothConnect -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_CONNECT
        } else {
            null
        }

        XPermission.BluetoothAdvertise -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_ADVERTISE
        } else {
            null
        }
    }

actual suspend fun requestPermission(
    activity: Activity,
    xPermission: XPermission,
): XPermissionStatus {
    return coroutineScope {
        val androidPermission =
            xPermission.androidPermission ?: return@coroutineScope XPermissionStatus.Granted
        ActivityCompat.requestPermissions(activity, arrayOf(androidPermission), 0x01)
        var index = 0
        while (isActive && index < 10) {
            delay(1000)
            index++
            val result = checkPermission(hdContext/*TODO*/, xPermission)
            if (result == XPermissionStatus.Granted) {
                return@coroutineScope XPermissionStatus.Granted
            }
        }
        XPermissionStatus.Granted
    }


}


