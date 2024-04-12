package com.yunext.kmp.mqtt.virtuals.protocol.tsl

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.virtuals.protocol.hdJson
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.BoolPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.BooleanPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoubleArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoubleArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DoublePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.FloatPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextArrayPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextArrayPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextEnumPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextEnumPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.TextPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.isEmpty
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.toPropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleParsePropertyKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleToJsonValues
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.addAll
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

interface TslValueParser {
    /**
     * 多个PropertyValue<*>拼接成json
     * {
     *    "signalStrength": 1,
     *    "TestKey": -99
     * }
     * @param values 要发送的值
     * @return json
     */
    fun toJson(values: List<PropertyValue<*>>): String

    /**
     * 根据tsl解析json转换成PropertyValue List
     * 比如 params部分
     *
     * Topic: /skeleton/tcuf6vn2ohw4mvhb/twins_test_001_cid/downQoS: 0
     *
     * {
     *   "cmd": "set",
     *   "params": {
     *     "signalStrength": -111,
     *     "abc": "hello",
     *     "version":"1.0.0",
     *     "connectExceedTim":"1234567890"
     *   }
     * }
     * 解析params部分应该得到[IntPropertyValue(-111),TextPropertyValue("1.0.0"),TextPropertyValue("1234567890")]
     * 参考 tslHandleUpdatePropertyValueFromJson
     */
    fun fromJson(tsl: Tsl, json: String): List<PropertyValue<*>>

    companion object : TslValueParser by KotlinSerializableTslParser()
}


class KotlinSerializableTslParser(private val kjson: Json = hdJson) : TslValueParser {


    override fun toJson(values: List<PropertyValue<*>>): String {
        val result =
            buildJsonObject {
                values.forEach { propertyValue ->
                    toJsonSingle(propertyValue)
                }
            }

        return kjson.encodeToString(result).also {
            HDLogger.d("KotlinSerializableTslParser", "json = $it")
        }
    }


    override fun fromJson(tsl: Tsl, json: String): List<PropertyValue<*>> {
        val properties = tsl.properties


        fun applyProperty(propertyKey: PropertyKey): PropertyValue<*>? {
            val jsonObject = hdJson.decodeFromString<JsonObject>(json)
            if (jsonObject.isEmpty()) {
                return null
            }

            val identifier = propertyKey.identifier
            return when (propertyKey) {
                is DoubleArrayPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonArray
                    if (value.isNullOrEmpty()) {
                        null
                    } else {
                        val list: List<Double> =
                            value.mapNotNull {
                                it.jsonPrimitive.doubleOrNull
                            }
                        DoubleArrayPropertyValue(propertyKey, list)
                    }
                }

                is FloatArrayPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonArray
                    if (value.isNullOrEmpty()) {
                        null
                    } else {
                        val list: List<Float> =
                            value.mapNotNull {
                                it.jsonPrimitive.floatOrNull
                            }
                        FloatArrayPropertyValue(propertyKey, list)
                    }
                }

                is IntArrayPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonArray
                    if (value.isNullOrEmpty()) {
                        null
                    } else {
                        val list: List<Int> =
                            value.mapNotNull {
                                it.jsonPrimitive.intOrNull
                            }
                        IntArrayPropertyValue(propertyKey, list)
                    }
                }

                is StructArrayPropertyKey -> {
                    // [ {"a":1,"b":"2"} , {"a":1,"b":"2"} , {"a":1,"b":"2"}]
                    val value = jsonObject[identifier]?.jsonArray
                    if (value.isNullOrEmpty()) {
                        null
                    } else {
                        // todo propertyKey.itemKeys

                        val list = value.map { item ->
                            val innerJsonObject = item.jsonObject

                            // 遍历keys 即a b 得到value
                            val vs = propertyKey.itemKeys.mapNotNull { k ->
                                val v = applyProperty(k)
                                if (v == null) null else k to v
                            }.toMap()

                            //item:  {"a":1,"b":"2"}
//                            val innerKey: StructPropertyKey =
//                                StructPropertyKey.fake(vs.map { it.key })
//                            val innerValue: Map<PropertyKey, PropertyValue<*>> = vs
//                            StructPropertyValue(innerKey, innerValue)
                            vs
                        }
                        StructArrayPropertyValue(propertyKey, list)
                    }
                }

