package com.yunext.kmp.mqtt.virtuals.protocol.tsl.property

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslPropertyType
import kotlinx.serialization.Serializable

//<editor-fold desc="#PropertyKey">
sealed interface PropertyKey {
    /* 属性读写类型：只读（r）或读写（rw） */
    val accessMode: String

    /* 是否是标准功能的必选属性（保留）*/
    val required: Boolean
    val desc: String
    val identifier: String
    val name: String
    val item: Boolean
        get() = false
    val type: TslPropertyType
}


private sealed interface IFakeKey<in Input, out Output : PropertyKey> {

}

private interface FakeKey<out Output : PropertyKey> : IFakeKey<Nothing, Output> {
    fun fakeKey(): Output
}

private interface FakeKeyWithInput<in Input, out Output : PropertyKey> : IFakeKey<Input, Output> {
    fun fakeKey(input: Input): Output
}

private fun generateIdentifier(tag: String): String = "fake_${tag}_identifier_${currentTime()}"

private inline fun <reified T> generateIdentifier(): String =
    generateIdentifier(T::class.simpleName ?: T::class.toString())

//</editor-fold>

//<editor-fold desc="##IntPropertyKey">
@Serializable
class IntPropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val max: Int,
    val min: Int,
    val unit: String,
    val step: String,
) : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.INT

    companion object : FakeKey<PropertyKey> {

        override fun fakeKey(): IntPropertyKey = IntPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<IntPropertyKey>(),
            "",
            0,
            0,
            "",
            ""
        )

        val EMPTY: IntPropertyKey by lazy {
            fakeKey()
        }
    }
}
//</editor-fold>

//<editor-fold desc="##FloatPropertyKey">
@Serializable
class FloatPropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val max: Float, val min: Float, val unit: String, val step: String,
) : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.FLOAT

    companion object : FakeKey<PropertyKey> {
        internal val EMPTY: FloatPropertyKey
                by lazy { fakeKey() }

        override fun fakeKey(): FloatPropertyKey {
            return FloatPropertyKey(
                false,
                "",
                false,
                "",
                identifier = generateIdentifier<FloatPropertyKey>(),
                "",
                0f,
                0f,
                "",
                ""
            )
        }
    }
}
//</editor-fold>

//<editor-fold desc="##DoublePropertyKey">
@Serializable
class DoublePropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val max: Double, val min: Double, val unit: String, val step: String,
) : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.DOUBLE

    companion object : FakeKey<PropertyKey> {
        internal val EMPTY: DoublePropertyKey
                by lazy {
                    fakeKey()
                }

        override fun fakeKey(): DoublePropertyKey {
            return DoublePropertyKey(
                false,
                "",
                false,
                "",
                identifier = generateIdentifier<DoublePropertyKey>(),
                "",
                0.0,
                0.0,
                "",
                ""
            )
        }
    }
}
//</editor-fold>

//<editor-fold desc="##TextPropertyKey">
@Serializable
class TextPropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val length: Int,
) : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.TEXT

    companion object : FakeKey<PropertyKey> {
        internal val EMPTY: TextPropertyKey
                by lazy { fakeKey() }

        override fun fakeKey(): TextPropertyKey {
            return TextPropertyKey(
                false, "", false, "", identifier =
                generateIdentifier<TextPropertyKey>(), "", 0
            )
        }
    }
}
//</editor-fold>


//<editor-fold desc="##DatePropertyKey">
@Serializable
class DatePropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
) : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.DATE

    companion object : FakeKey<DatePropertyKey> {
        internal val EMPTY: DatePropertyKey
                by lazy { fakeKey() }

        override fun fakeKey(): DatePropertyKey {
            return DatePropertyKey(
                false, "", false, "", identifier =
                generateIdentifier<DatePropertyKey>(), ""
            )
        }
    }
}
//</editor-fold>

//<editor-fold desc="##BooleanPropertyKey">
@Serializable
class BooleanPropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val specs: List<BoolPropertyValue.KeyValue>,
) : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.BOOL

    companion object : FakeKey<PropertyKey> {
        override fun fakeKey(): BooleanPropertyKey = BooleanPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<BooleanPropertyKey>(),
            "",
            specs = emptyList()
        )
    }
}
//</editor-fold>


