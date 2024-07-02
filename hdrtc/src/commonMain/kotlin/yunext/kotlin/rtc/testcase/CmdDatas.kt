package yunext.kotlin.rtc.testcase

import yunext.kotlin.rtc.procotol.RTCCmd
import yunext.kotlin.rtc.procotol.RTCCmdData

val AuthenticationWriteData = RTCCmdData(RTCCmd.AuthenticationWrite, "a001", "b001")
val AuthenticationNotifyData = RTCCmdData(RTCCmd.AuthenticationNotify, "a001", "b002")
val ParameterWriteData = RTCCmdData(RTCCmd.ParameterWrite, "a001", "b003")
val TimestampWriteData = RTCCmdData(RTCCmd.TimestampWrite, "a001", "b004")

val rtcCmdDataList by lazy {
    listOf(
        AuthenticationWriteData,
        AuthenticationNotifyData,
        ParameterWriteData,
        TimestampWriteData
    )
}