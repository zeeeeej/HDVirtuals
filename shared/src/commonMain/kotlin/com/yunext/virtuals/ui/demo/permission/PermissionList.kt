package com.yunext.virtuals.ui.demo.permission

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.common.util.currentTime
import com.yunext.virtuals.ui.common.dialog.DialogDefaults
import com.yunext.virtuals.ui.common.dialog.debugShape
import kotlinx.coroutines.launch

@Composable
fun PermissionList(
    modifier: Modifier,
    list: List<PermissionData>,
    onStatusChanged: (XPermission, XPermissionStatus) -> Unit,
) {
    LaunchedEffect(list) {
        HDLogger.d("PermissionRequestView", "[list]$list")
    }
    LazyColumn(
        modifier.debugShape(true).background(Color.Black),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list, key = { it.permission }) {
            Box(
                Modifier.fillMaxWidth().height(ITEM_HEIGHT.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                when (it.status) {
                    XPermissionStatus.Granted -> PermissionGrantedItem(it)
                    XPermissionStatus.DENIED -> PermissionDeniedItem(it) { status ->
                        HDLogger.d(
                            "PermissionRequestView",
                            "PermissionList 上抛 ${it.permission}->$status"
                        )
                        onStatusChanged(it.permission, status)
                    }
                }
            }
            Divider(Modifier.fillMaxWidth().background(Color.White))

        }


    }
}

internal const val ITEM_HEIGHT = 44

@Composable
private fun PermissionGrantedItem(permission: PermissionData) {

    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            permission.permission.text, style = TextStyle.Default.copy(
                color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
            ), modifier = Modifier.weight(1f)
        )
        Image(
            Icons.Default.Done,
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "granted",
            modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(4.dp)
        )
    }

}

@Composable
private fun PermissionDeniedItem(
    permission: PermissionData,
    onStatusChanged: (XPermissionStatus) -> Unit,
) {
    var key: Long? by remember(permission) { mutableStateOf(null) }


    Row(
        Modifier.fillMaxSize().clickable(onClick = { key = currentTime() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            permission.permission.text, style = TextStyle.Default.copy(
                color = Color.Red, fontSize = 18.sp, fontWeight = FontWeight.Bold
            ), modifier = Modifier.weight(1f)
        )
        Image(
            imageVector = Icons.Default.Warning,
            colorFilter = ColorFilter.tint(Color.Red),
            contentDescription = "denied",
            modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(4.dp)
        )

        PermissionRequestView(permission.permission, key, onStatusChanged = {
            key = null
            HDLogger.d("PermissionRequestView", "PermissionDeniedItem 上抛 $it")
            onStatusChanged(it)
        })
    }




}