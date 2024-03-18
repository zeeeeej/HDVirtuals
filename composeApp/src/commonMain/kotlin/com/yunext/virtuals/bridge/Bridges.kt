package com.yunext.virtuals.bridge

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 切换横竖屏
 * @param changeTo 目标
 * @param isFromUser 是否由用户主动发起
 */
expect fun changeKeyBoardType(changeTo: OrientationType, isFromUser: Boolean)

/**
 * 当前横竖屏状态
 */
val orientationTypeStateFlow: StateFlow<OrientationType>
    get() = orientationTypeFlowInternal.asStateFlow()

private const val PORT = 0
private const val LAND = 1

enum class OrientationType(val type: Int) {
    Port(PORT), Land(LAND);
}

val OrientationType.text: String
    get() = when (this) {
        OrientationType.Port -> "竖屏"
        OrientationType.Land -> "横屏"
    }

//
private val orientationTypeFlowInternal: MutableStateFlow<OrientationType> =
    MutableStateFlow(OrientationType.Port)

internal fun updateOrientationTypeFlow(orientationType: OrientationType) {
    orientationTypeFlowInternal.value = orientationType
}


