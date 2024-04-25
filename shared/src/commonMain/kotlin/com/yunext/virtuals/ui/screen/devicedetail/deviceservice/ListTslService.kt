package com.yunext.virtuals.ui.screen.devicedetail.deviceservice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_blue_light
import com.yunext.kmp.resource.color.app_orange
import com.yunext.kmp.resource.color.app_orange_light
import com.yunext.kmp.resource.color.app_red
import com.yunext.kmp.resource.color.app_red_light
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.kmp.ui.compose.CHItemShadowShape
import com.yunext.virtuals.ui.data.ServiceData
import com.yunext.virtuals.ui.screen.devicedetail.deviceproperty.BottomPart
import com.yunext.virtuals.ui.screen.devicedetail.InputPart
import com.yunext.virtuals.ui.screen.devicedetail.deviceproperty.LabelPart
import com.yunext.virtuals.ui.theme.ItemDefaults

// ----------------- services ----------------- //

/**
 * 服务列表
 * 当收到服务cmd时，全局弹窗让用户回复或者自动回复。
 * 比如收到查询时间的cmd，弹出一个弹窗显示输入参数input，提示用户填写当前时间（output）或者点击自动按钮回复。
 * 或者配置一个脚本来处理输入参数并且回复输出，没有配置时弹窗让用户填写。
 */
@Composable
internal fun  ListTslService(list: List<ServiceData>, onClick: (ServiceData) -> Unit) {

    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items = list, key = { _, it ->
            it.toString()
        }) { index, it ->
            ServiceItem(it) {
                onClick(list[index])
            }
        }
    }
}

@Composable
private fun ServiceItem(data: ServiceData, onClick: () -> Unit) {
    val desc = data.desc

    CHItemShadowShape() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ItemDefaults.itemShape)
                .background(ItemDefaults.itemBackground)
                .padding(16.dp)
        ) {
            // 头部基本信息
            HeaderParts(data = data) {
                onClick()
            }

            Spacer(modifier = Modifier.height(16.dp))
            // 输入
            InputPart("输入", data.input)
            Spacer(modifier = Modifier.height(16.dp))
            // 输出
            InputPart("输出", data.output)
            Spacer(modifier = Modifier.height(16.dp))
            // desc
            BottomPart(desc)
        }
    }
}


@Composable
private fun HeaderParts(data: ServiceData, onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.weight(1f, true)) {
            Row(modifier = Modifier) {
                Text(
                    text = data.name.run { this.ifEmpty { "未知" } },
                    fontSize = 16.sp,
                    color = app_textColor_333333,
                    fontWeight = FontWeight.Bold
                )
                Text(text = data.key.run {
                    if (this.isEmpty()) "-" else "(${this})"
                }, fontSize = 16.sp, color = app_textColor_999999, fontWeight = FontWeight.Bold)

            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier) {
                val required = if (data.required) "必须" else "非必须"
                val requiredColor =
                    if (data.required) app_red to app_red_light else app_appColor to app_blue_light
                LabelPart(
                    required,
                    requiredColor.first,
                    requiredColor.second
                )
                Spacer(modifier = Modifier.width(8.dp))
                val async = if (data.async) "async" else "sync"
                val asyncColor =
                    if (data.async) app_orange to app_orange_light else app_appColor to app_blue_light

                LabelPart(async, asyncColor.first, asyncColor.second)
                Spacer(modifier = Modifier.width(8.dp))

            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .clip(ItemDefaults.editShape)
                .border(width = 1.dp, color = app_appColor, shape = ItemDefaults.editShape)
                .clickable {
                    onClick()
                }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            text = "模拟",
            fontSize = 13.sp,
            color = app_appColor
        )
    }
}





