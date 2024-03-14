package com.yunext.virtuals.ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import com.yunext.virtuals.ui.HDResProvider
import com.yunext.virtuals.ui.hdRes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
private fun HDImage(
    @OptIn(ExperimentalResourceApi::class)
    resource: DrawableResource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
) {
    @OptIn(ExperimentalResourceApi::class)
    Image(
        painterResource(resource),
        contentDescription,
        modifier,
        alignment,
        contentScale,
        alpha,
        colorFilter
    )
}

@OptIn(ExperimentalResourceApi::class)
typealias DrawableResourceFactory = HDResProvider.() -> DrawableResource

@Composable
fun HDImage(
    @OptIn(ExperimentalResourceApi::class)
    resource: DrawableResourceFactory,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
) {
    @OptIn(ExperimentalResourceApi::class)
    val res = hdRes {
        this.resource()
    }
    @OptIn(ExperimentalResourceApi::class)
    HDImage(res, contentDescription, modifier, alignment, contentScale, alpha, colorFilter)
}