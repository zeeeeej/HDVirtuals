package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.dialog.CHDialog
import com.yunext.virtuals.ui.common.dialog.DialogDefaults
import com.yunext.virtuals.ui.common.dialog.debugShape

@Composable
internal fun RTCSelectedDialog(onSelect: (Boolean) -> Unit) {
    CHDialog {
        RTCTestPage(onSelect)
    }
}

@Composable
private fun RTCTestPage(onSelect: (Boolean) -> Unit) {

    Box(
        Modifier
            .width(200.dp).aspectRatio(16 / 9f)//.debugShape(debug = true).clipToBounds()
    ) {

        Column(Modifier.fillMaxSize()) {
            Btn(Modifier.weight(1f).fillMaxWidth().clipToBounds(), "Master") {
                onSelect(true)
            }

            Text(
                "请选择",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )

            Btn(Modifier.weight(1f).fillMaxWidth().clipToBounds(), "Slave") {
                onSelect(false)
            }
        }


    }
}

@Composable
private fun Btn(modifier: Modifier, text: String, onClick: () -> Unit) {
    Box(
        modifier.padding(0.dp)
            .clip(DialogDefaults.DEFAULT_SHAPE)
            .clickable(onClick = onClick).debugShape(debug = true).hdBackground(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text, style = TextStyle.Default.copy(
                fontWeight = FontWeight.Bold, fontSize = 32.sp
            )
        )
    }

}