package com.yunext.kmp.mqtt.virtuals.protocol.tsl.property

import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslException
import kotlinx.serialization.Serializable

//<editor-fold desc="#PropertyValue">
@Serializable
sealed interface PropertyValue<out KEY : PropertyKey> : DisplayValue<String> {
    val key: KEY
}

interface DisplayValue<T : Any> {
    val displayValue: T
}

interface DefaultValue<in T, in K : PropertyKey, out R : PropertyValue<*>> {
    fun createValue(key: K?, source: T? = null): R
}

private const val UNKNOWN_VALUE = "未知"
//</editor-fold>

//<editor-fold desc="##IntPropertyValue">
@Serializable
class IntPropertyValue(override val key: IntPropertyKey, val value: Int?) :
    PropertyValue<IntPropertyKey> {
    override val displayValue: String
        get() = "${value ?: UNKNOWN_VALUE}"

    companion object : DefaultValue<Int, IntPropertyKey, IntPropertyValue> {
        override fun createValue(
            key: IntPropertyKey?,
            source: Int?,
        ): IntPropertyValue {
            return IntPropertyValue(key ?: IntPropertyKey.fakeKey(), source)
        }
    }
}
//</editor-fold>

//<editor-fold desc="##BoolPropertyValue">
@Serializable
class BoolPropertyValue(
    override val key: BooleanPropertyKey,
    val value: Int?,
) : PropertyValue<BooleanPropertyKey> {

    override val displayValue: String
        get() {
            return this.key.specs.singleOrNull {
                it.value == value
            }?.desc ?: UNKNOWN_VALUE
        }

    @Serializable
    data class KeyValue(val value: Int?, val desc: String) {
        companion object {
            val EMPTY = KeyValue(null, "")
        }
    }

    companion object : DefaultValue<Int, BooleanPropertyKey, BoolPropertyValue> {
        private fun from(key: BooleanPropertyKey, value: Int? = null) =
            BoolPropertyValue(key, key.specs.singleOrNull {
                value == it.value
            }?.value)

        override fun createValue(
            key: BooleanPropertyKey?,
            source: Int?,
        ): BoolPropertyValue {
            return from(key ?: BooleanPropertyKey.fakeKey())
        }
    }
}

val BoolPropertyValue.keyValue: BoolPropertyValue.KeyValue?
    get() = if (value == null) null else keyValues.singleOrNull {
        value == it.value
    }

val BoolPropertyValue.keyValues
    get() = this.key.specs.map {
        BoolPropertyValue.KeyValue(it.value, it.desc)
    }

fun BoolPropertyValue.KeyValue.isEmpty(): Boolean {
    return this == BoolPropertyValue.KeyValue.EMPTY
}
//</editor-fold>

//<editor-fold desc="##FloatPropertyValue">
@Serializable
class FloatPropertyValue(override val key: FloatPropertyKey, val value: Float?) :
    PropertyValue<FloatPropertyKey> {
    override val displayValue: String
        get() = "${value ?: UNKNOWN_VALUE}"

    companion object : DefaultValue<Float, FloatPropertyKey, FloatPropertyValue> {
        override fun createValue(
            key: FloatPropertyKey?,
            source: Float?,
        ): FloatPropertyValue {
            return FloatPropertyValue(key ?: FloatPropertyKey.fakeKey(), source)
        }
    }
}
//</editor-fold>

//<editor-fold desc="##DoublePropertyValue">
@Serializable
class DoublePropertyValue(override val key: DoublePropertyKey, val value: Double?) :
    PropertyValue<DoublePropertyKey> {
    override val displayValue: String
        get() = "${value ?: UNKNOWN_VALUE}"

    companion object : DefaultValue<Double, DoublePropertyKey, DoublePropertyValue> {
        override fun createValue(
            key: DoublePropertyKey?,
            source: Double?,
        ): DoublePropertyValue {
            return DoublePropertyValue(key ?: DoublePropertyKey.fakeKey(), source)
        }
    }
}
//</editor-fold>

