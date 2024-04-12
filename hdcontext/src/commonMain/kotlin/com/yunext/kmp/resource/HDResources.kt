package com.yunext.kmp.resource

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

// https://github.com/JetBrains/compose-multiplatform/issues/4111

@OptIn(InternalResourceApi::class)
@ExperimentalResourceApi
object HDRes {
    /**
     * Reads the content of the resource file at the specified path and returns it as a byte array.
     *
     * Example: `val bytes = Res.readBytes("files/key.bin")`
     *
     * @param path The path of the file to read in the compose resource's directory.
     * @return The content of the file as a byte array.
     */
    public suspend fun readBytes(path: String): ByteArray = readResourceBytes(path)

    public object drawable

    public object string

    public object font
}

@OptIn(ExperimentalResourceApi::class)
fun hdRes(block: HDResProvider.() -> DrawableResource): DrawableResource {
    return with(HDRes.drawable) {
        hdResProvider.block()
    }
}

suspend fun hdResFiles(fileName: String):ByteArray {
    return hdResProvider.readFiles(fileName)
}

private lateinit var hdResProviderInternal: HDResProvider

internal val hdResProvider: HDResProvider
    get() = if (::hdResProviderInternal.isInitialized) hdResProviderInternal else throw IllegalArgumentException("use ::initHDRes 初始化")

fun initHDRes(provider: HDResProvider) {
    if (::hdResProviderInternal.isInitialized) {
        //error("hdRes has init.")
        return
    }
    hdResProviderInternal = provider
}

@OptIn(ExperimentalResourceApi::class)
interface HDResProvider {
    val HDRes.drawable.ic_app: DrawableResource
    val HDRes.drawable.icon_app_hadlinks: DrawableResource
    val HDRes.drawable.icon_twins_4g: DrawableResource
    val HDRes.drawable.icon_twins_add_btn_click: DrawableResource
    val HDRes.drawable.icon_twins_add_btn_nor: DrawableResource
    val HDRes.drawable.icon_twins_add_click: DrawableResource
    val HDRes.drawable.icon_twins_add_nor: DrawableResource
    val HDRes.drawable.icon_twins_body_bg: DrawableResource
    val HDRes.drawable.icon_twins_checked: DrawableResource
    val HDRes.drawable.icon_twins_close_btn: DrawableResource
    val HDRes.drawable.icon_twins_close_click: DrawableResource
    val HDRes.drawable.icon_twins_close_nor: DrawableResource
    val HDRes.drawable.icon_twins_distribution_network: DrawableResource
    val HDRes.drawable.icon_twins_fail: DrawableResource
    val HDRes.drawable.icon_twins_label_bg: DrawableResource
    val HDRes.drawable.icon_twins_label_short_bg: DrawableResource
    val HDRes.drawable.icon_twins_log: DrawableResource
    val HDRes.drawable.icon_twins_more_click: DrawableResource
    val HDRes.drawable.icon_twins_more_nor: DrawableResource
    val HDRes.drawable.icon_twins_no_data: DrawableResource
    val HDRes.drawable.icon_twins_no_device: DrawableResource
    val HDRes.drawable.icon_twins_offline: DrawableResource
    val HDRes.drawable.icon_twins_on_btn: DrawableResource
    val HDRes.drawable.icon_twins_refresh: DrawableResource
    val HDRes.drawable.icon_twins_return_click: DrawableResource
    val HDRes.drawable.icon_twins_return_nor: DrawableResource
    val HDRes.drawable.icon_twins_search: DrawableResource
    val HDRes.drawable.icon_twins_search_close: DrawableResource
    val HDRes.drawable.icon_twins_set_up: DrawableResource
    val HDRes.drawable.icon_twins_success: DrawableResource
    val HDRes.drawable.icon_twins_uncheck: DrawableResource
    val HDRes.drawable.icon_twins_wifi: DrawableResource

    suspend fun readFiles(fileName:String):ByteArray
}

