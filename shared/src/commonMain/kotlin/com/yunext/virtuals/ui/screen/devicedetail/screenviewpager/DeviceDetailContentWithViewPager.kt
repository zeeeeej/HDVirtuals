package com.yunext.virtuals.ui.screen.devicedetail.screenviewpager

import HDDebugText
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.yunext.virtuals.ui.data.EventData
import com.yunext.virtuals.ui.data.PropertyData
import com.yunext.virtuals.ui.data.ServiceData
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnServiceListener
import com.yunext.virtuals.ui.screen.devicedetail.deviceevent.DeviceDetailSubEventScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceproperty.DeviceDetailSubPropertyScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.DeviceDetailSubServiceScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnEventListener
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnPropertyListener
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.DeviceDetailTabs
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.EventsTab
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.HDDeviceTab
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.PropertiesTab
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.ServicesTab
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.deviceDetailTabs
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.index
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DeviceDetailContentWithViewPager(
    propertyList: List<PropertyData>,
    eventList: List<EventData>,
    serviceList: List<ServiceData>,
    onPropertyListener: OnPropertyListener,
    onEventListener: OnEventListener,
    onServiceListener:OnServiceListener,
) {
    HDDebugText("${propertyList.size}/${eventList.size}/${serviceList.size}")
    Column {
        val tabs by remember {
            mutableStateOf(deviceDetailTabs)
        }

        var curTab: HDDeviceTab by remember() { mutableStateOf(PropertiesTab) }

        val pageState = rememberPagerState(initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = {
                tabs.size
            }
        )
        LaunchedEffect(pageState) {
            snapshotFlow { pageState.currentPage }.collect {
                curTab = tabs[it]
            }
        }

        val coroutineScope = rememberCoroutineScope()

        DeviceDetailTabs(curTab, tabs) {
            curTab = it
            coroutineScope.launch {
                val pos = curTab.index
                pageState.animateScrollToPage(pos)
            }
        }


        HorizontalPager(state = pageState) { pos ->
            when (tabs[pos]) {
                PropertiesTab -> DeviceDetailSubPropertyScreenImpl(propertyList) {
                    onPropertyListener.invoke(it.value.value)
                }

                EventsTab -> DeviceDetailSubEventScreenImpl(eventList,onEventListener)
                ServicesTab -> DeviceDetailSubServiceScreenImpl(serviceList,onServiceListener)
            }
        }


    }
}


//@Composable
//fun MyArray(list: List<String>) {
//    var selectedItem: String? by remember {
//        mutableStateOf(null)
//    }
//    LazyColumn {
//        items(list, key = { it.toString() }) { item ->
//            MyItem(item) {
//                selectedItem = item
//            }
//
//            MyItem2(item) {
//                selectedItem = item
//            }
//        }
//    }
//}
//
//@Composable
//fun MyItem(item: String, onClick: () -> Unit) {
//    Text(item, modifier = Modifier.clickable {
//        onClick()
//    })
//}
//
//@Composable
//fun MyItem2(item: String, onClick: (String) -> Unit) {
//    Text(item, modifier = Modifier.clickable {
//        onClick(item)
//    })
//}

//
//@PublishedApi
//internal enum class Bar{
//    A,B;
//}
//
//
//
//
//@Retention(AnnotationRetention.BINARY)
//@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
//@RequiresOptIn(message="hahaha",level = RequiresOptIn.Level.ERROR)
//annotation class `还没想好`
//
//@`还没想好`
//internal fun foo(){
////    Bar.values()
////    val list =  Bar.entries
//
////    EnumEntries<Bar>
////    arrayListOf()
//}
//
//@还没想好
//private fun t(){
//
//    foo()
//}