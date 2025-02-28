package com.yunext.kmp.resource.color

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val xf98 = Color(0xFFFF9988)
val x98f = Color(0xFF8899ff)
val x9f8 = Color(0xFF99ff88)

val app_blue_1 = Color(0xff00DBE5)
val app_blue_2 = Color(0xff339DFF)
val app_zi = Color(0xff8B73F8)
val app_appColor = Color(0xff339DFF)
val app_textColor_333333 = Color(0xff333333)
val app_textColor_666666 = Color(0xff666666)
val app_textColor_999999 = Color(0xff999999)
val app_red = Color(0xffEC6161)
val app_red_light = app_red.copy(alpha = .1f)
val app_orange = Color(0xffFFA70B)
val app_orange_light = Color(0xffFFA70B).copy(alpha = .15f)
val app_blue_light = app_appColor.copy(alpha = .1f)
val app_gray = Color(0xffDEDFE0)
val app_green = Color(0xff00CBD3)
val app_gray_deep = Color(0xff949BA6)
val app_gray_f4f5f7 = Color(0xffF4F5F7)
val app_gray_light = app_gray.copy(alpha = .6f)
val app_background_70 = Color(1f, 1f, 1f, 0.7f)


val app_brush_item_content_spec = Brush.verticalGradient(colors = listOf(Color(0xffF7F8FA), Color(0xFFEFF0F2)))
val app_background_brush = Brush.verticalGradient(colors = listOf(Color(0xFFF4F7FB), Color(0xFFF0F4FC)))
val app_button_brush = Brush.linearGradient(colors = listOf(app_blue_1, app_blue_2))
val app_button_brush_debug = Brush.linearGradient(colors = listOf(China.r_yan_zhi_hong, China.g_bo_he_lv))
val app_search = Brush.linearGradient(colors = listOf(Color(0xffF0F4FC), Color(0xFFF4F7FB)))