//<editor-fold desc="##TextPropertyValue">
@Serializable
class TextPropertyValue(override val key: TextPropertyKey, val value: String?) :
    PropertyValue<TextPropertyKey> {
    override val displayValue: String
        get() = value ?: UNKNOWN_VALUE

    companion object : DefaultValue<String, TextPropertyKey, TextPropertyValue> {
        override fun createValue(
            key: TextPropertyKey?,
            source: String?,
        ): TextPropertyValue {
            return TextPropertyValue(key ?: TextPropertyKey.fakeKey(), source)
        }
    }
}
//</editor-fold>

//<editor-fold desc="##DatePropertyValue">
@Serializable
class DatePropertyValue(override val key: DatePropertyKey, val value: String?) :
    PropertyValue<DatePropertyKey> {
    override val displayValue: String
        get() = value ?: UNKNOWN_VALUE

    companion object : DefaultValue<String, DatePropertyKey, DatePropertyValue> {
        override fun createValue(
            key: DatePropertyKey?,
            source: String?,
        ): DatePropertyValue {
            return DatePropertyValue(key ?: DatePropertyKey.fakeKey(), source)
        }

    }
}


val DatePropertyValue.longValue: Long?
    get() = try {
        this.value?.toLong()
    } catch (e: Throwable) {
        null
    }

//</editor-fold>

//<editor-fold desc="##IntEnumPropertyValue">
@Serializable
class IntEnumPropertyValue(override val key: IntEnumPropertyKey, val keyValue: KeyValue) :
    PropertyValue<IntEnumPropertyKey> {
    override val displayValue: String
        get() = if (isEmpty()) UNKNOWN_VALUE else "[${keyValue.value}]${keyValue.desc}"

    @Serializable
    data class KeyValue(val value: Int?, val desc: String) {
        companion object {
            internal val EMPTY = KeyValue(null, "")
        }
    }

    companion object : DefaultValue<Int, IntEnumPropertyKey, IntEnumPropertyValue> {
        private fun from(key: IntEnumPropertyKey, value: Int? = null) =
            IntEnumPropertyValue(key, key.specs.singleOrNull {
                value == it.value
            } ?: KeyValue.EMPTY)

        override fun createValue(
            key: IntEnumPropertyKey?,
            source: Int?,
        ): IntEnumPropertyValue {
            return from(key ?: IntEnumPropertyKey.fakeKey(), source)
        }
    }
}

fun IntEnumPropertyValue.isEmpty(): Boolean {
    return keyValue == IntEnumPropertyValue.KeyValue.EMPTY
}

fun IntEnumPropertyValue.KeyValue.isEmpty(): Boolean {
    return this == IntEnumPropertyValue.KeyValue.EMPTY
}

val IntEnumPropertyValue.keyValues: List<IntEnumPropertyValue.KeyValue>
    get() = this.key.specs

//</editor-fold>

//<editor-fold desc="##TextEnumPropertyValue">
@Serializable
class TextEnumPropertyValue(override val key: TextEnumPropertyKey, val keyValue: KeyValue) :
    PropertyValue<TextEnumPropertyKey> {
    override val displayValue: String
        get() = if (isEmpty()) UNKNOWN_VALUE else "[${keyValue.value}]${keyValue.desc}"

    @Serializable
    data class KeyValue(val value: String, val desc: String) {
        companion object {
            internal val EMPTY = KeyValue("", "")
        }
    }

    companion object : DefaultValue<String, TextEnumPropertyKey, TextEnumPropertyValue> {
        private fun from(key: TextEnumPropertyKey, value: String? = null) =
            TextEnumPropertyValue(key, key.specs.singleOrNull {
                value == it.value
            } ?: KeyValue.EMPTY)

        override fun createValue(
            key: TextEnumPropertyKey?,
            source: String?,
        ): TextEnumPropertyValue {
            return from(key = key ?: TextEnumPropertyKey.fakeKey())
        }
    }

}

