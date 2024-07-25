package com.yunext.virtuals.ui.screen.logger

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_blue_2
import com.yunext.kmp.resource.color.app_blue_light
import com.yunext.kmp.resource.color.app_brush_item_content_spec
import com.yunext.kmp.resource.color.app_gray_deep
import com.yunext.kmp.resource.color.app_green
import com.yunext.kmp.resource.color.app_red
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_666666
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.kmp.resource.color.app_zi
import com.yunext.kmp.ui.compose.CHItemShadowShape
import com.yunext.kmp.ui.compose.Debug
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.kmp.ui.compose.hdClip
import com.yunext.kmp.ui.compose.hdBorder
import com.yunext.virtuals.data.DownLog
import com.yunext.virtuals.data.Log
import com.yunext.virtuals.data.OnlineLog
import com.yunext.virtuals.data.UpLog
import com.yunext.virtuals.ui.common.TwinsEmptyView
import com.yunext.virtuals.ui.common.TwinsLabelText
import com.yunext.virtuals.ui.demo.DirectionLazyList
import com.yunext.virtuals.ui.demo.MoveDirection
import com.yunext.virtuals.ui.screen.logger.data.UIType
import com.yunext.virtuals.ui.screen.logger.data.timestampStr
import com.yunext.virtuals.ui.screen.logger.data.title
import com.yunext.virtuals.ui.theme.ItemDefaults
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun InternalLogPage(
    modifier: Modifier,
    list: List<Log>,
    preFetchSize: Int,
    tab: UIType,
    msg: String,
    pullState: PullState,
    onTabSelectIng: (UIType) -> Unit,
    onTabSelected: (UIType) -> Unit,
    onLoadMore: () -> Unit,
) {
    Debug { "[recompose_test_01] InternalLogPage ${list.size} " }
    val tabs by remember {
        mutableStateOf(UIType.entries.toList())
    }
    val state = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()
    LaunchedEffect(state) {
        snapshotFlow { state.currentPage }.collect {
            val curTab = tabs[it]
            onTabSelectIng.invoke(curTab)
        }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.settledPage }.collect {
            val curTab = tabs[it]
            onTabSelected.invoke(curTab)
        }
    }
    val curPosition by remember(tab) {
        mutableStateOf(tabs.indexOf(tab))
    }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        //<editor-fold desc="->tab">
        ScrollableTabRow(selectedTabIndex = curPosition,
//            modifier = Modifier.fillMaxWidth(),
            contentColor = Color.Transparent,
            edgePadding = 0.dp,
            backgroundColor = Color.Transparent,
            divider = @Composable {

                androidx.compose.material.TabRowDefaults.Divider(
                    color = Color.Transparent
                )

            },
            indicator = @Composable { tabPositions ->
                androidx.compose.material.TabRowDefaults.Indicator(
                    color = app_appColor,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[curPosition])
                )
            }) {
            tabs.forEach { item ->
                TabItem(item, item == tab, onClick = {
                    scope.launch {
                        val page = tabs.indexOf(item)
                        state.animateScrollToPage(page)
                        //onTabSelected.invoke(tabs[page])
                    }
                })
            }
        }
        //</editor-fold>

        //<editor-fold desc="->msg">
        Text(
            msg, style = TextStyle.Default.copy(
                color = app_textColor_999999, fontSize = 11.sp, textAlign = TextAlign.Center
            ), modifier = Modifier.padding(top = 12.dp)
        )
        //</editor-fold>

        //<editor-fold desc="->viewpager">
        Box(Modifier.fillMaxWidth().weight(1f).hdBorder(debug = false)) {
            HorizontalPager(
                state = state, modifier = Modifier.fillMaxSize()
            ) { page ->
                LogPage(tabs[page], list, preFetchSize = preFetchSize, pullState, onRefresh = {
                    onTabSelected.invoke(tabs[page])
                }, onLoadMore = {
                    onLoadMore()
                }, onItemSelected = {})


            }

        }
        //</editor-fold>
    }

}


