package com.yunext.kmp.common.util

import kotlinx.datetime.Clock

fun currentTime() = Clock.System.now().toEpochMilliseconds()