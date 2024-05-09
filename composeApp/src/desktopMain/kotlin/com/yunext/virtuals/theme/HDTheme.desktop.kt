package com.yunext.virtuals.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun HDTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}