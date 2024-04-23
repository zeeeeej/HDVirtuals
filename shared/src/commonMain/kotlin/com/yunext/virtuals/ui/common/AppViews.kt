package com.yunext.virtuals.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.HDRes
import com.yunext.kmp.resource.color.China
import com.yunext.kmp.resource.color.app_appColor
import com.yunext.kmp.resource.color.app_background_brush
import com.yunext.kmp.resource.color.app_button_brush
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.resource.color.app_textColor_666666
import com.yunext.kmp.resource.color.app_textColor_999999
import com.yunext.kmp.resource.hdRes
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.data.DeviceStatus
import com.yunext.virtuals.ui.data.iconRes
import com.yunext.virtuals.ui.screen.LocalPaddingValues
import com.yunext.virtuals.ui.theme.Twins
import org.jetbrains.compose.resources.ExperimentalResourceApi

private object TwinsTitleDefaults {
    val height = 44.dp
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TwinsTitle(
    modifier: Modifier = Modifier,
    text: String,
    icon: DrawableResourceFactory? = null,
    //icon: Painter? = null,
    leftClick: () -> Unit = {},
    rightClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(LocalPaddingValues.current)
            .height(TwinsTitleDefaults.height),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // left
        HDImage(
            resource = {
                HDRes.drawable.icon_twins_return_nor
            },
            contentDescription = "back",
            Modifier
                .clip(CircleShape)
                .clickable {
                    leftClick.invoke()
                }
                .applyIcon()
        )

        //
        Row(modifier = Modifier.wrapContentSize().weight(1f)) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .requiredWidthIn(max = 200.dp),
//                    .width(200.dp),
                maxLines = 1
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(2.dp))
                HDImage(
                    resource = icon,
                    //painter = icon,//painterResource(id = icon),
                    modifier = Modifier.align(Alignment.CenterVertically),
                    contentDescription = "status"
                )
            }

        }

        //
        if (rightClick != null) {
            HDImage(
                resource = { HDRes.drawable.icon_twins_more_nor },
                //painter = painterResource(DrawableResource("icon_twins_more_nor.png")),//painterResource(id = R.mipmap.icon_twins_more_nor),
                contentDescription = "more",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        rightClick.invoke()
                    }
                    .applyIcon()
            )
        } else {
            Box(modifier = Modifier.applyIcon())
        }

    }


}

private fun Modifier.applyIcon() = this
    .size(TwinsTitleDefaults.height)
    .padding(10.dp)


