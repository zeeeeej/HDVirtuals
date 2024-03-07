package com.yunext.virtuals.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.yunext.kmp.common.util.hdMD5
import com.yunext.kmp.common.util.hdRandomString
import kotlinx.coroutines.launch

@Composable
fun DemoScreen() {
    Column(Modifier.height(256.dp).fillMaxWidth().drawBehind {
        drawRect(Color.Black)
    }) {
        var text by remember { mutableStateOf("DemoScreen") }
        val coroutineScope = rememberCoroutineScope()
        Text(
            text,
            style = TextStyle(color = Color.White),
            modifier = Modifier.padding(12.dp, 8.dp).weight(1f)
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(onClick = {
                coroutineScope.launch {
                    text = "md:${hdMD5(text)} ,random:${hdRandomString(4)}"
                }
            }) {
                Text("hdcommon")
            }
            Button(onClick = {
                coroutineScope.launch {
                    text = "md:${hdMD5(text)} ,random:${hdRandomString(4)}"
                }
            }) {
                Text("DemoScreen")
            }
            Button(onClick = {
                coroutineScope.launch {
                    text = "md:${hdMD5(text)} ,random:${hdRandomString(4)}"
                }
            }) {
                Text("DemoScreen")
            }
            Button(onClick = {
                coroutineScope.launch {
                    text = "md:${hdMD5(text)} ,random:${hdRandomString(4)}"
                }
            }) {
                Text("DemoScreen")
            }
        }

    }
}