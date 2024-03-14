package com.yunext.virtuals.ui.common.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * todo 点击事件的问题
 */
@Composable
internal fun XPopContainer(
    contentAlignment: Alignment = Alignment.TopStart,
    dimAmount:Float=.5f,
    onDismiss: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = dimAmount))
            .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                onDismiss()
            }, contentAlignment = contentAlignment
    ) {
        content()
    }
}