@OptIn(ExperimentalResourceApi::class)
@Composable
fun TwinsEmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HDImage(
            resource = { HDRes.drawable.ic_app },
//            painter = painterResource(DrawableResource("ic_app.png")),//painterResource(id = R.mipmap.ic_app),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "没有找到相关内容",
            fontSize = 14.sp,
            color = app_textColor_666666
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TwinsEmptyViewForDevice() {
    Debug { "TwinsHomePage-内容-设备列表-空" }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HDImage(
            resource = { HDRes.drawable.ic_app },
//            painter = painterResource(hdRes { HDRes.drawable.ic_app }),//painterResource(id = R.mipmap.ic_app),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "你还没有添加设备",
            fontSize = 16.sp,
            color = app_textColor_333333
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "添加设备，实时查看设备信息",
            fontSize = 12.sp,
            color = app_textColor_999999
        )
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun TwinsDeviceStatus(modifier: Modifier = Modifier, deviceStatus: DeviceStatus) {
    HDImage(
        resource = deviceStatus.iconRes,
        // painter = painterResource(DrawableResource("icon_twins_offline.png")),//id = R.mipmap.icon_twins_offline),
        contentDescription = "deviceStatus", modifier = modifier
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TwinsLabelText(modifier: Modifier = Modifier, text: String) {
    Box(modifier = modifier.widthIn(min = 60.dp, max = 60.dp)) {
        HDImage(
            resource = { HDRes.drawable.icon_twins_label_bg },
//            painter = painterResource(DrawableResource("icon_twins_label_bg.png")),//id = R.mipmap.icon_twins_label_bg),
            contentDescription = "bg",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = text, fontSize = 11.sp, color = Color(0x66666666), modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .padding(horizontal = 6.dp)
        )
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun TwinsBackgroundBlock(
    modifier: Modifier = Modifier,
//    painter: Painter = painterResource("icon_twins_4g.png"),
//    painter: Painter = painterResource(
//        hdRes {
//            HDRes.drawable.icon_twins_body_bg
//        }
//    ),
    @OptIn(ExperimentalResourceApi::class)
    resource: DrawableResourceFactory = {
        hdRes {
            HDRes.drawable.icon_twins_body_bg
        }
    },
    grey: Boolean = false,
) {
    Debug("TwinsBackgroundBlock")
    if (grey) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(app_background_brush)
        )
    } else {
        HDImage(
            resource = resource,
            contentScale = ContentScale.Crop,
            contentDescription = "background",
            modifier = modifier.fillMaxWidth()
        )
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun EmptyDeviceBlock(
    modifier: Modifier = Modifier,
    resource: DrawableResourceFactory = {
        hdRes {
            HDRes.drawable.icon_twins_no_device
        }
    },
    //painter: Painter = painterResource(DrawableResource("icon_twins_no_device.png")),
) {
    HDImage(
        resource = resource,
//        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = "EmptyDeviceBlock",
        modifier = modifier
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EmptyDataBlock(
    modifier: Modifier = Modifier,
    resource: DrawableResourceFactory = {
        hdRes {
            HDRes.drawable.icon_twins_no_data
        }
    },
    //painter: Painter = painterResource(DrawableResource("icon_twins_no_data.png")),
) {
    HDImage(
        resource = resource,
        contentScale = ContentScale.Crop,
        contentDescription = "EmptyDataBlock",
        modifier = modifier
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LabelTextBlock(
    modifier: Modifier = Modifier,
    text: String,
    resource: DrawableResourceFactory = {
        hdRes {
            HDRes.drawable.icon_twins_label_bg
        }
    },
    //painter: Painter = painterResource(DrawableResource("icon_twins_label_bg.png")),
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        HDImage(
            resource = resource,
            contentDescription = "bg"
        )
        Text(
            text = text, fontSize = 11.sp, color = Color(0x66666666), modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .padding(start = 6.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextBlock(
    modifier: Modifier = Modifier,
    text: String,
    hint: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    keyboardActions: KeyboardActions = KeyboardActions(),
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = text,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier,
        textStyle = Twins.twins_edit_text.copy(textAlign = TextAlign.End),
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = hint,
                style = Twins.twins_edit_text_hint.copy(textAlign = TextAlign.End)
            )
        },
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = China.r_luo_xia_hong,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent

        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextCenterBlock(
    modifier: Modifier = Modifier,
    text: String,
    hint: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    keyboardActions: KeyboardActions = KeyboardActions(),
    onValueChange: (String) -> Unit,
) {
    val textAlign = TextAlign.Center
    TextField(
        value = text,
        onValueChange = onValueChange,
        modifier = modifier.background(Color.Transparent),

        textStyle = Twins.twins_edit_text.copy(textAlign = textAlign),
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = hint,
                style = Twins.twins_edit_text_hint.copy(textAlign = textAlign)
            )
        },
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors().copy(
            cursorColor = China.r_luo_xia_hong,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,

        ), keyboardOptions = keyboardOptions, keyboardActions = keyboardActions
    )
}

@Composable
fun CheckedTextBlock(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
) {
    val style = if (checked) {
        Twins.twins_title.copy(color = app_appColor)
    } else {
        Twins.twins_title
    }
    val shape = RoundedCornerShape(22.dp)
    Text(
        text = text,
        style = style,
        modifier = modifier
            .clip(shape)
            .run {
                if (checked) {
                    this.border(width = 1.dp, color = app_appColor, shape = shape)
                } else {
                    this.background(Color(0xfff4f5f7))
                }
            }
            .clickable {
                onCheckedChanged.invoke(!checked)
            }
            .padding(vertical = 5.dp, horizontal = 16.dp)
    )
}


@Composable
fun CommitButtonBlockPreview() {
    CommitButtonBlock("ahhaha", true) {

    }
}

@Composable
fun CommitButtonBlock2(
    text: String,
    enable: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(app_button_brush)
            .clickable(enabled = enable) {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 16.sp,
                color = Color(1f, 1f, 1f, if (enable) 1f else .5f),
                fontWeight = FontWeight(400), textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            modifier = Modifier

        )
    }

}

@Composable
fun CommitButtonBlock(
    text: String,
    enable: Boolean = true,
    onClick: () -> Unit,
) {

    Text(
        text = text,
        //textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 16.sp,
            color = Color(1f, 1f, 1f, if (enable) 1f else .5f),
            fontWeight = FontWeight(400), //textAlign = TextAlign.Center,
        ),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 0.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(app_button_brush)
            .clickable(enabled = enable) {
                onClick()
            }
            .wrapContentHeight()
            .wrapContentWidth()


    )

}

// 文本居中示例
@Composable
private fun TextDemo() {
    Box(
        Modifier
            .background(China.g_zhu_lv)
            .padding(32.dp)
            .size(200.dp)
    ) {
        Text(
            text = "hello !",
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .background(China.r_luo_xia_hong)
                .wrapContentHeight()
                .wrapContentWidth(),
//            textAlign = TextAlign.Center
        )
    }
}

@Deprecated("delete")
@Composable
fun FloatActionBlock(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .size(58.dp)
            .padding(4.dp)
    ) {
        Surface(
            shadowElevation = 4.dp,
            modifier = Modifier

                .fillMaxSize()
                .clip(CircleShape)
                .drawWithContent {
                    drawCircle(app_button_brush)
                    drawContent()
                }

//                .size(58.dp)
//                .clip(CircleShape)


//        .background(China.r_luo_xia_hong)
//            , color = blue_1
        ) {
            Icon(
                Icons.Filled.Add,
                tint = Color.White,
                contentDescription = "add_device",
                modifier = Modifier
                    .wrapContentSize()
//                .background(China.g_zhu_lv)
            )

        }
    }
}

@Composable
fun DividerBlock(color: Color = app_textColor_999999, horizontal: Boolean = true) {
    Spacer(modifier = Modifier
        .run {
            if (horizontal) {
                this
                    .fillMaxWidth()
                    .height(.5.dp)
            } else {
                this
                    .width(.5.dp)
                    .fillMaxHeight()

            }

        }
        .background(color))
}