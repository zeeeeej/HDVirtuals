package com.yunext.virtuals.ui.screen.logger

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yunext.kmp.db.datasource.LogDatasource
import com.yunext.kmp.ui.compose.Debug
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.kmp.ui.compose.hdBorder
import com.yunext.virtuals.data.Log
import com.yunext.virtuals.ui.Effect
import com.yunext.virtuals.ui.common.DrawableResourceFactory
import com.yunext.virtuals.ui.common.TwinsBackgroundBlock
import com.yunext.virtuals.ui.common.TwinsTitle
import com.yunext.virtuals.ui.common.dialog.CHLoadingDialog
import com.yunext.virtuals.ui.common.dialog.CHTipsDialog
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.processing
import com.yunext.virtuals.ui.screen.logger.data.TimeSetter
import com.yunext.virtuals.ui.screen.logger.data.TimeSetterType
import com.yunext.virtuals.ui.screen.logger.data.UIType
import com.yunext.virtuals.ui.screen.logger.data.ZERO
import com.yunext.virtuals.ui.screen.logger.data.isZero
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

class LoggerScreen(private val deviceAndState: DeviceAndStateViewData) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            LogScreenModel(
                LogState(
                    device = deviceAndState, list = emptyList(), effect = Effect.Idle
                )
            )
        }
        val coroutineScope = rememberCoroutineScope()
        val state by screenModel.state.collectAsState()
//        LaunchedEffect(Unit) {
//            screenModel.doSearch(SearchCondition())
//        }

        var toast by remember { mutableStateOf("") }
       var toastJob :Job? by remember { mutableStateOf(null) }
//        val toast by state.toast.collectAsState("")

        Debug { "[recompose_test_01] LoggerScreen ${state.list.size} " }
        LoggerScreenImpl(state, state.list, msg = state.msg ?: "", onLeft = {
            navigator.pop()
        }, onTextSearchChanged = {
            screenModel.onSearchConditionChanged(it)
        }, onDateTimeSearchChanged = { start, end ->
            screenModel.onSearchConditionChanged(start = start, end = end)
        }, onTabSearchChanged = {
            screenModel.onSearchConditionChanged(it)
        }, onPullStateChanged = {

        }, onLoadMore = {
            screenModel.doLoadMore()
        }, onToast = {
            toastJob?.cancel()
            toastJob = null
            toastJob = coroutineScope.launch {
                toast = it
                delay(3000)
                toast=""
            }
        })

        var loading by remember { mutableStateOf(true) }
        var alert: String? by remember { mutableStateOf(null) }
        LaunchedEffect(state) {
            val effect = state.effect
            loading = effect.processing
            alert = state.alert
        }
        if (loading) {
            CHLoadingDialog("数据加载中...") {
                loading = false
            }
        }


        val t by remember {
            derivedStateOf { alert ?: "" }
        }
        if (t.isNotEmpty()) {
            CHTipsDialog(text = t) {
                alert = null
            }
        }

        LaunchedEffect(Unit) {
            screenModel.toast.collect {
                Napier.e {
                    "toast:$toast"
                }
                toast = it
                toastJob?.cancel()
                toastJob = null
                toastJob = launch {
                    delay(3000)
                    toast = ""
                }
            }
        }


        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            AnimatedVisibility(toast.isNotEmpty()) {
                Snackbar(modifier = Modifier, action = {
                    Text(modifier = Modifier.clickable {
                        toast = ""
                    }, text = "关闭")
                }) {
                    Text("${toast}")
                }
            }

        }
    }
}


