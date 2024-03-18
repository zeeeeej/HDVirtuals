package com.yunext.kmp.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Composable
fun Debug(msg: String, tag: String = TAG, debug: Boolean = DEBUG) {
    if (debug) {
        SideEffect {
            println("_${tag}_ $msg")
        }
    }
}

private const val TAG = "xpl"
private const val DEBUG = false