package com.yunext.virtuals.ui.common

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class StableValue<out T : Any> internal constructor(val value: T)

internal fun <T : Any> stableValueOf(value: T) = value.stable()
internal fun <T : Any> T.stable() = StableValue(this)