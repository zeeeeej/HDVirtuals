package com.yunext.virtuals.ui.screen.logger.data

import com.yunext.kmp.db.datasource.LogDatasource

internal enum class UIType {
    ALl, Fail, Success, Online, Up, Down;
}

internal val UIType.title: String
    get() = when (this) {
        UIType.ALl -> "全部"
        UIType.Fail -> "失败"
        UIType.Success -> "成功"
        UIType.Online -> "上/下线"
        UIType.Up -> "发出"
        UIType.Down -> "收到"
    }


internal fun UIType.toSign() = when (this) {
    UIType.ALl -> LogDatasource.Sign.ALL
    UIType.Fail -> LogDatasource.Sign.FAIL
    UIType.Success -> LogDatasource.Sign.SUCCESS
    UIType.Online -> LogDatasource.Sign.ONLINE
    UIType.Up -> LogDatasource.Sign.UP
    UIType.Down -> LogDatasource.Sign.DOWN
}

internal fun LogDatasource.Sign.toUIType() = when (this) {
    LogDatasource.Sign.ALL -> UIType.ALl
    LogDatasource.Sign.FAIL -> UIType.Fail
    LogDatasource.Sign.SUCCESS -> UIType.Success
    LogDatasource.Sign.ONLINE -> UIType.Online
    LogDatasource.Sign.UP -> UIType.Up
    LogDatasource.Sign.DOWN -> UIType.Down
}