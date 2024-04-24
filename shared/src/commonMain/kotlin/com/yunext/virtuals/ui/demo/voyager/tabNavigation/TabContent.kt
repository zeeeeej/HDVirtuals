package com.yunext.virtuals.ui.demo.voyager.tabNavigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.transitions.SlideTransition
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.ui.demo.voyager.basicNavigationScreen.BasicNavigationScreen

@Composable
fun Tab.TabContent(msg:String,onMsgChanged:(String)->Unit = {}) {
    val tabTitle = options.title

    LifecycleEffect(
        onStarted = { HDLogger.d("Navigator", "Start tab $tabTitle") },
        onDisposed = { HDLogger.d("Navigator", "Dispose tab $tabTitle") }
    )

    Navigator(BasicNavigationScreen(index = 0)) { navigator ->
        SlideTransition(navigator) { screen ->
            Column {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    TabNavigationButton(HomeTab)

                    Spacer(modifier = Modifier.weight(.05f))

                    TabNavigationButton(FavoritesTab(msg,onMsgChanged))

                    Spacer(modifier = Modifier.weight(.05f))

                    TabNavigationButton(ProfileTab)
                }
                screen.Content()
                //HDLogger.d("Navigator", "Last Event: ${navigator.lastEvent}")
            }
        }
    }
}

@Composable
private fun InnerTabNavigation() {

}

@Composable
private fun RowScope.TabNavigationButton(
    tab: Tab,
) {
    val tabNavigator = LocalTabNavigator.current

    Button(
        enabled = tabNavigator.current.key != tab.key,
        onClick = { tabNavigator.current = tab },
        modifier = Modifier.weight(1f)
    ) {
        Text(text = tab.options.title)
    }
}