fun TextEnumPropertyValue.isEmpty(): Boolean {
    return keyValue == TextEnumPropertyValue.KeyValue.EMPTY
}

fun TextEnumPropertyValue.KeyValue.isEmpty(): Boolean {
    return this == TextEnumPropertyValue.KeyValue.EMPTY
}

val TextEnumPropertyValue.keyValues: List<TextEnumPropertyValue.KeyValue>
    get() = this.key.specs

//</editor-fold>

//<editor-fold desc="##ArrayPropertyValue">
@Serializable
sealed class ArrayPropertyValue(
    override val key: ArrayPropertyKey,
) : PropertyValue<ArrayPropertyKey> {

    companion object : DefaultValue<List<PropertyValue<*>>, ArrayPropertyKey, ArrayPropertyValue> {
        override fun createValue(
            key: ArrayPropertyKey?,
            source: List<PropertyValue<*>>?,
        ): ArrayPropertyValue {
            val list = source ?: emptyList()
            val tempKey = key ?: throw IllegalStateException("不能确定key,无法确定item类型")
            return when (tempKey) {
                is DoubleArrayPropertyKey -> {
                    val values = list.mapNotNull { propertyValue ->
                        if (propertyValue is DoublePropertyValue) {
                            propertyValue.value ?: 0.0
                        } else null
                    }
                    DoubleArrayPropertyValue(tempKey, values)
                }

                is FloatArrayPropertyKey -> {
                    val values = list.mapNotNull { propertyValue ->
                        if (propertyValue is FloatPropertyValue) {
                            propertyValue.value ?: 0f
                        } else null
                    }
                    FloatArrayPropertyValue(tempKey, values)
                }

                is IntArrayPropertyKey -> {
                    val values = list.mapNotNull { propertyValue ->
                        if (propertyValue is IntPropertyValue) {
                            propertyValue.value ?: 0
                        } else null
                    }
                    IntArrayPropertyValue(tempKey, values)
                }

                is StructArrayPropertyKey -> {
                    val values = list.map { propertyValue ->
                        if (propertyValue is StructPropertyValue) {
                            propertyValue.itemValues
                        } else emptyMap()
                    }
                    StructArrayPropertyValue(tempKey, values)
                }

                is TextArrayPropertyKey -> {
                    val values = list.mapNotNull { propertyValue ->
                        if (propertyValue is TextPropertyValue) {
                            propertyValue.value ?: ""
                        } else null
                    }
                    TextArrayPropertyValue(tempKey, values)
                }
            }
        }

    }
}

class IntArrayPropertyValue(
    override val key: IntArrayPropertyKey,
    val value: List<Int>,
) : ArrayPropertyValue(key) {
    override val displayValue: String
        get() = if (value.isEmpty()) "[${UNKNOWN_VALUE}]" else "[\n" + value.joinToString(",") + "\n]"
}

class FloatArrayPropertyValue(
    override val key: FloatArrayPropertyKey,
    val value: List<Float>,
) : ArrayPropertyValue(key) {
    override val displayValue: String
        get() = if (value.isEmpty()) "[${UNKNOWN_VALUE}]" else "[\n" + value.joinToString(",") + "\n]"
}

class DoubleArrayPropertyValue(
    override val key: DoubleArrayPropertyKey,
    val value: List<Double>,
) : ArrayPropertyValue(key) {
    override val displayValue: String
        get() = if (value.isEmpty()) "[${UNKNOWN_VALUE}]" else "[\n" + value.joinToString(",") + "\n]"

}


class TextArrayPropertyValue(
    override val key: TextArrayPropertyKey,
    val value: List<String>,
) : ArrayPropertyValue(key) {
    override val displayValue: String
        get() = if (value.isEmpty()) "[${UNKNOWN_VALUE}]" else "[\n" + value.joinToString(",") + "\n]"
}