//<editor-fold desc="##EnumPropertyKey">
@Serializable
sealed class EnumPropertyKey() : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.ENUM
    abstract val enumType: TslPropertyType
}

@Serializable
class IntEnumPropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val specs: List<IntEnumPropertyValue.KeyValue>,
) : EnumPropertyKey() {

    override val enumType = TslPropertyType.INT

    companion object : FakeKey<EnumPropertyKey> {
        override fun fakeKey(): IntEnumPropertyKey = IntEnumPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<IntEnumPropertyKey>(),
            "",
            specs = emptyList()
        )
    }
}

@Serializable
class TextEnumPropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val specs: List<TextEnumPropertyValue.KeyValue>,
) : EnumPropertyKey() {
    override val enumType = TslPropertyType.TEXT
    override val type: TslPropertyType
        get() = TslPropertyType.ENUM

    companion object : FakeKey<EnumPropertyKey> {
        override fun fakeKey(): TextEnumPropertyKey = TextEnumPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<TextEnumPropertyKey>(),
            "",
            specs = emptyList()
        )
    }

}
//</editor-fold>

//<editor-fold desc="##ArrayPropertyKey">
@Serializable
sealed class ArrayPropertyKey(
    override val item: Boolean = false,
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val size: Int,
) : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.ARRAY
    abstract val itemType: TslPropertyType


}


class IntArrayPropertyKey(
    item: Boolean = false,
    accessMode: String,
    required: Boolean,
    desc: String,
    identifier: String,
    name: String,
    size: Int,
//    val items: List<PropertyKey>
) : @Serializable ArrayPropertyKey(item, accessMode, required, desc, identifier, name, size) {
    override val itemType: TslPropertyType = TslPropertyType.INT

    companion object : FakeKey<ArrayPropertyKey> {
        override fun fakeKey(): IntArrayPropertyKey = IntArrayPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<IntArrayPropertyKey>(),
            "",
            size = 0,
        )
    }
}

class FloatArrayPropertyKey(

    item: Boolean = false,
    accessMode: String,
    required: Boolean,
    desc: String,
    identifier: String,
    name: String,
    size: Int,
) : @Serializable ArrayPropertyKey(item, accessMode, required, desc, identifier, name, size) {
    override val itemType: TslPropertyType = TslPropertyType.FLOAT

    companion object : FakeKey<ArrayPropertyKey> {
        override fun fakeKey(): FloatArrayPropertyKey = FloatArrayPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<FloatArrayPropertyKey>(),
            "",
            size = 0,
        )
    }
}


class DoubleArrayPropertyKey(

    item: Boolean = false,
    accessMode: String,
    required: Boolean,
    desc: String,
    identifier: String,
    name: String,
    size: Int,
) : @Serializable ArrayPropertyKey(item, accessMode, required, desc, identifier, name, size) {
    override val itemType: TslPropertyType = TslPropertyType.DOUBLE

    companion object : FakeKey<DoubleArrayPropertyKey> {
        override fun fakeKey(): DoubleArrayPropertyKey = DoubleArrayPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<DoubleArrayPropertyKey>(),
            "",
            size = 0,
        )
    }
}


class TextArrayPropertyKey(
    item: Boolean = false,
    accessMode: String,
    required: Boolean,
    desc: String,
    identifier: String,
    name: String,
    size: Int,
) : @Serializable ArrayPropertyKey(item, accessMode, required, desc, identifier, name, size) {
    override val itemType: TslPropertyType = TslPropertyType.TEXT

    companion object : FakeKey<ArrayPropertyKey> {
        override fun fakeKey(): TextArrayPropertyKey = TextArrayPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<TextArrayPropertyKey>(),
            "",
            size = 0,
        )
    }
}

class StructArrayPropertyKey(
    item: Boolean = false,
    accessMode: String,
    required: Boolean,
    desc: String,
    identifier: String,
    name: String,
    size: Int,
    val itemKeys: List<PropertyKey>,
) : @Serializable ArrayPropertyKey(item, accessMode, required, desc, identifier, name, size) {
    override val itemType: TslPropertyType = TslPropertyType.STRUCT

    companion object : FakeKey<ArrayPropertyKey> {
        override fun fakeKey(): StructArrayPropertyKey = StructArrayPropertyKey(
            false,
            "",
            false,
            "",
            identifier = generateIdentifier<StructArrayPropertyKey>(),
            "",
            size = 0,
            itemKeys = emptyList()
        )
    }
}
//</editor-fold>

