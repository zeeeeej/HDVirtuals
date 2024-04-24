package com.yunext.virtuals.ui.demo.voyager.case01

import HDDebugText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