@Composable
private fun TabItem(uiType: UIType, selected: Boolean, onClick: () -> Unit) {

    Text(
        uiType.title, style = TextStyle.Default.copy(
            color = if (selected) app_appColor else app_textColor_333333,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        ), modifier = Modifier.clickable { onClick() }.padding(vertical = 8.dp, horizontal = 12.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LogPage(
    tab: UIType,
    list: List<Log>,
    preFetchSize: Int,
    pullState: PullState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onItemSelected: (Log) -> Unit,
    debug: Boolean = false,
) {
    Debug { "[recompose_test_01] LogPage ${list.size} " }

    val refreshScope = rememberCoroutineScope()
    val refreshing by remember(pullState) { mutableStateOf(pullState == PullState.Refreshing) }

    fun refresh() = refreshScope.launch {
//        refreshing = true
        onRefresh()

//        withTimeout(5000) {
//            refreshing = false
//        }
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)


    Box(/*Modifier.pullRefresh(onPull = {
        it
    }, onRelease = { it: Float ->
        it
    }, enabled = true)*/
        modifier = Modifier.fillMaxSize()
            .pullRefresh(state, true)
    ) {
//        TwinsEmptyView()
        if (list.isEmpty()) {
            Box(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                TwinsEmptyView()
            }

        } else {
            val lazyListState = rememberLazyListState()
            //  判断方向
            var testOffset by remember { mutableStateOf(0f) }
            var firstVisibleItemScrollOffsetCurrent by remember { mutableStateOf(0) }
            var firstVisibleItemIndexCurrent by remember { mutableStateOf(0) }
            var lastScrollOffset: Boolean? by remember { mutableStateOf(null) }

//            val scrollableState = rememberScrollableState{
//                    delta->
//                testOffset = delta
//                delta
//            }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset {
                        val delta = available.y
                        testOffset = delta
//                        val newOffset = toolbarOffsetHeightPx.value + delta
//                        toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                        return Offset.Zero
                    }
                }
            }

            LaunchedEffect(tab) {
                lazyListState.scrollToItem(0)
            }
//            val isPreFetching by remember(list) {
//                derivedStateOf {
//                    val info = lazyListState.layoutInfo
//                    val index = info.visibleItemsInfo.lastOrNull()?.index ?: 0
//                    val totalItemsCount = info.totalItemsCount
//                    val result =
//                        if (totalItemsCount - index > preFetchSize) (index == info.totalItemsCount - preFetchSize) else false
//                    HDLogger.d(
//                        "isPreFetching",
//                        "[$result]index:$index /${info.totalItemsCount} preFetchSize=$preFetchSize"
//                    )
//                    result
//                    //index == info.totalItemsCount - preFetchSize
//                }
//            }


//            var isMoveUp:Boolean? by remember {
//                mutableStateOf(false)
//            }
//            val isMoveUp by remember {
//                derivedStateOf {
//                    if (lazyListState.firstVisibleItemIndex == firstVisibleItemIndexCurrent) {
//                        val delta =
//                            lazyListState.firstVisibleItemScrollOffset - firstVisibleItemScrollOffsetCurrent
//                        val result = delta >= 0
//                        lastScrollOffset = result
//                        HDLogger.d(
//                            "doLoadMore",
//                            "testFangXiang=$result"
//                        )
//                        firstVisibleItemScrollOffsetCurrent =
//                            lazyListState.firstVisibleItemScrollOffset
//                    } else {
//                        firstVisibleItemScrollOffsetCurrent =
//                            lazyListState.firstVisibleItemScrollOffset
//                        firstVisibleItemIndexCurrent = lazyListState.firstVisibleItemIndex
//                    }
//                    lastScrollOffset
//                }
//            }
            //println("isMoveUp $isMoveUp")


            val canLoadMore by remember {
                derivedStateOf {
                    val info = lazyListState.layoutInfo
//                    val msg = """
//                        |firstVisibleItemIndex     :   ${lazyListState.firstVisibleItemIndex}
//                        |firstVisibleItemScrollOffset     :   ${lazyListState.firstVisibleItemScrollOffset}
//                        |canScrollBackward    :   ${lazyListState.canScrollBackward}
//                        |canScrollForward    :   ${lazyListState.canScrollForward}
//                        |visibleItemsInfo     :   ${info.visibleItemsInfo.size}
//                        |viewportStartOffset  :   ${info.viewportStartOffset}
//                        |viewportEndOffset    :   ${info.viewportEndOffset}
//                        |totalItemsCount      :   ${info.totalItemsCount}
//                        |viewportSize         :   ${info.viewportSize}
//                        |beforeContentPadding :   ${info.beforeContentPadding}
//                        |afterContentPadding  :   ${info.afterContentPadding}
//                        |mainAxisItemSpacing  :   ${info.mainAxisItemSpacing}
//                        |isScrollInProgress   :   ${lazyListState.isScrollInProgress}
//
//                    """.trimMargin()


                    val index = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val totalItemsCount = info.totalItemsCount
                    val yuzhi = totalItemsCount - preFetchSize
                    // 21 - 5 = 16
                    val isPreFetching = testOffset < 0 && (yuzhi == index) // 如何判断滑动方向
//                    val isPreFetching = isMoveUp==true && (yuzhi == index) // 如何判断滑动方向
                    val isBottom = !lazyListState.canScrollForward // 会触发



                    HDLogger.d(
                        "doLoadMore",
                        "testOffset=$testOffset isPreFetching=$isPreFetching(index:$index /${info.totalItemsCount} preFetchSize=$preFetchSize) ,isBottom:$isBottom"
                    )


//                    HDLogger.d("canLoadMore", "msg:$msg")
                    isPreFetching //|| isBottom
                }
            }
            Debug {
                "LogPage canLoadMore:$canLoadMore "
            }

            DirectionLazyList {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.align(Alignment.TopCenter)
//                        .onMoveDirectionChanged((lazyListState) ){
//                                isMoveUp = when(it){
//                                    MoveDirection.Up -> true
//                                    MoveDirection.Down -> false
//                                }
//                        }
                        .nestedScroll(nestedScrollConnection)
//                    .scrollable(state =scrollableState ,orientation = Orientation.Vertical)
                    ,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(list, { it.toString() }) { log ->
                        LogItem(log) {
                            onItemSelected.invoke(log)
                        }
                    }

                    item(key = "isBottomed") {
                        if (pullState == PullState.LoadMoreIng) {
                            Box(Modifier.fillMaxWidth()) {
                                Text(
                                    "加载更多...",
                                    style = TextStyle.Default.copy(
                                        color = app_textColor_666666,
                                        fontSize = 11.sp
                                    ), modifier = Modifier.align(Alignment.Center)
                                )
                                LaunchedEffect(Unit) {
                                    lazyListState.animateScrollToItem(list.size - 1)
                                }
                            }


                        }
                    }
                }
            }


            LaunchedEffect(canLoadMore) {
                if (pullState == PullState.Idle && (canLoadMore)) {
                    onLoadMore()
                }
            }

            val showButton by remember {
                derivedStateOf {
                    lazyListState.firstVisibleItemIndex > 0
                }
            }



            AnimatedVisibility(
                visible = showButton,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                ScrollToTopButton(modifier = Modifier) {
                    refreshScope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            }

            if (debug) {
                Text(
                    "isBottomed=$canLoadMore\npullState：$pullState \nisMoveUp:",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(app_brush_item_content_spec)
                        .hdClip(RoundedCornerShape(12.dp))
                )
            }

        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }


}

@Composable
private fun ScrollToTopButton(modifier: Modifier, onClick: () -> Unit) {
    Row(modifier.padding(16.dp)
        .shadow(2.dp, CircleShape)
        .hdBackground {
            Color.LightGray
        }
        .clickable(onClick = onClick)
        .padding(horizontal = 12.dp, vertical = 8.dp)
        .clip(CircleShape)

    ) {
        Text(text = "回到顶部", modifier = Modifier)
        Image(Icons.Default.KeyboardArrowUp, null)
    }


}

//<editor-fold desc="Log Item 布局">
@Composable
private fun LogItem(log: Log, onClick: () -> Unit) {
    Column(Modifier.padding(horizontal = 0.dp)) {
        //Text("$log")
        CHItemShadowShape(Modifier) {
            when (log) {
                is DownLog -> DownLogItem(log, onClick)
                is OnlineLog -> OnlineLogItem(log)
                is UpLog -> UpLogItem(log, onClick)
            }
        }

    }
}

@Composable
private fun OnlineLogItem(log: OnlineLog) {
    Column(Modifier.fillMaxWidth().wrapContentHeight().hdClip(ItemDefaults.itemShape)
        .drawWithContent {
            drawRect(Color.White)
            drawContent()
        }
//        .pointerInput(Unit) {
//            detectTapGestures(
//                onTap = {
//                    onClick()
//                },
//                onPress = { },
//                onLongPress = { onLongClick() })
//        }
        .padding(16.dp)) {
        // 时间戳  + 状态
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material.Text(
                    text = "${log.timestamp}",//+device.status.str,
//                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                )
                //LogType(modifier = Modifier.padding(horizontal = 12.dp), text = "up")
            }

            LogOnlineBlock(Modifier, log.onLine)


        }
        Spacer(modifier = Modifier.height(16.dp))
        LogContentList(DefaultContentItem.ContentB.map {
            it.text to when (it) {
                ContentItem.DeviceId -> log.deviceId
                ContentItem.ClientId -> log.clientId
                else -> throw IllegalArgumentException("OnlineLog不支持 $it")
            }
        })
    }

}