class StructArrayPropertyValue(
    override val key: StructArrayPropertyKey,
    val value: List<Map<PropertyKey, PropertyValue<*>>>,
) : ArrayPropertyValue(key) {
    override val displayValue: String
        get() = if (value.isEmpty()) "[${UNKNOWN_VALUE}]" else "[\n" + value.joinToString("") {
            "${it}\n"
        } + "\n]"
}
//</editor-fold>

//<editor-fold desc="##StructPropertyValue">
@Serializable
class StructPropertyValue(
    override val key: StructPropertyKey,
    val itemValues: Map<PropertyKey, PropertyValue<*>>,
) : PropertyValue<StructPropertyKey>, DisplayValue<String> {
    override val displayValue: String
        get() = "{\n" + itemValues.values.map { v ->
            "${v.key.identifier} : " +
                    when (v) {
                        is DoubleArrayPropertyValue -> throw TslException("StructPropertyValue属性值${this.key.type} 不支持 ${v.key.type}")
                        is FloatArrayPropertyValue -> throw TslException("StructPropertyValue属性值${this.key.type} 不支持 ${v.key.type}")
                        is IntArrayPropertyValue -> throw TslException("StructPropertyValue属性值${this.key.type} 不支持 ${v.key.type}")
                        is TextArrayPropertyValue -> throw TslException("StructPropertyValue属性值${this.key.type} 不支持 ${v.key.type}")
                        is BoolPropertyValue -> v.displayValue
                        is DatePropertyValue -> v.displayValue
                        is DoublePropertyValue -> v.displayValue
                        is FloatPropertyValue -> v.displayValue
                        is IntEnumPropertyValue -> v.displayValue
                        is IntPropertyValue -> v.displayValue
                        is StructPropertyValue -> throw TslException("StructPropertyValue属性值${this.key.type} 不支持 ${v.key.type}")
                        is TextEnumPropertyValue -> v.displayValue
                        is TextPropertyValue -> v.displayValue
                        is StructArrayPropertyValue -> throw TslException("StructPropertyValue属性值${this.key.type} 不支持 ${v.key.type}")
                    } + ","
        }.toList().joinToString("\n") + "\n}"

    companion object {

        private fun fakeStructPropertyKey(source: StructArrayPropertyValue): StructPropertyKey {
            // 取出source的itemKeys（里面的struct有的key）比如P1,P2 {"P1":1,"P2":2.0}
            val items = source.key.itemKeys
            // 构建个fake的StructPropertyKey
            return StructPropertyKey.fakeKey(items)
        }

        /**
         * 添加的时候没有key 模拟一个key
         */
        fun createFakePropertyKeyForAdd(
            source: StructArrayPropertyValue,
        ): StructPropertyValue {
            val key = fakeStructPropertyKey(source)
            return fakeEmptyValueWithKey(key)
        }

        private fun fakeEmptyValueWithKey(key: StructPropertyKey): StructPropertyValue {
            val list: Map<PropertyKey, PropertyValue<*>> = key.items.associateWith { propertyKey ->
                val unSupport =
                    { throw IllegalStateException("fakeEmptyValueWithKey不支持 ${propertyKey.type}") }
                val propertyValue: PropertyValue<*> = when (propertyKey) {

                    is BooleanPropertyKey -> BoolPropertyValue(propertyKey, null)
                    is DatePropertyKey -> DatePropertyValue(propertyKey, null)
                    is DoublePropertyKey -> DoublePropertyValue(propertyKey, null)
                    is IntEnumPropertyKey -> IntEnumPropertyValue.createValue(key = propertyKey)
                    is TextEnumPropertyKey -> TextEnumPropertyValue.createValue(key = propertyKey)
                    is FloatPropertyKey -> FloatPropertyValue(propertyKey, null)
                    is IntPropertyKey -> IntPropertyValue(propertyKey, null)
                    is TextPropertyKey -> TextPropertyValue(propertyKey, null)
                    is DoubleArrayPropertyKey -> unSupport()
                    is FloatArrayPropertyKey -> unSupport()
                    is IntArrayPropertyKey -> unSupport()
                    is StructArrayPropertyKey -> unSupport()
                    is TextArrayPropertyKey -> unSupport()
                    is StructPropertyKey -> unSupport()
                }
                propertyValue
            }
            return StructPropertyValue(key, list)
        }

        // 应用于array中没有key的StructArrayPropertyValue
        // 所以需要模拟（create）一个key
        fun createFakePropertyKeyForArray(
            source: StructArrayPropertyValue,
        ): List<StructPropertyValue> {
            val key = fakeStructPropertyKey(source)
            // array 每一个都是一样的StructPropertyKey.取出数据填充key
            return source.value.map { itemValues ->
                StructPropertyValue(key, itemValues)
            }
        }

    }
}

