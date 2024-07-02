package com.yunext.virtuals.ui.screen.debug

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yunext.kmp.common.logger.HDLogger

//@Composable
//actual fun checkPermission(permission: HDPermission): HDPermissionStatus {
//    val ctx = LocalContext.current
//    return remember(permission) {
//        when (permission) {
//            HDPermission.BluetoothScan -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    val scanPermission = Manifest.permission.BLUETOOTH_SCAN
//                    val granted = ContextCompat.checkSelfPermission(
//                        ctx, scanPermission
//                    ) == PackageManager.PERMISSION_GRANTED
//                    if (granted) {
//                        HDPermissionStatus.Granted
//                    } else {
//                        HDPermissionStatus.DENIED
//                    }
//                } else {
//                    HDPermissionStatus.Granted
//                }
//            }
//
//            HDPermission.BluetoothConnect -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    val curPermission = Manifest.permission.BLUETOOTH_CONNECT
//                    val granted = ContextCompat.checkSelfPermission(
//                        ctx, curPermission
//                    ) == PackageManager.PERMISSION_GRANTED
//                    if (granted) {
//                        HDPermissionStatus.Granted
//                    } else {
//                        HDPermissionStatus.DENIED
//                    }
//                } else {
//                    HDPermissionStatus.Granted
//                }
//            }
//
//            HDPermission.BluetoothAdvertise -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    val curPermission = Manifest.permission.BLUETOOTH_ADVERTISE
//                    val granted = ContextCompat.checkSelfPermission(
//                        ctx, curPermission
//                    ) == PackageManager.PERMISSION_GRANTED
//                    if (granted) {
//                        HDPermissionStatus.Granted
//                    } else {
//                        HDPermissionStatus.DENIED
//                    }
//                } else {
//                    HDPermissionStatus.Granted
//                }
//            }
//        }
//    }
//
//}
//
//@Composable
//actual fun requestPermission(
//    permission: HDPermission,
//    key: Any?,
//    onHDPermission: (Boolean) -> Unit,
//) {
//    LaunchedEffect(key) {
//        HDLogger.d("requestPermission", "requestPermission $permission - $key")
//    }
//    val onHDPermissionWrapper by rememberUpdatedState(onHDPermission)
//    val requestBlock: @Composable (String) -> Unit = {
//
//        val launcher =
//            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted: Boolean ->
//                HDLogger.d(
//                    "requestPermission",
//                    "rememberLauncherForActivityResult : $permission -> $granted"
//                )
//                onHDPermissionWrapper(granted)
//            }
//        LaunchedEffect(permission, key) {
//            key ?: return@LaunchedEffect
//            launcher.launch(it)
//        }
//    }
//    val ctx = LocalContext.current
//    when (permission) {
//        HDPermission.BluetoothScan -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                val scanPermission = Manifest.permission.BLUETOOTH_SCAN
//                if (ContextCompat.checkSelfPermission(
//                        ctx, scanPermission
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    // ignore Build.VERSION.SDK_INT < Build.VERSION_CODES.S
//                } else {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(
//                            ctx as Activity,
//                            scanPermission
//                        )
//                    ) {
//                        Text("在设置里打开蓝牙搜索权限")
//                    } else {
//                        requestBlock(scanPermission)
//                    }
//                }
//            } else {
//                // ignore Granted
//            }
//        }
//
//        HDPermission.BluetoothConnect -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val curPermission = Manifest.permission.BLUETOOTH_CONNECT
//            if (ContextCompat.checkSelfPermission(
//                    ctx, curPermission
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                // ignore Build.VERSION.SDK_INT < Build.VERSION_CODES.S
//            } else {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        ctx as Activity,
//                        curPermission
//                    )
//                ) {
//                    Text("在设置里打开蓝牙连接权限")
//                } else {
//                    requestBlock(curPermission)
//                }
//            }
//        } else {
//            // ignore Granted
//        }
//
//        HDPermission.BluetoothAdvertise -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val curPermission = Manifest.permission.BLUETOOTH_ADVERTISE
//            if (ContextCompat.checkSelfPermission(
//                    ctx, curPermission
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                // ignore Build.VERSION.SDK_INT < Build.VERSION_CODES.S
//            } else {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        ctx as Activity,
//                        curPermission
//                    )
//                ) {
//                    Text("在设置里打开蓝牙广播权限")
//                } else {
//                    requestBlock(curPermission)
//                }
//            }
//        } else {
//            // ignore Granted
//        }
//    }
//}