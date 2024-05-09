package com.yunext.virtuals.ui.common


import android.app.Activity
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
actual fun SystemBarsPadding(content: @Composable () -> Unit) {
    SystemBarsPaddingInternal (content)
}

@Composable
private fun SystemBarsPaddingInternal(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val imePadding = remember { mutableStateOf(0.dp) }

    DisposableEffect(context) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            val rect = android.graphics.Rect()
            val view = (context as? Activity)?.window?.decorView
            view?.getWindowVisibleDisplayFrame(rect)

            val heightDiff = view?.height?.minus(rect.bottom)?.div(density) ?: 0f
            imePadding.value = if (heightDiff > 100) heightDiff.dp else 0.dp

            true
        }

        (context as? Activity)?.window?.decorView?.viewTreeObserver?.addOnPreDrawListener(listener)

        onDispose {
            (context as? Activity)?.window?.decorView?.viewTreeObserver?.removeOnPreDrawListener(
                listener
            )
        }
    }

    Box(
        modifier = Modifier
            .padding(bottom = imePadding.value)
            .fillMaxWidth()
    ) {
        content()
    }
}