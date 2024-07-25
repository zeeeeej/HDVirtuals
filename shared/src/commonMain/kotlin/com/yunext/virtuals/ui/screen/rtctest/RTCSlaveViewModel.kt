package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLog
import com.yunext.virtuals.ui.common.HDStateScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import yunext.kotlin.bluetooth.ble.logger.XBleRecord
import yunext.kotlin.bluetooth.ble.slave.BroadcastStatus
import yunext.kotlin.bluetooth.ble.slave.ConnectStatus
import yunext.kotlin.rtc.RTCSlave
import yunext.kotlin.rtc.procotol.RTC_ACCESS_KEY
import yunext.kotlin.rtc.procotol.text
import kotlin.random.Random

data class ParameterDataVo(val key: String, val value: String,val title:String)

@Stable
data class RTCSlaveState(
    val address: String = "",
    val accessKey: String = "",
    val broadcasting: BroadcastStatus = BroadcastStatus.BroadcastStopped,
    val connected: ConnectStatus = ConnectStatus.Disconnected,
    val params: List<ParameterDataVo> = emptyList(),
    val effect:SetPropertyEffect = SetPropertyEffect.Idle,
    val records: List<XBleRecord> = emptyList(),
) {
    companion object {
        val DEFAULT by lazy {
            RTCSlaveState()
        }
    }
}

val RTCSlaveState.display: String
    get() = """
        address:$address ,broadcasting:$broadcasting ,connected:$connected params:${params.size}
    """.trimIndent()

//expect fun checkPermission(activity: Activity,permission:Array<PermissionState>) {}
class RTCSlaveViewModel : HDStateScreenModel<RTCSlaveState>(RTCSlaveState.DEFAULT) {
    private var currentRtcSlave: RTCSlave? = null

    @OptIn(ExperimentalStdlibApi::class)
    fun config(todoAddress: String) {
        log.d("[config] address:$todoAddress ,rtcSlave:$currentRtcSlave")
        val address = todoAddress.ifBlank {
            Random.Default.nextBytes(6).toHexString()
        }
        screenModelScope.launch {
            val old = currentRtcSlave
            if (old != null) {
                log.d("[config] address:$address 移除旧的")
                currentRtcSlave = null
                old.stopBroadcasting()
                old.disconnect()
                old.clear()
                mutableState.value = RTCSlaveState.DEFAULT
            }
            delay(500)
            val slave = RTCSlave(address,RTC_ACCESS_KEY)
            delay(100)
            log.d("[config] address:$address accessKey:${RTC_ACCESS_KEY},创建新的rtcSlave:$slave")
            launch {
                slave.localPropertyMap.collectLatest {
                    val list = it.map { (k, v) ->
                        ParameterDataVo(k.name, v.toHexString(),k.text)
                    }
                    mutableState.value = state.value.copy(
                        params = list,
                    )
                }
            }

            launch {
                slave.address.collectLatest {
                    mutableState.value = state.value.copy(
                        address = it,
                    )
                }
            }

            launch {
                slave.accessKey.collectLatest {
                    mutableState.value = state.value.copy(
                        accessKey = it,
                    )
                }
            }

            launch {
                slave.record.collectLatest {
                    mutableState.value = state.value.copy(
                        records = it,
                    )
                }
            }

            launch {
                slave.connected.collectLatest { status ->
                    log.d("slave.connected.collectLatest : $status")
                    mutableState.value = state.value.copy(
                        connected = status
                    )
                }
            }

            launch {
                slave.broadcasting.collectLatest { status ->
                    log.d("slave.broadcasting.collectLatest : $status")
                    mutableState.value = state.value.copy(
                        broadcasting = status
                    )
                }
            }
            delay(100)
            this@RTCSlaveViewModel.currentRtcSlave = slave
            log.d("[config] address:$address 完成！")

            launch {
                state.collectLatest {
                    log.d("[state]:$it")
                }
            }
        }

    }

    fun changeAccessKey(key:String){
        currentRtcSlave?.changeAccessKey(key)
    }

    fun startBroadcast() {
        log.d("[startBroadcast] currentRtcSlave:$currentRtcSlave  address=${currentRtcSlave?.address?.value}")
        currentRtcSlave?.startBroadcasting()
    }

    fun updateProperty(data: ParameterDataVo) {
        val key = data.key
        val value = data.value


        currentRtcSlave?.setProperty(key, value = value)
    }

    fun stopBroadcast() {
        log.d("[stopBroadcast] currentRtcSlave:$currentRtcSlave ")
        currentRtcSlave?.stopBroadcasting()
    }

    fun disconnect() {
        log.d("[disconnect] currentRtcSlave:$currentRtcSlave ")
        currentRtcSlave?.disconnect()
    }

    override fun onDispose() {
        super.onDispose()
        log.d("[onDispose] currentRtcSlave:$currentRtcSlave ")
        currentRtcSlave?.clear()
    }

    companion object {
        private val log by lazy {
            HDLog("RTCSlaveViewModel", false)
        }
    }
}