package com.yunext.virtuals.ui.screen.logger

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.resource.HDRes
import com.yunext.kmp.resource.color.China
import com.yunext.kmp.resource.color.app_search
import com.yunext.kmp.resource.color.app_textColor_333333
import com.yunext.kmp.ui.compose.clickablePure
import com.yunext.kmp.ui.compose.hdClip
import com.yunext.kmp.ui.compose.hdBorder
import com.yunext.virtuals.ui.common.HDImage
import com.yunext.virtuals.ui.theme.Twins
import org.jetbrains.compose.resources.ExperimentalResourceApi
@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun InternalSearchBlock(
    modifier: Modifier,
    text: String,
    onChanged: (String) -> Unit,
    onSearch: () -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Row(
            Modifier.weight(1f)
                .hdClip(RoundedCornerShape(48.dp))
                .background(app_search)
//                .clip(RoundedCornerShape(48.dp))
                .padding(vertical = 0.dp)
        ) {
//            SystemBarsPadding {

            val trailingIcon: (@Composable () -> Unit)? =
                if (text.isNotEmpty()) {
                    @Composable {
                        HDImage(
                            resource = { HDRes.drawable.icon_twins_search_close },
                            contentDescription = null, modifier = Modifier.clickable {
                                onChanged("")
                            }
                        )
                    }
                } else null


            TextField(
                value = text,
                onValueChange = onChanged,
//                keyboardOptions = keyboardOptions,
//                keyboardActions = keyboardActions,
                modifier = Modifier.fillMaxWidth().hdBorder(debug = false),
                textStyle = Twins.twins_edit_text.copy(textAlign = TextAlign.End),
                placeholder = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "关键字",
                        style = Twins.twins_edit_text_hint.copy(textAlign = TextAlign.End)
                    )
                },
                singleLine = true,
                maxLines = 1,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    HDImage(
                        resource = { HDRes.drawable.icon_twins_search },
                        contentDescription = null
                    )
                },
                trailingIcon = trailingIcon,
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,

                    cursorColor = China.r_luo_xia_hong,


                    )
            )
//            }

        }

        Text(
            "搜索",
            modifier = Modifier
                .clickablePure(enabled = true) {
                    onSearch()
                }
                .padding(horizontal = 12.dp),

            style = TextStyle.Default.copy(
                fontSize = 14.sp,
                color = app_textColor_333333.copy(if (true) 1f else .5f)
            )
        )
    }
}