                is TextArrayPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonArray
                    if (value.isNullOrEmpty()) {
                        null
                    } else {
                        val list: List<String> =
                            value.mapNotNull {
                                it.jsonPrimitive.contentOrNull
                            }
                        TextArrayPropertyValue(propertyKey, list)
                    }
                }

                is BooleanPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonPrimitive?.intOrNull
                    if (value == null) {
                        null
                    } else {
                        BoolPropertyValue(propertyKey, value)
                    }
                }

                is DatePropertyKey -> {
                    val value = jsonObject[identifier]?.jsonPrimitive?.contentOrNull
                    if (value == null) {
                        null
                    } else {
                        DatePropertyValue(propertyKey, value)
                    }
                }

                is DoublePropertyKey -> {
                    val value = jsonObject[identifier]?.jsonPrimitive?.doubleOrNull
                    if (value == null) {
                        null
                    } else {
                        DoublePropertyValue(propertyKey, value)
                    }
                }

                is IntEnumPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonPrimitive?.intOrNull
                    if (value == null) {
                        null
                    } else {
                        val spec = propertyKey.specs.singleOrNull() {
                            value == it.value
                        }
                        IntEnumPropertyValue(
                            propertyKey,
                            IntEnumPropertyValue.KeyValue(value, spec?.desc ?: "")
                        )
                    }
                }

                is TextEnumPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonPrimitive?.contentOrNull
                    if (value == null) {
                        null
                    } else {
                        val spec = propertyKey.specs.singleOrNull() {
                            value == it.value
                        }
                        TextEnumPropertyValue(
                            propertyKey,
                            TextEnumPropertyValue.KeyValue(value, spec?.desc ?: "")
                        )
                    }
                }

                is FloatPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonPrimitive?.floatOrNull
                    if (value == null) {
                        null
                    } else {
                        FloatPropertyValue(propertyKey, value)
                    }
                }

                is IntPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonPrimitive?.intOrNull
                    if (value == null) {
                        null
                    } else {
                        IntPropertyValue(propertyKey, value)
                    }
                }

                is StructPropertyKey -> {
                    // {"twins_struct":{"twins_struct_a":55,"twins_struct_b":"hhhhh","twins_struct_c":0.58866,"twins_struct_d":"bbbbb","twins_struct_e":true,"twins_struct_f":1,"twins_struct_g":"dong"}}}
                    val value = jsonObject[identifier]?.jsonObject
                    if (value == null) {
                        null
                    } else {
                        val fake = propertyKey.fake // TODO 处理fake
                        val itemValues: Map<PropertyKey, PropertyValue<*>> =
                            propertyKey.items.mapNotNull { innerPropertyKey ->
                                val values = applyProperty(innerPropertyKey)
                                if (values != null) innerPropertyKey to values else null
                            }.toMap()
                        StructPropertyValue(
                            propertyKey,
                            itemValues
                        )
                    }
                }

                is TextPropertyKey -> {
                    val value = jsonObject[identifier]?.jsonPrimitive?.contentOrNull
                    if (value == null) {
                        null
                    } else {
                        TextPropertyValue(propertyKey, value)
                    }
                }

                null -> null
            }

        }

        val list = properties.mapNotNull { property ->
            // 遍历 json里有就加入，否则null
            val propertyKey = property.toPropertyKey()
            if (propertyKey == null) {
                null
            } else {
                val propertyValue: PropertyValue<*>? = applyProperty(propertyKey)
                propertyValue
            }
        }
        return list
    }

    /**
     * 构建一个json对象发送出去
     *
     *
     *
     */
    @OptIn(ExperimentalSerializationApi::class)
    private fun JsonObjectBuilder.toJsonSingle(
        propertyValue: PropertyValue<*>,
        structInArray: Boolean = false,
    ) {
        when (propertyValue) {
            is DoubleArrayPropertyValue -> {
                val propertyKey = propertyValue.key
                val size = propertyKey.size
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                put(key, buildJsonArray {
                    addAll(value.take(size))
                })
            }

            is FloatArrayPropertyValue -> {
                val propertyKey = propertyValue.key
                val size = propertyKey.size
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                put(key, buildJsonArray {
                    addAll(value.take(size))
                })
            }

            is IntArrayPropertyValue -> {
                val propertyKey = propertyValue.key
                val size = propertyKey.size
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                put(key, buildJsonArray {
                    addAll(value.take(size))
                })
            }

            is StructArrayPropertyValue -> {
                val propertyKey = propertyValue.key
                val size = propertyKey.size
                val key = propertyValue.key.identifier
                val sourceList = propertyValue.value
                put(key, buildJsonArray {
                    val jsonObjectList: List<JsonObject> = sourceList.take(size)
                        .map { structPropertyValue ->
                            buildJsonObject {
                                structPropertyValue.forEach { (k, v) ->
                                    this.toJsonSingle(v, true)
                                }
                            }
                        }
                    addAll(jsonObjectList)
                })
            }

            is TextArrayPropertyValue -> {
                val propertyKey = propertyValue.key
                val size = propertyKey.size
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                put(key, buildJsonArray {
                    addAll(value.take(size))
                })
            }

            is BoolPropertyValue -> {
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                if (value == null) {
                    if (structInArray) {
                        put(key, false)
                    } else {
                        put(key, null)
                    }
                } else {
                    put(key, value)
                }

            }

            is DatePropertyValue -> {
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                if (value == null) {
                    if (structInArray) {
                        put(key, "")
                    } else {
                        put(key, null)
                    }
                } else {
                    put(key, value)
                }
            }

            is DoublePropertyValue -> {
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                if (value == null) {
                    if (structInArray) {
                        put(key, 0.0)
                    } else {
                        put(key, null)
                    }
                } else {
                    put(key, value)
                }

            }

            is FloatPropertyValue -> {
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                if (value == null) {
                    if (structInArray) {
                        put(key, 0f)
                    } else {
                        put(key, null)
                    }
                } else {
                    put(key, value)
                }
            }

            is IntEnumPropertyValue -> {

                val key = propertyValue.key.identifier

                val value = propertyValue.keyValue.value
                if (propertyValue.isEmpty()) {
                    if (structInArray) {
                        put(key, 0)
                    } else {
                        put(key, null)
                    }
                } else {
                    put(key, value)
                }
            }

            is IntPropertyValue -> {
                val key = propertyValue.key.identifier

                val value = propertyValue.value
                if (value == null) {
                    if (structInArray) {
                        put(key, 0)
                    } else {
                        put(key, null)
                    }
                } else {
                    put(key, value)
                }
            }

            is StructPropertyValue -> {
                val key = propertyValue.key.identifier
                val itemValues = propertyValue.itemValues
                put(key, buildJsonObject {
                    itemValues.forEach { (k, v) ->
                        this.toJsonSingle(v)
                    }
                })
            }

            is TextEnumPropertyValue -> {
                val key = propertyValue.key.identifier
                val value = propertyValue.keyValue.value
                if (propertyValue.isEmpty()) {
                    if (structInArray) {
                        put(key, "")
                    } else {
                        put(key, null)
                    }
                } else {
                    put(key, value)
                }
            }

            is TextPropertyValue -> {
                val key = propertyValue.key.identifier
                val value = propertyValue.value
                if (value == null) {
                    if (structInArray) {
                        put(key, "")
                    } else {
                        put(key, null)
                    }
                } else {
                    put(key, value)
                }
            }
        }
    }
}

@Deprecated("by gson")
class GsonTslParser(private val gson: Gson) : TslValueParser {

    override fun toJson(values: List<PropertyValue<*>>): String {
        val result = values.tslHandleToJsonValues()
        return gson.toJSon(result).also {
            HDLogger.d("GsonTslParser", "json = $it")
        }
    }

    override fun fromJson(tsl: Tsl, json: String): List<PropertyValue<*>> {
        TODO("Not yet implemented")
    }

    interface Gson {
        fun toJSon(source: Any): String
    }

}



