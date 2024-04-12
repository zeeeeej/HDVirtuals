package com.yunext.kmp.mqtt.virtuals.protocol.tsl

class Tsl(
    val id: String,
    val version: String,
    val productKey: String,
    val current: Boolean,
    val events: List<TslEvent>,
    val properties: List<TslProperty>,
    val services: List<TslService>,
)

val Tsl.display: String
    get() {
        return """
            // Tsl
            id          :       $id
            version     :       $version
            productKey  :       $productKey
            current     :       $current
            events      :       ${events.size}
            properties  :       ${properties.size}
            services    :       ${services.size}
            // 属性
            ${properties.display(TslProperty::display)}
            // 事件
            ${events.display { it.display }}
            // 服务
            ${services.display { it.display }}
            
        """.trimIndent()
    }


inline fun <reified T> List<T>.display(crossinline toString: (T) -> String): String {
    return this.joinToString("\n") { toString(it) }
}

//fun List<TslProperty>.displays(): String = this.joinToString("\n") { it.display }

val TslProperty.display: String
    get() {
        return "\n" + """
            identifier      :       $identifier
            name            :       $name
            accessMode      :       $accessMode
            desc            :       $desc
            required        :       $required
            dataType        :       $dataType
            specs           :       $specs
            inner           :       $inner
        """.trimIndent()
    }

//fun List<TslEvent>.display(): String = this.joinToString("\n") { it.display }

val TslEvent.display: String
    get() = "\n" + """
        identifier      :       $identifier
        name            :       $name
        type            :       $type
        required        :       $required
        desc            :       $desc
        method          :       $method
        outputData      :       ${outputData.size}
    """.trimIndent()

//fun List<TslService>.display(): String = this.joinToString("\n") { it.display }
val TslService.display: String
    get() = "\n" + """
        identifier      :       $identifier
        name            :       $name
        callType        :       $callType
        required        :       $required
        desc            :       $desc
        method          :       $method
        inputData       :       ${inputData.size}
        outputData      :       ${outputData.size}
    """.trimIndent()