@Composable
private fun UpLogItem(log: UpLog, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().wrapContentHeight().hdClip(ItemDefaults.itemShape)
        .drawWithContent {
            drawRect(Color.White)
            drawContent()
        }
//        .pointerInput(Unit) {
//            detectTapGestures(
//                onTap = {
//                    onClick()
//                },
//                onPress = { },
//                onLongPress = { onLongClick() })
//        }
        .padding(16.dp)) {
        // 时间戳 + type + 状态
        Row(
            modifier = Modifier.fillMaxWidth().hdBorder(debug = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material.Text(
                    text = log.timestampStr,//+device.status.str,
//                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                )
                LogType(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = "up",
                    app_blue_light
                )
            }

            LogStateBlock(modifier = Modifier, success = log.state)


        }
        Spacer(modifier = Modifier.height(16.dp))
        LogContentList(DefaultContentItem.ContentA.map {
            it.text to when (it) {
                ContentItem.DeviceId -> log.deviceId
                ContentItem.Topic -> log.topic
                ContentItem.Cmd -> log.cmd
                ContentItem.ClientId -> log.clientId
                ContentItem.Payload -> log.payload
            }
        })
    }
}


@Composable
private fun LogContentList(logs: List<Pair<String, String>>) {
    val gap = @Composable {
        Spacer(Modifier.height(5.dp))
    }
    logs.forEachIndexed() { index, (key, value) ->
        LabelKeyAndValueBlock(key = key, value = value)
        if (index <= logs.size - 1) {
            gap()
        }
    }
}

