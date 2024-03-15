package com.yunext.virtuals.ui.data

import com.yunext.virtuals.ui.HDRes
import com.yunext.virtuals.ui.common.DrawableResourceFactory
import com.yunext.virtuals.ui.hdRes
import org.jetbrains.compose.resources.ExperimentalResourceApi

enum class MenuData(val icon: String, val text: String) {
    ConfigWiFi("icon_twins_distribution_network.png", "进入配网模式"),
    Setting("icon_twins_set_up.png", "配置"),
    Logger("icon_twins_log.png", "通信日志"),
    UpdateTsl("icon_twins_refresh.png", "检查更新物模型")
    ;
}

@OptIn(ExperimentalResourceApi::class)
val MenuData.iconRes: DrawableResourceFactory
    get() = when (this) {
        MenuData.ConfigWiFi -> {
            { hdRes { HDRes.drawable.icon_twins_distribution_network } }
        }

        MenuData.Setting -> {
            { hdRes { HDRes.drawable.icon_twins_set_up } }
        }

        MenuData.Logger -> {
            { hdRes { HDRes.drawable.icon_twins_log } }
        }

        MenuData.UpdateTsl -> {
            { hdRes { HDRes.drawable.icon_twins_refresh } }
        }
    }