//<editor-fold desc="##StructPropertyKey">
@Serializable
class StructPropertyKey(
    override val accessMode: String,
    override val required: Boolean,
    override val desc: String,
    override val identifier: String,
    override val name: String,
    val items: List<PropertyKey>,
    /*构造出来的*/
    val fake: Boolean = false,
) : PropertyKey {
    override val type: TslPropertyType
        get() = TslPropertyType.STRUCT

    companion object : FakeKeyWithInput<List<PropertyKey>, PropertyKey> {
        override fun fakeKey(input: List<PropertyKey>): StructPropertyKey {
            return StructPropertyKey(
                accessMode = "rw",
                required = true,
                desc = "fake_StructArrayPropertyKey_desc",
                identifier = generateIdentifier<StructPropertyKey>(),
                name = "fake_StructArrayPropertyKey_name",
                items = input,
                fake = true
            )
        }
    }

}
//</editor-fold>


//<editor-fold desc="#扩展属性和方法">
val PropertyKey.display: String
    get() {
        return "{\n" + "   [identifier]$identifier\n" + "   [name]$name\n" + "   [type]${type.text}\n" + "   [spec]$specDisplay\n" + "\n}"
    }

val PropertyKey.unitStr: String
    get() = when (this) {
        is DoubleArrayPropertyKey -> ""
        is FloatArrayPropertyKey -> ""
        is IntArrayPropertyKey -> ""
        is StructArrayPropertyKey -> ""
        is TextArrayPropertyKey -> ""
        is BooleanPropertyKey -> ""
        is DatePropertyKey -> ""
        is DoublePropertyKey -> this.unit
        is IntEnumPropertyKey -> ""
        is TextEnumPropertyKey -> ""
        is FloatPropertyKey -> this.unit
        is IntPropertyKey -> this.unit
        is StructPropertyKey -> ""
        is TextPropertyKey -> ""
    }

val PropertyKey.specDisplay: String
    get() {
        return when (this) {
            is DoubleArrayPropertyKey -> "[大小]${this.size}\n[子类型]${this.itemType.text}"
            is FloatArrayPropertyKey -> "[大小]${this.size}\n[类型]${this.itemType.text}"
            is IntArrayPropertyKey -> "[大小]${this.size}\n[类型]${this.itemType.text}"
            is StructArrayPropertyKey -> "[大小]${this.size}\n[类型]${this.itemType.text}\n" + this.itemKeys.joinToString(
                "\n"
            ) {
                "-->[${it.identifier}]\n${it.specDisplay}"
            }

            is TextArrayPropertyKey -> "[大小]${this.size}\n[类型]${this.itemType.text}"
            is BooleanPropertyKey -> "[取值范围]\n${
                this.specs.map {
                    "[${it.value}]:${it.desc}"
                }.joinToString("\n")
            }"

            is DatePropertyKey -> "[说明]\nString类型UTC毫秒"
            is DoublePropertyKey -> "[最大值]${this.max}\n[最小值]${this.min}\n[步长]${this.step}\n[单位]${this.unit}"
            is IntEnumPropertyKey -> "[子类型]${this.enumType}\n[取值范围]\n${
                this.specs.map {
                    "   [${it.value}]:${it.desc}"
                }.joinToString("\n")
            }"

            is TextEnumPropertyKey -> "[子类型]${this.enumType}\n[取值范围]\n${
                this.specs.map {
                    "   [${it.value}]:${it.desc}"
                }.joinToString("\n")
            }"

            is FloatPropertyKey -> "[最大值]${this.max}\n[最小值]${this.min}\n[步长]${this.step}\n[单位]${this.unit}"
            is IntPropertyKey -> "[最大值]${this.max}\n[最小值]${this.min}\n[步长]${this.step}\n[单位]${this.unit}"
            is StructPropertyKey -> "结构：\n ${
                this.items.joinToString("\n") {
                    "[${it.identifier}] : ${it.type}"
                }
            }"

            is TextPropertyKey -> "[最大长度]${this.length}"
        }
    }

