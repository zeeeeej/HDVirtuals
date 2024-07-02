package com.yunext.virtuals.ui.demo.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.Activity
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.hdContext

interface PermissionState {
    val list: List<PermissionData>

    suspend fun request(xPermission: XPermission): XPermissionStatus

    fun check(context: HDContext, permission: XPermission)

    fun update(permission: XPermission, status: XPermissionStatus)
}

@Composable
fun rememberPermissionState(list: List<XPermission>): PermissionState {
    return remember(list) {
        PermissionListStateImpl(list)
    }
}

data class PermissionData(val permission: XPermission, val status: XPermissionStatus)

fun List<PermissionData>.anyDenied() = this.any {
    it.status == XPermissionStatus.DENIED
}

private class PermissionListStateImpl(list: List<XPermission>) : PermissionState {
    private var map: MutableMap<XPermission, XPermissionStatus> by mutableStateOf(list.associate {
        (it to XPermissionStatus.DENIED)
    }.toMutableMap())

    private var _list by mutableStateOf<List<PermissionData>>(emptyList())
    override val list: List<PermissionData>
        get() = _list

    private fun notifyList() {
        _list = map.map { (k, v) -> PermissionData(k, v) }
    }

    override suspend fun request(xPermission: XPermission): XPermissionStatus {
        TODO()
    }

    init {
        list.forEach {
            check(hdContext, it)
        }
    }

    override fun check(context: HDContext, permission: XPermission) {
        val status = checkPermission(context, permission)
        val newMap = map
        newMap[permission] = status
        map = newMap
        notifyList()
    }

    override fun update(permission: XPermission, status: XPermissionStatus) {
        HDLogger.d("PermissionRequestView", "[update]permission:$permission status:$status")
        val newMap = map
        newMap[permission] = status
        map = newMap
        notifyList()
    }
}

expect fun checkPermission(context: HDContext, permission: XPermission): XPermissionStatus

@Composable
expect fun PermissionRequestView(
    permission: XPermission, key: Any?, onStatusChanged: (XPermissionStatus) -> Unit,
    shouldShowRequestPermissionRationale: () -> Unit = {},
)

expect suspend fun requestPermission(
    activity: Activity,
    xPermission: XPermission,
): XPermissionStatus