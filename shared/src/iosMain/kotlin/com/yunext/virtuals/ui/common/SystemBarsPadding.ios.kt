package com.yunext.virtuals.ui.common

import androidx.compose.runtime.Composable

@Composable
actual fun SystemBarsPadding(content: @Composable () -> Unit) {
    content()
}