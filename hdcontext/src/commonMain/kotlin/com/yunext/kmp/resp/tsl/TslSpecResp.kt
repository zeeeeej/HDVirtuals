package com.yunext.kmp.resp.tsl

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * 属性约束
 */
@Serializable
class TslSpecResp(
    /* 参数最小值（int、float、double类型特有） */
    val min: Int?,
    /* 参数最大值（int、float、double类型特有） */
    val max: Int?,
    /* 属性单位（int、float、double类型特有，非必填） */
    val unit: String?,
    /* 单位名称（int、float、double类型特有，非必填） */
    val unitName: String?,
    /* 数组元素的个数，最大512（array类型特有） */
    val size: Int?,
    /* 步长，属性值变化的最小粒度（text、enum类型无此参数） */
    val step: Int?,
    /* 数据长度，最大10240（text类型特有） */
    val length: Int?,
    /* 数组元素的类型（array类型特有） */
    val type: String?,
    /* struct格式的数据有多个properties */
    val item: JsonElement? = null,
    /* 枚举描述:enum和bool类型的参数信息 */
    val enumDesc: JsonElement? = null,
)