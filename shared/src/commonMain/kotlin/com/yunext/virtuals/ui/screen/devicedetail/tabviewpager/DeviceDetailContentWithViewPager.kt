package com.yunext.virtuals.ui.screen.devicedetail.tabviewpager

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
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.virtuals.ui.common.stable
import com.yunext.virtuals.ui.data.EventData
import com.yunext.virtuals.ui.data.PropertyData
import com.yunext.virtuals.ui.data.ServiceData
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.DeviceDetailSubEventScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.DeviceDetailSubPropertyScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.DeviceDetailSubServiceScreenImpl
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.DeviceDetailTabs
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.EventsTab
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.HDDeviceTab
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.PropertiesTab
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.ServicesTab
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.deviceDetailTabs
import com.yunext.virtuals.ui.screen.devicedetail.tabnormal.index
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DeviceDetailContentWithViewPager(
    propertyList: List<PropertyData>,
    eventList: List<EventData>,
    serviceList: List<ServiceData>,
    onPropertyEdit: (PropertyValue<*>) -> Unit,
) {
    HDDebugText("DeviceDetailContentWithViewPager")
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
        LaunchedEffect(pageState){
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
                    onPropertyEdit.invoke(it.value.value)
                }

                EventsTab -> DeviceDetailSubEventScreenImpl(eventList)
                ServicesTab -> DeviceDetailSubServiceScreenImpl()
            }
        }


    }
}