// todo subcomposelayout
@Composable
fun LabelKeyAndValueBlock(key: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TwinsLabelText(modifier = Modifier, key)
        Spacer(Modifier.width(4.dp))
        Text(
            value,
            style = TextStyle.Default.copy(app_textColor_666666, fontSize = 13.sp),
            modifier = Modifier.weight(1f)
        )

    }


}

@Composable
private fun DownLogItem(log: DownLog, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().wrapContentHeight().hdClip(ItemDefaults.itemShape)
        .drawWithContent {
            drawRect(Color.White)
            drawContent()
        }
//        .pointerInput(Unit) {
//            detectTapGestures(
//                onTap = {
//                    onClick()
//                },
//                onPress = { },
//                onLongPress = { onLongClick() })
//        }
        .padding(16.dp)) {
        // 时间戳 + type + 状态
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material.Text(
                    text = "${log.timestamp}",//+device.status.str,
//                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                )
                LogType(modifier = Modifier.padding(horizontal = 12.dp), text = "down", app_zi)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LogContentList(DefaultContentItem.ContentA.map {
            it.text to when (it) {
                ContentItem.DeviceId -> log.deviceId
                ContentItem.Topic -> log.topic
                ContentItem.Cmd -> log.cmd
                ContentItem.ClientId -> log.clientId
                ContentItem.Payload -> log.payload
            }
        })
    }
}

private enum class ContentItem {
    DeviceId, Topic, Cmd, ClientId, Payload;
}

private val ContentItem.text: String
    get() = when (this) {
        ContentItem.DeviceId -> "设备ID"
        ContentItem.Topic -> "topic"
        ContentItem.Cmd -> "cmd"
        ContentItem.ClientId -> "clientID"
        ContentItem.Payload -> "payload"
    }

