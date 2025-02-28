package com.yunext.kmp.resp.tsl

import com.yunext.kmp.resp.tsl.TslParamResp
import kotlinx.serialization.Serializable

@Serializable
class TslServiceResp(
    val identifier: String?,
    val name: String?,
    /* async（异步调用）或sync（同步调用）*/
    val callType: String?,
    /* 是否是标准功能的必选属性 */
    val required: Boolean?,
    val desc: String?,
    /* 事件对应的方法名称（根据identifier生成） */
    val method: String?,
    /* 输入参数 */
    val inputData: List<TslParamResp>?,
    /* 输出参数 */
    val outputData: List<TslParamResp>?

    )