@Composable
internal fun LoggerScreenImpl(
    state: LogState,
    list: List<Log>,
    msg: String,
    onLeft: () -> Unit,
    onTextSearchChanged: (String) -> Unit,
    onDateTimeSearchChanged: (Long, Long) -> Unit,
    onTabSearchChanged: (UIType) -> Unit,
    onLoadMore: () -> Unit,
    onPullStateChanged: (PullState) -> Unit,
    onToast: (String) -> Unit,
    debug:Boolean = false

    ) {
    Debug { "[recompose_test_01] LoggerScreenImpl ${list.size} " }
    @OptIn(ExperimentalResourceApi::class)
    CommonScreen(modifier = Modifier.fillMaxSize(),
        title = "日志",
        onLeft = onLeft,
        background = {
            TwinsBackgroundBlock(grey = true)
        }
    ) {
        var text by remember { mutableStateOf("") }
        Column(Modifier.fillMaxSize()) {
            // 搜索
            Box(Modifier.hdBackground { Color.White }) {
                InternalSearchBlock(
                    modifier = Modifier,
                    text = text,
                    onChanged = {
                        text = it
                    },
                    onSearch = {
                        onTextSearchChanged(text)
                    })
            }

            var editTime: Pair<TimeSetterType, TimeSetter>? by remember {
                mutableStateOf(null)
            }

//            var startTime: TimeSetter by remember(state.searchCondition.start) {
//                mutableStateOf(state.searchCondition.start)
//            }
//
//            var endTime: TimeSetter by remember(state.searchCondition.end) {
//                mutableStateOf(ZERO)
//            }


            InternalDateTimeSetterBlock(
                modifier = Modifier.fillMaxWidth().hdBorder(debug = false)
                    .hdBackground { Color.White },
                start = TimeSetter(state.searchCondition.start),
                end = TimeSetter(state.searchCondition.end),
                onClick = {
                    editTime = when (it) {
                        TimeSetterType.Start -> it to TimeSetter(state.searchCondition.start)
                        TimeSetterType.End -> it to TimeSetter(state.searchCondition.end)
                    }
                }
            )
            var curTab: UIType? by remember {
                mutableStateOf(null)
            }
            // logs
            Box(Modifier.weight(1f)) {
                InternalLogPage(
                    list = list,
                    preFetchSize = state.searchCondition.preFetchSize,
                    modifier = Modifier.fillMaxSize().hdBorder(debug = false),
                    tab = curTab ?: when (state.searchCondition.sign) {
                        LogDatasource.Sign.ALL -> UIType.ALl
                        LogDatasource.Sign.FAIL -> UIType.Fail
                        LogDatasource.Sign.SUCCESS -> UIType.Success
                        LogDatasource.Sign.ONLINE -> UIType.Online
                        LogDatasource.Sign.UP -> UIType.Up
                        LogDatasource.Sign.DOWN -> UIType.Down
                    },
                    msg = "共${state.searchCondition.pageNumber}页,${list.size}条数据",
                    pullState = state.pullState,
                    onTabSelectIng = {
                        curTab = it
                    },
                    onTabSelected = {
                        curTab = null
                        onTabSearchChanged.invoke(it)
                    }, onLoadMore = onLoadMore
                )
            }

            // edit time
            if (editTime != null) {
                InternalSelectedDatetimeDialog(title = when (editTime?.first) {
                    TimeSetterType.Start -> "开始"
                    TimeSetterType.End -> "结束"
                    null -> "未知"
                }, dateTime =
                editTime?.second ?: ZERO, onSelected = { value ->
                    val oldStart = state.searchCondition.start
                    val oldEnd = state.searchCondition.end

                    when (editTime?.first) {
                        TimeSetterType.Start -> {
                            if ((!oldEnd.isZero()) and (value >= oldEnd)) {
                                onToast.invoke("开始时间大于结束时间")
                                return@InternalSelectedDatetimeDialog
                            }
                            onDateTimeSearchChanged.invoke(value, oldEnd)
                        }

                        TimeSetterType.End -> {
                            if ((value <= oldStart) and (!oldStart.isZero())) {
                                onToast.invoke("结束时间小于开始时间")
                                return@InternalSelectedDatetimeDialog
                            }
                            onDateTimeSearchChanged.invoke(oldStart, value)
                        }

                        null -> {}
                    }
                    editTime = null
                }, onDismiss = {
                    editTime = null
                })
            }

        }
    }
}

@Immutable
internal interface CommonScreenScope {
    fun Modifier.onLeft(onClick: () -> Unit): Modifier
}

private object CommonScreenScopeInstance : CommonScreenScope {
    override fun Modifier.onLeft(onClick: () -> Unit): Modifier {
        return this.clickable(onClick = onClick)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun CommonScreen(
    modifier: Modifier = Modifier,
    title: String,
    icon: DrawableResourceFactory? = null,
    onLeft: () -> Unit,
    background: (@Composable CommonScreenScope.() -> Unit)? = null,
    content: @Composable CommonScreenScope.() -> Unit,
) {
    Box(modifier) {
        // 背景
        if (background != null) {
            CommonScreenScopeInstance.background()
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1 标题
            TwinsTitle(modifier = Modifier.fillMaxWidth().hdBackground {
                Color.White
            }, text = title,

                icon = icon, leftClick = {
                    onLeft()
                }, rightClick = {

                })

            // 2 内容
            Box(Modifier.fillMaxWidth().weight(1f)) {
                CommonScreenScopeInstance.content()
            }
        }

    }

}