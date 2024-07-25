package com.yunext.virtuals.ui.screen.devicedetail.screenviewpager

import HDDebugText
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_666666
import com.yunext.virtuals.ui.data.EventData
import com.yunext.virtuals.ui.data.PropertyData
import com.yunext.virtuals.ui.data.ServiceData
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnServiceListener
import com.yunext.virtuals.ui.screen.devicedetail.deviceevent.DeviceDetailSubEventScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceproperty.DeviceDetailSubPropertyScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.DeviceDetailSubServiceScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnEventListener
import com.yunext.virtuals.ui.screen.devicedetail.deviceservice.OnPropertyListener
import com.yunext.virtuals.ui.screen.devicedetail.screennormal.DeviceDetailTabsV3
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
    onServiceListener: OnServiceListener,
    debug:Boolean = false,
) {
    if (debug){
        HDDebugText("${propertyList.size}/${eventList.size}/${serviceList.size}")
    }
    Column {
        val tabs by remember {
            mutableStateOf(deviceDetailTabs)
        }

        var curTab: HDDeviceTab by remember { mutableStateOf(PropertiesTab) }

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

        // tab

        Row(modifier = Modifier.fillMaxWidth()) {

            DeviceDetailTabsV3(
                modifier = Modifier.weight(1f),
                curTab = curTab,
                tabs = tabs
            ) { tab ->
                curTab = tab
                coroutineScope.launch {
                    val pos = curTab.index
                    pageState.animateScrollToPage(pos)
                }
            }

            CountInfo(
                Modifier.weight(1f),
                when (curTab) {
                    EventsTab -> eventList.size
                    PropertiesTab -> propertyList.size
                    ServicesTab -> serviceList.size
                }
            )
        }


        // content
        HorizontalPager(state = pageState) { pos ->
            when (tabs[pos]) {
                PropertiesTab -> DeviceDetailSubPropertyScreenImpl(propertyList) {
                    onPropertyListener.invoke(it.value.value)
                }

                EventsTab -> DeviceDetailSubEventScreenImpl(eventList, onEventListener)
                ServicesTab -> DeviceDetailSubServiceScreenImpl(serviceList, onServiceListener)
            }
        }


    }
}

@Composable
fun CountInfo(modifier: Modifier, size: Int) {
    Box(modifier.padding(16.dp), contentAlignment = Alignment.CenterEnd) {
        Row(Modifier, verticalAlignment = Alignment.CenterVertically) {

            Text(
                "共计", style = TextStyle.Default.copy(
                    fontSize = 12.sp, color = app_textColor_666666
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                "$size", style = TextStyle.Default.copy(
                    fontSize = 18.sp, color = app_textColor_333333
                )
            )

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
//@RequiresOptIn(message="_",level = RequiresOptIn.Level.ERROR)
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