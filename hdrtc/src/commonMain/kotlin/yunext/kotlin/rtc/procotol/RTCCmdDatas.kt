package yunext.kotlin.rtc.procotol

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val AuthenticationWriteData = RTCCmdData(RTCCmd.AuthenticationWrite, "a001", "b001")
private val AuthenticationNotifyData = RTCCmdData(RTCCmd.AuthenticationNotify, "a001", "b002")
private val ParameterWriteData = RTCCmdData(RTCCmd.ParameterWrite, "a001", "b001")
private val TimestampWriteData = RTCCmdData(RTCCmd.TimestampWrite, "a001", "b001")

val rtcCmdDataList by lazy {
    listOf(
        AuthenticationWriteData,
        AuthenticationNotifyData,
        ParameterWriteData,
        TimestampWriteData
    )
}

internal interface RTCCMDDataStore {
    val localPropertyMap: StateFlow<Map<ParameterKey, ByteArray>>
}

abstract class AbstractRTCCMDDataStore : RTCCMDDataStore {
    protected val keys = ParameterKey.entries.toList()

    protected val _localPropertyMap: MutableStateFlow<Map<ParameterKey, ByteArray>> =
        MutableStateFlow(keys.associateWith {
            byteArrayOf()
        })

    override val localPropertyMap: StateFlow<Map<ParameterKey, ByteArray>> =
        _localPropertyMap.asStateFlow()


    protected fun createParameter(key: String, value: String): Pair<ParameterKey, ByteArray> {
        val k = keys.singleOrNull() {
            it.name == key
        } ?: throw IllegalArgumentException("不存在的key:${key}")
        return k to if (value.isNotEmpty()) value.encodeToByteArray() else byteArrayOf()
    }

}