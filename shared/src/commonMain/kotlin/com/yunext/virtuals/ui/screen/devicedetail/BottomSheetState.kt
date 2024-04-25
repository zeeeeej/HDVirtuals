package com.yunext.virtuals.ui.screen.devicedetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy

@Stable
interface BottomSheetState {
    var title: String
    var msg: String
    var desc: String
}


internal fun BottomSheetState(): BottomSheetState = BottomSheetStateImpl()

@Composable
internal fun rememberBottomSheetState(): BottomSheetState {
    return remember { BottomSheetState() }
}

private class BottomSheetStateImpl(
    title: String = "",
    msg: String = "",
    desc: String = "",
) : BottomSheetState {
    override var title: String
        get() = _title
        set(value) {
            _title = value
        }

    override var msg: String
        get() = _msg
        set(value) {
            _msg = value
        }
    override var desc: String
        get() = _desc
        set(value) {
            _desc = value
        }

    private var _title by mutableStateOf(title, structuralEqualityPolicy())
    private var _msg by mutableStateOf(msg, structuralEqualityPolicy())
    private var _desc by mutableStateOf(desc, structuralEqualityPolicy())

}

