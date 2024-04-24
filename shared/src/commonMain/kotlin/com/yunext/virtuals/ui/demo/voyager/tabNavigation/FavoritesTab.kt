package com.yunext.virtuals.ui.demo.voyager.tabNavigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.yunext.kmp.ui.compose.clickablePure
import com.yunext.virtuals.ui.data.randomText
import com.yunext.virtuals.ui.demo.voyager.case01.CommonScreenModel
import com.yunext.virtuals.ui.demo.voyager.case01.Tab01DemoScreen
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class FavoritesTab(
    private val msg: String,
    @Contextual
    private val onMsgChanged: (String) -> Unit,
) : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Favorite)

            return remember {
                TabOptions(
                    index = 1u,
                    title = "Favorites",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
//        val screenModel = rememberScreenModel {
//            CommonScreenModel()
//        }
//        LaunchedEffect(Unit) {
//            screenModel.change("我的最爱!")
//        }
//        val msg by screenModel.state.collectAsState()
        Box() {
            Text(
                "FavoritesTab msg:$msg",
                modifier = Modifier.align(Alignment.Center).clickablePure {
                    onMsgChanged.invoke("我的最爱->${randomText(4)}")
                })
            Box(Modifier) {
                TabContent(msg,onMsgChanged)
            }
        }
    }
}