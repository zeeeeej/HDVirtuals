package com.yunext.virtuals.ui.demo.voyager.nestedNavigation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import com.yunext.kmp.resource.color.China
import com.yunext.virtuals.ui.demo.voyager.basicNavigationScreen.BasicNavigationScreen


@Composable
fun NestedNavigationScreen() {
    NestedNavigation(backgroundColor = China.g_zhu_lv) {
        CurrentScreen()
        NestedNavigation(backgroundColor = China.r_luo_xia_hong) {
            CurrentScreen()
            NestedNavigation(backgroundColor = China.h_gu_huang) { navigator ->
                CurrentScreen()
                Button(
                    onClick = { navigator.popUntilRoot() },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(text = "Pop Until Root")
                }
            }
        }
    }
}

@Composable
private fun NestedNavigation(
    backgroundColor: Color,
    content: NavigatorContent = { CurrentScreen() },
) {
    Navigator(
        screen = BasicNavigationScreen(index = 0, wrapContent = true)
    ) { navigator ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .background(backgroundColor)
        ) {
            Text(
                text = "Level #${navigator.level}",
                modifier = Modifier.padding(8.dp)
            )
            content(navigator)
        }
    }
}
