package com.yunext.virtuals.ui.demo.voyager.case01

import HDDebugText
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.yunext.virtuals.ui.data.randomText
import com.yunext.virtuals.ui.demo.voyager.tabNavigation.TabNavigationScreen


class CommonScreenModel : StateScreenModel<String>("hello voyager!") {

    fun change(value: String) {
        this.mutableState.value = value
    }
}

class BottomSheetNavigatorDemo : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
//        BottomSheetNavigator {
//            BottomSheetNavigatorDemoPrivate()
//        }
        val screenModel = rememberScreenModel { CommonScreenModel() }
        val msg by screenModel.state.collectAsState()
        LaunchedEffect(Unit){
            screenModel.change("我是父亲！")
        }
        Column {
            Text("parent msg:$msg")
            TabNavigationScreen(msg){
                screenModel.change(it)
            }
        }
    }

}

@Composable
private fun BottomSheetNavigatorDemoPrivate() {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    Button(onClick = {
        bottomSheetNavigator.show(Tab01DemoScreen(randomText(4)))
    }) {
        HDDebugText("click")
    }

}

class Tab01DemoScreen(val text: String) : Screen {
    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize()) {
            HDDebugText("TAB $text")
        }
    }
}



