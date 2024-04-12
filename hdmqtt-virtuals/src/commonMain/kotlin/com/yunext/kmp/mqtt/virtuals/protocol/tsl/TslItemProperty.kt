package com.yunext.kmp.mqtt.virtuals.protocol.tsl

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.virtuals.protocol.hdJson
import com.yunext.kmp.mqtt.virtuals.repository.convert
import com.yunext.kmp.resp.tsl.TslItemPropertyResp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Struct内部属性集合
 */
@Serializable
class TslItemProperty(

    val identifier: String,
    val name: String,
    /*
     * DataTypeEnum
     * 属性类型
     * int
     * float
     * double
     * text
     * date（String类型UTC毫秒）
     * bool（0或1的int类型）
     * enum（int类型，枚举项定义方法与bool类型定义0和1的值方法相同）
     * struct（结构体类型，可包含前面7种类型，下面使用"specs":[{}]描述包含的对象）
     * array（数组类型，支持int、double、float、text、struct）
     */
    val dataType: TslPropertyType,
    val specs: TslSpec?,
) {
    companion object {
//        private val gson: Gson = Gson()
//        private val TYPE = object : TypeToken<List<TslItemPropertyResp>>() {
//
//        }.type


        fun from(json: String): List<TslItemProperty> {
            return try {
                hdJson.decodeFromString<List<TslItemPropertyResp>>(json)
                    .map(TslItemPropertyResp::convert)
            } catch (e: Throwable) {
                HDLogger.d("TslItemProperty::from", "e:$e")
                e.printStackTrace()
                listOf()
            }
        }

        fun from(element: JsonElement): List<TslItemProperty> {
            return try {
                hdJson.decodeFromString<List<TslItemProperty>>(element.toString())
            } catch (e: Throwable) {
                listOf()
            }
        }
    }
}

//typealias Item = @Serializable List<TslItemProperty>

internal val TslItemProperty.display: String
    get() {
        return "$name<$identifier> [$dataType] "
    }





