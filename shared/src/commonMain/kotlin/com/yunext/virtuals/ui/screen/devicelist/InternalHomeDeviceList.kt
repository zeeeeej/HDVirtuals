package com.yunext.virtuals.ui.screen.devicelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.ui.compose.Debug
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.TwinsDeviceStatus
import com.yunext.virtuals.ui.common.TwinsLabelText
import com.yunext.virtuals.ui.data.DeviceAndState
import com.yunext.virtuals.ui.theme.ItemDefaults


@Composable
internal expect fun TwinsDeviceList(
    modifier: Modifier,
    list: List<DeviceAndState>,
    onDeviceSelected: (DeviceAndState) -> Unit,
    onDeviceDelete: (DeviceAndState) -> Unit,
)

@Composable
internal expect fun TwinsDeviceItem(
    modifier: Modifier,
    device: DeviceAndState,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
)

@Composable
internal fun TwinsDeviceItemCommon(
    modifier: Modifier,
    device: DeviceAndState,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Debug("TwinsHomePage-内容-设备列表-item")
    Text("desktop - DeviceItem", modifier = modifier.padding(16.dp).clickable {
        onClick()
    })

    Column(modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .clip(ItemDefaults.itemShape)
//        .background(Color.White)
//        .background(xplRandomColor())
        .hdBackground()
//        .drawWithContent{
//            drawRect(xplRandomColor())
//            drawContent()
//        }
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {},
                onPress = { onClick() },
                onLongPress = { onLongClick() })
        }
//        .clickable {
//            onClick()
//        }
        .padding(16.dp)) {
        Debug("TwinsHomePage-内容-设备列表-item-internal-1")
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = device.name,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(4.dp))
            TwinsDeviceStatus(deviceStatus = device.status)

        }

        Spacer(modifier = Modifier.height(16.dp))

        DeviceCommunicationIdAndModel(Modifier, device.communicationId, device.model)
    }
}

@Composable
internal fun DeviceCommunicationIdAndModel(
    modifier: Modifier,
    communicationId: String,
    model: String,
) {
    Debug("TwinsHomePage-内容-设备列表-item-internal-2")
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TwinsLabelText(text = "通信ID")
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = communicationId,
            maxLines = 1,
            fontSize = 11.sp,
            color = Color(0xff666666),
            overflow = TextOverflow.Ellipsis
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TwinsLabelText(text = "设备型号")
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = model,
            maxLines = 1,
            fontSize = 11.sp,
            color = Color(0xff666666),
            overflow = TextOverflow.Ellipsis
        )
    }
}