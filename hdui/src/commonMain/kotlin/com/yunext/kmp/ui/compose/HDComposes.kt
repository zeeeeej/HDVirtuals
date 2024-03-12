package com.yunext.kmp.ui.compose

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import com.yunext.kmp.resource.color.x98f
import com.yunext.kmp.resource.color.x9f8
import com.yunext.kmp.resource.color.xf98
import kotlin.random.Random

fun hdRandomColor(alpha: Float = 1f) = Color(
    alpha = alpha,
    red = Random.nextFloat(),
    green = Random.nextFloat(),
    blue = Random.nextFloat(),
)

val hdBrush = Brush.linearGradient(listOf(x9f8, x98f, xf98))

private val contentScales =
    arrayOf(
        ContentScale.Fit,
        ContentScale.Crop,
        ContentScale.FillBounds,
        ContentScale.FillWidth,
        ContentScale.Inside,
        ContentScale.None,
    )

fun randomContentScale(): ContentScale =
    (contentScales + FixedScale(Random.nextFloat())).random()


val ContentScale.desc: String
    get() {
        return when (this) {
            ContentScale.Fit -> "Fit"
            ContentScale.Crop -> "Crop"
            ContentScale.FillBounds -> "FillBounds"
            ContentScale.FillWidth -> "FillWidth"
            ContentScale.Inside -> "Inside"
            ContentScale.None -> "None"
            is FixedScale -> "FixedScale(${this.value})"
            else -> {
                this.toString()
            }
        }
    }
