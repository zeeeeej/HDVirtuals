package com.yunext.virtuals.bridge

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 切换横竖屏
 * @param changeTo 目标
 * @param isFromUser 是否由用户主动发起
 */
actual fun changeKeyBoardType(
    changeTo: OrientationType,
    isFromUser: Boolean,
) {
    curOrientationTypeInternal.value = changeTo
}


private var curOrientationTypeInternal: MutableStateFlow<OrientationType> = MutableStateFlow(OrientationType.Port)
internal val curOrientationType: StateFlow<OrientationType>
    get() = curOrientationTypeInternal.asStateFlow()