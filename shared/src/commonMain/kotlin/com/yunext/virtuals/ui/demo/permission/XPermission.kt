package com.yunext.virtuals.ui.demo.permission

enum class XPermission {
    BluetoothScan,
    BluetoothConnect,
    BluetoothAdvertise,
    ;
}

enum class XPermissionStatus {
    Granted,
    DENIED
    ;
}

val XPermission.text: String
    get() = when (this) {
        XPermission.BluetoothScan -> "蓝牙扫描权限"
        XPermission.BluetoothConnect -> "蓝牙连接权限"
        XPermission.BluetoothAdvertise -> "蓝牙广播权限"
    }