val ArrayPropertyValue.asStringList: List<String>
    get() = when (val data = this) {
        is DoubleArrayPropertyValue -> {
            data.value.map {
                it.toString()
            }
        }

        is FloatArrayPropertyValue -> data.value.map {
            it.toString()
        }

        is IntArrayPropertyValue -> data.value.map {
            it.toString()
        }

        is StructArrayPropertyValue -> data.value.map { map ->
            map.map { (k, v) ->
                k.identifier
            }.joinToString(",") { it }
        }

        is TextArrayPropertyValue -> data.value.map {
            it
        }
    }

val ArrayPropertyValue.asPropertyValueList: List<PropertyValue<*>>
    get() {
        val propertyValues = when (val data = this) {
            is DoubleArrayPropertyValue -> {
                data.value.map {
                    DoublePropertyValue.createValue(null, it)
                }
            }

            is FloatArrayPropertyValue -> data.value.map {
                FloatPropertyValue.createValue(null, it)
            }

            is IntArrayPropertyValue -> data.value.map {
                IntPropertyValue.createValue(null, it)
            }

            is StructArrayPropertyValue -> StructPropertyValue.createFakePropertyKeyForArray(data)

            is TextArrayPropertyValue -> data.value.map {
                TextPropertyValue.createValue(null, it)
            }
        }
        return propertyValues
    }
//</editor-fold>

//<editor-fold desc="#PropertyValue<*> 扩展">
val PropertyValue<*>.valueStr: String
    get() = when (this) {
        is DoubleArrayPropertyValue -> this.value.toString()
        is FloatArrayPropertyValue -> this.value.toString()
        is IntArrayPropertyValue -> this.value.toString()
        is StructArrayPropertyValue -> this.value.toString()
        is TextArrayPropertyValue -> this.value.toString()
        is BoolPropertyValue -> this.keyValue?.let { "(${it.value}):${it.desc}" } ?: UNKNOWN_VALUE
        is DatePropertyValue -> this.value ?: UNKNOWN_VALUE
        is DoublePropertyValue -> this.value?.toString() ?: UNKNOWN_VALUE
        is FloatPropertyValue -> this.value?.toString() ?: UNKNOWN_VALUE
        is IntEnumPropertyValue -> if (isEmpty()) UNKNOWN_VALUE else this.keyValue.let { "(${it.value}):${it.desc}" }
        is IntPropertyValue -> this.value?.toString() ?: UNKNOWN_VALUE
        is StructPropertyValue -> this.itemValues.values.joinToString("\n") {
            it.key.identifier + "=" + it.valueStr
        }

        is TextEnumPropertyValue -> if (isEmpty()) UNKNOWN_VALUE else this.keyValue.let { "(${it.value}):${it.desc}" }
        is TextPropertyValue -> this.value ?: ""
    }
//</editor-fold>