package com.yunext.virtuals.ui.demo

import HDDebugText
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.ui.compose.Debug
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleHorizontalPagerSample() {
    // Creates a 1-pager/viewport horizontal pager with single page snapping
    val state = rememberPagerState { 10 }
    HorizontalPager(
        state = state,
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        Box(
            modifier = Modifier
                .padding(10.dp)
                .background(Color.Blue)
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = page.toString(), fontSize = 32.sp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HorizontalPagerWithScrollableContent() {
    // This is a sample using NestedScroll and Pager.
    // We use the toolbar offset changing example from
    // androidx.compose.ui.samples.NestedScrollConnectionSample

    val pagerState = rememberPagerState { 10 }

    val toolbarHeight = 48.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        TopAppBar(
            modifier = Modifier
                .height(toolbarHeight)
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) },
            title = { Text("Toolbar offset is ${toolbarOffsetHeightPx.value}") }
        )

        val paddingOffset =
            toolbarHeight + with(LocalDensity.current) { toolbarOffsetHeightPx.value.toDp() }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            contentPadding = PaddingValues(top = paddingOffset)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                repeat(20) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(4.dp)
                            .background(if (it % 2 == 0) Color.Black else Color.Yellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it.toString(),
                            color = if (it % 2 != 0) Color.Black else Color.Yellow
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TestApp() {
    var showContent by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize(),
//        Modifier.fillMaxWidth().wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state: LazyListState = rememberLazyListState()

        var visibleBeforeOffset by remember { mutableStateOf(0) }
        var visibleItemIndex by remember { mutableStateOf(0) }

        var lastScrollOffset: Boolean? by remember { mutableStateOf(null) }
//        LaunchedEffect(state){
//            snapshotFlow { state }.collectLatest {
//                cur->
//                if (visibleItemIndex == cur.firstVisibleItemIndex) {
//                    if (visibleBeforeOffset - cur.firstVisibleItemScrollOffset >= 0) {
//                        println("向下滑动")
//                        visibleBeforeOffset = cur.firstVisibleItemScrollOffset
//                        scrollOffset = true
//                    } else {
//                        println("向上滑动")
//                        visibleBeforeOffset = cur.firstVisibleItemScrollOffset
//                        scrollOffset = false
//                    }
//                } else {
//                    visibleItemIndex = cur.firstVisibleItemIndex
//                    visibleBeforeOffset = cur.firstVisibleItemScrollOffset
//                }
//            }
//        }
        val scrollOffset by remember {
            derivedStateOf {
                if (visibleItemIndex == state.firstVisibleItemIndex) {
                    if (visibleBeforeOffset - state.firstVisibleItemScrollOffset >= 0) {
                        println("向下滑动")
                        visibleBeforeOffset = state.firstVisibleItemScrollOffset
                        lastScrollOffset = true
                    } else {
                        println("向上滑动")
                        visibleBeforeOffset = state.firstVisibleItemScrollOffset
                        lastScrollOffset = false
                    }
                } else {
                    visibleItemIndex = state.firstVisibleItemIndex
                    visibleBeforeOffset = state.firstVisibleItemScrollOffset
                }
                lastScrollOffset
            }
        }


        HDDebugText("${scrollOffset}")
//        println("scrollOffset:${scrollOffset}")
        Debug { "scrollOffset:${scrollOffset}" }

        LazyColumn(state = state) {
            items(100) {
                Box(Modifier.height(100.dp).fillMaxWidth()) {
                    Text("Item index=$it")
                }
            }

        }

    }
}

enum class MoveDirection {
    Up, Down
}

interface LazyListScope {
    fun Modifier.onMoveDirectionChanged(
        state: LazyListState, onMoveDirection: (MoveDirection) -> Unit,
    ): Modifier
}

private object LazyListScopeInstance : LazyListScope {

    override fun Modifier.onMoveDirectionChanged(
        state: LazyListState, onMoveDirection: (MoveDirection) -> Unit,
    ): Modifier = composed {
//        val onMoveDirectionState by rememberUpdatedState(onMoveDirection)
        var lastFirstVisibleItemScrollOffset by remember { mutableStateOf(0) }
        var lastFirstVisibleItemIndex by remember { mutableStateOf(0) }
        var lastScrollOffset: Boolean? by remember { mutableStateOf(null) }
        val isMoveUp by remember {
            derivedStateOf {
                if (state.firstVisibleItemIndex == lastFirstVisibleItemIndex) {
                    val delta =
                        state.firstVisibleItemScrollOffset - lastFirstVisibleItemScrollOffset
                    val result = delta >= 0
                    lastScrollOffset = result
                    lastFirstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset
                    when (result) {
                        true -> onMoveDirection(MoveDirection.Up)
                        false -> onMoveDirection(MoveDirection.Down)
                        null -> {}
                    }
                    println("xxxxxxx $result")
                } else {
                    lastFirstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset
                    lastFirstVisibleItemIndex = state.firstVisibleItemIndex
                }

                lastScrollOffset
            }
        }

        Debug { "isMoveUp :$isMoveUp" }
//        LaunchedEffect(state){
//            snapshotFlow {
//                lastScrollOffset
//            }.collect{
//                when (it) {
//                    true -> onMoveDirection(MoveDirection.Up)
//                    false -> onMoveDirection(MoveDirection.Down)
//                    null -> {}
//                }
//            }
//
//        }
        this
    }

}

@Composable
fun DirectionLazyList(content: @Composable LazyListScope.() -> Unit) {
    LazyListScopeInstance.content()
}