private object DefaultContentItem {
    val ContentA by lazy {
        listOf(
            ContentItem.DeviceId,
            ContentItem.Topic,
            ContentItem.Cmd,
            ContentItem.ClientId,
            ContentItem.Payload
        )
    }

    val ContentB by lazy {
        listOf(
            ContentItem.DeviceId,
            ContentItem.ClientId,
        )
    }
}

@Composable
private fun LogType(modifier: Modifier = Modifier, text: String, textColor: Color) {
    Box(
        modifier.background(textColor, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            Modifier.padding(vertical = 3.dp, horizontal = 8.dp),
            style = TextStyle.Default.copy(
                fontSize = 11.sp, color = app_appColor, textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun LogOnlineBlock(modifier: Modifier = Modifier, online: Boolean) {
    Box(
        modifier.background(
            if (online) app_blue_2 else app_gray_deep, shape = RoundedCornerShape(16.dp)
        ), contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (online) "在线" else "离线",
            modifier = Modifier.padding(vertical = 3.dp, horizontal = 8.dp),
            style = TextStyle.Default.copy(
                fontSize = 11.sp, color = Color.White, textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun LogStateBlock(modifier: Modifier = Modifier, success: Boolean) {
    Box(
        modifier.background(if (success) app_green else app_red, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (success) "成功" else "失败",
            modifier = Modifier.padding(vertical = 3.dp, horizontal = 8.dp),
            style = TextStyle.Default.copy(
                fontSize = 11.sp, color = Color.White, textAlign = TextAlign.Center
            )
        )
    }
}
//</editor-fold>


//<editor-fold desc="DEMO">
//  https://www.composables.com/multiplatform/tabrow/examples
@Composable
fun FancyIndicatorContainerTabs() {
    var state by remember { mutableStateOf(0) }
    val titles = listOf("TAB 1", "TAB 2", "TAB 3", "TAB 4", "TAB 5", "TAB 6", "TAB 7")

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        FancyAnimatedIndicator(tabPositions = tabPositions, selectedTabIndex = state)
    }

    Column {
        TabRow(
            selectedTabIndex = state, indicator = indicator
        ) {
            titles.forEachIndexed { index, title ->
                Tab(text = { Text(title) }, selected = state == index, onClick = { state = index })
            }
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Fancy transition tab ${state + 1} selected",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun FancyAnimatedIndicator(tabPositions: List<TabPosition>, selectedTabIndex: Int) {
    val colors = listOf(Color.Yellow, Color.Red, Color.Green)
    val transition = updateTransition(selectedTabIndex)
    val indicatorStart by transition.animateDp(transitionSpec = {
        // Handle directionality here, if we are moving to the right, we
        // want the right side of the indicator to move faster, if we are
        // moving to the left, we want the left side to move faster.
        if (initialState < targetState) {
            spring(dampingRatio = 1f, stiffness = 50f)
        } else {
            spring(dampingRatio = 1f, stiffness = 1000f)
        }
    }) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(transitionSpec = {
        // Handle directionality here, if we are moving to the right, we
        // want the right side of the indicator to move faster, if we are
        // moving to the left, we want the left side to move faster.
        if (initialState < targetState) {
            spring(dampingRatio = 1f, stiffness = 1000f)
        } else {
            spring(dampingRatio = 1f, stiffness = 50f)
        }
    }) {
        tabPositions[it].right
    }

    val indicatorColor by transition.animateColor {
        colors[it % colors.size]
    }

    FancyIndicator(
        // Pass the current color to the indicator
        indicatorColor, modifier = Modifier
            // Fill up the entire TabRow, and place the indicator at the start
            .fillMaxSize().wrapContentSize(align = Alignment.BottomStart)
            // Apply an offset from the start to correctly position the indicator around the tab
            .offset(x = indicatorStart)
            // Make the width of the indicator follow the animated width as we move between tabs
            .width(indicatorEnd - indicatorStart)
    )
}

@Composable
fun FancyIndicator(color: Color, modifier: Modifier = Modifier) {
    // Draws a rounded rectangular with border around the Tab, with a 5.dp padding from the edges
    // Color is passed in as a parameter [color]
    Box(
        modifier.padding(5.dp).fillMaxSize()
            .border(BorderStroke(2.dp, color), RoundedCornerShape(5.dp))
    )
}
//</editor-fold>