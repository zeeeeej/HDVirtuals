package com.yunext.virtuals.ui.demo.permission

import androidx.compose.runtime.Composable
import com.yunext.kmp.context.Activity

@Composable
actual fun PermissionRequestView(
    permission: XPermission, key: Any?, onStatusChanged: (XPermissionStatus) -> Unit,
    shouldShowRequestPermissionRationale: () -> Unit,
) {
    TODO("Not yet implemented")
}

actual suspend fun requestPermission(activity: Activity, xPermission: XPermission): XPermissionStatus {
    TODO("Not yet implemented")
}