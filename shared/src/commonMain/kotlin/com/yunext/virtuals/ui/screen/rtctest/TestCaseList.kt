package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.yunext.kmp.ui.compose.hdBackground

@Composable
internal fun TestCaseList(modifier: Modifier, list: List<TestCaseEffect>) {
    val rememberLazyListState = rememberLazyListState()
    LaunchedEffect(list) {
        if (list.isNotEmpty()) {
            rememberLazyListState.scrollToItem(list.size - 1)
        }
    }
    LazyColumn(modifier = modifier,state = rememberLazyListState) {
        items(list, {
            it.toString()
        }) {
            TestCaseItem(it)
        }
    }
}

@Composable
private fun TestCaseItem(effect: TestCaseEffect) {
    Text(
        effect.show, color = when (effect) {
            is TestCaseEffect.Fail -> Color.Red
            TestCaseEffect.Idle -> Color.Black
            is TestCaseEffect.Progress -> Color.Black
            is TestCaseEffect.Start -> Color.Black
            is TestCaseEffect.Success -> Color.Green
        }, fontSize = 11.sp, fontWeight = FontWeight.Light,
        modifier = Modifier.hdBackground { Color.LightGray }
    )
}