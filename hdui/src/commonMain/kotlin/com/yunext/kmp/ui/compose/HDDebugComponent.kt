package com.yunext.kmp.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Deprecated("废弃，使用fun Debug(tag: String = TAG, debug: Boolean = DEBUG, msg: () -> String)",
    ReplaceWith(
        "Debug(tag=tag, debug=debug){msg}"
))
@Composable
fun Debug(msg: String, tag: String = TAG, debug: Boolean = DEBUG) {
    if (debug) {
        SideEffect {
            println("_${tag}_ $msg")
        }
    }
}

@Composable
fun Debug(tag: String = TAG, debug: Boolean = DEBUG, msg: () -> String) {
    if (debug) {
        SideEffect {
            println("_${tag}_ $msg")
        }
    }
}

private const val TAG = "xpl"
private const val DEBUG = true