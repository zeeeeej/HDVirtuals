package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.ui.compose.hdBackground
import com.yunext.virtuals.ui.common.EditTextBlock
import com.yunext.virtuals.ui.common.dialog.debugShape

@Composable
internal fun ParameterView(list: List<ParameterDataVo>,effect: SetPropertyEffect, onChanged: (ParameterDataVo) -> Unit) {
    var grid by remember {
        mutableStateOf(false)
    }
    Column {
        Text("参数列表", modifier = Modifier.clickable {
            grid = !grid
        })
        Text(when(effect){
            is SetPropertyEffect.Fail -> "设置${effect.key}=${effect.value}失败：${effect.msg}"
            SetPropertyEffect.Idle -> "等待设置"
            is SetPropertyEffect.Start -> "设置${effect.key}=${effect.value}中..."
            is SetPropertyEffect.Success -> "设置${effect.key}=${effect.value}成功！"
        }, modifier = Modifier.clickable {

        },color = Color.Gray)
        ParameterDataList(grid = grid, list = list, onChanged = onChanged)
    }

}


@Composable
private fun ParameterDataList(
    grid: Boolean,
    list: List<ParameterDataVo>,
    onChanged: (ParameterDataVo) -> Unit,
) {
    Box(Modifier.fillMaxWidth().height(360.dp)
        .hdBackground { Color.LightGray }
//        .debugShape(true)
        .padding(16.dp)) {
        if (grid) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(list, {
                    it.key
                }) {
                    ParameterDataItemGrid(it, onChanged)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(list, { it.key }) {
                    ParameterDataItem(it, onChanged)
                }
            }
        }

    }
}

@Composable
private fun ParameterDataItem(data: ParameterDataVo, onChanged: (ParameterDataVo) -> Unit) {
    var value by remember(data.value) {
        mutableStateOf(data.value)
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            data.key,
            modifier = Modifier,
            style = TextStyle.Default.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            "(${data.title})",
            modifier = Modifier,
            style = TextStyle.Default.copy(fontSize = 18.sp, fontWeight = FontWeight.Normal)
        )
        EditTextBlock(
            text = value, hint = "请输入", onValueChange = {
                value = it
            }, modifier = Modifier.weight(1f).debugShape(true),
            trailingIcon = {
                Image(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onChanged(data.copy(value = value))
                    })
            }
        )


    }
}

@Composable
private fun ParameterDataItemGrid(data: ParameterDataVo, onChanged: (ParameterDataVo) -> Unit) {
    var value by remember(data.value) {
        mutableStateOf(data.value)
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                data.key,
                modifier = Modifier,
                style = TextStyle.Default.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                "(${data.title})",
                modifier = Modifier,
                style = TextStyle.Default.copy(fontSize = 14.sp, fontWeight = FontWeight.Normal)
            )
        }

        EditTextBlock(
            text = value, hint = "请输入", onValueChange = {
                value = it
            }, modifier = Modifier.fillMaxWidth().debugShape(true),
            trailingIcon = {
                Image(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onChanged(data.copy(value = value))
                    })
            }
        )


    }
}