val PropertyKey.specDisplayDebug: String
    get() {
        return when (this) {
            is DoubleArrayPropertyKey -> "[大小]${this.size}\n[子类型]${this.itemType.text}"
            is FloatArrayPropertyKey -> "[大小]${this.size}\n[类型]${this.itemType.text}"
            is IntArrayPropertyKey -> "[大小]${this.size}\n[类型]${this.itemType.text}"
            is StructArrayPropertyKey -> "[大小]${this.size}\n[类型]${this.itemType.text}" // todo
            is TextArrayPropertyKey -> "[大小]${this.size}\n[类型]${this.itemType.text}"
            is BooleanPropertyKey -> "[取值范围]\n${
                this.specs.map {
                    "[${it.value}]:${it.desc}"
                }.joinToString("\n")
            }"

            is DatePropertyKey -> "[说明]\nString类型UTC毫秒"
            is DoublePropertyKey -> "[最大值]${this.max}\n[最小值]${this.min}\n[步长]${this.step}\n[单位]${this.unit}"
            is IntEnumPropertyKey -> "[子类型]${this.enumType}\n[取值范围]\n${
                this.specs.map {
                    "   [${it.value}]:${it.desc}"
                }.joinToString("\n")
            }"

            is TextEnumPropertyKey -> "[子类型]${this.enumType}\n[取值范围]\n${
                this.specs.map {
                    "   [${it.value}]:${it.desc}"
                }.joinToString("\n")
            }"

            is FloatPropertyKey -> "[最大值]${this.max}\n[最小值]${this.min}\n[步长]${this.step}\n[单位]${this.unit}"
            is IntPropertyKey -> "[最大值]${this.max}\n[最小值]${this.min}\n[步长]${this.step}\n[单位]${this.unit}"
            is StructPropertyKey -> "结构： ${
                this.items.map {
                    "[${it.identifier}]:${it.type}"
                }.joinToString("\n")
            }"

            is TextPropertyKey -> "[最大长度]${this.length}"
        }
    }

val PropertyKey.nameAndKey: String
    get() = "${name}(${identifier})"


/*
 * 每个PropertyKey 初始化后的默认值
 * PropertyKey -> PropertyValue
 */
internal fun PropertyKey.toDefaultValue(): PropertyValue<*> {
    return when (this) {
        is ArrayPropertyKey -> {
            val arrayPropertyKey: ArrayPropertyKey = this
            when (arrayPropertyKey) {
                is IntArrayPropertyKey -> IntArrayPropertyValue(arrayPropertyKey, listOf())
                is DoubleArrayPropertyKey -> DoubleArrayPropertyValue(
                    arrayPropertyKey,
                    listOf()
                )

                is FloatArrayPropertyKey -> FloatArrayPropertyValue(arrayPropertyKey, listOf())
                is TextArrayPropertyKey -> TextArrayPropertyValue(arrayPropertyKey, listOf())
                is StructArrayPropertyKey -> StructArrayPropertyValue(arrayPropertyKey, listOf())
            }
        }

        is BooleanPropertyKey -> BoolPropertyValue(this, null)
        is DatePropertyKey -> DatePropertyValue(this, null)
        is DoublePropertyKey -> DoublePropertyValue(this, null)
        is IntEnumPropertyKey -> IntEnumPropertyValue.createValue(key = this)
        is TextEnumPropertyKey -> TextEnumPropertyValue.createValue(key = this)
        is FloatPropertyKey -> FloatPropertyValue(this, null)
        is IntPropertyKey -> IntPropertyValue(this, null)
        is StructPropertyKey -> {
            val items = this.items
            val map = items.map { propertyKey ->
                propertyKey to propertyKey.toDefaultValue()
            }.toMap()
            HDLogger.d(
                "PropertyKey.tslHandleDefaultValue",
                "StructPropertyKey:${this.identifier}==>${items.size}==>${map.size}"
            )
            StructPropertyValue(this, map)
        }

        is TextPropertyKey -> TextPropertyValue(this, null)
    }
}
//</editor-fold>