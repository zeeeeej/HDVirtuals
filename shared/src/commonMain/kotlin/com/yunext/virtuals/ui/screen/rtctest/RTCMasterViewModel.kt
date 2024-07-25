package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLog
import com.yunext.kmp.common.logger.XLog
import com.yunext.virtuals.ui.common.HDStateScreenModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.generateXBleDevice
import yunext.kotlin.bluetooth.ble.logger.XBleRecord
import yunext.kotlin.bluetooth.ble.master.BleMasterConnectedStatus
import yunext.kotlin.bluetooth.ble.master.BleMasterScanningStatus
import yunext.kotlin.bluetooth.ble.master.XBleMasterScanResult
import yunext.kotlin.bluetooth.ble.master.connected
import yunext.kotlin.rtc.RTCMaster
import yunext.kotlin.rtc.procotol.RTC_ACCESS_KEY
import yunext.kotlin.rtc.procotol.text
import kotlin.random.Random

data class BleDeviceVo(val name: String, val mac: String, val rssi: Int)

private fun BleDeviceVo(d: XBleMasterScanResult) =
    BleDeviceVo(name = d.device.deviceName ?: "", mac = d.device.address, rssi = d.rssi)

private fun BleDeviceVo.asBleDevice() =
    generateXBleDevice(deviceName = this.name, address = this.mac)

sealed interface AuthEffect {
    data object Idle : AuthEffect
    data class Start(val key: String) : AuthEffect
    data class Success(val key: String) : AuthEffect
    data class Fail(val key: String, val msg: String) : AuthEffect
}

sealed interface TestCaseEffect {
    data object Idle : TestCaseEffect
    data class Start(val msg: String) : TestCaseEffect
    data class Progress(val msg: String) : TestCaseEffect
    data class Success(val msg: String) : TestCaseEffect
    data class Fail(val msg: String) : TestCaseEffect
}

val TestCaseEffect.show:String
    get() = when(this){
        is TestCaseEffect.Fail -> this.msg
        TestCaseEffect.Idle -> ""
        is TestCaseEffect.Progress -> this.msg
        is TestCaseEffect.Start -> this.msg
        is TestCaseEffect.Success -> this.msg
    }

sealed interface SetPropertyEffect {
    data object Idle : SetPropertyEffect
    data class Start(val key: String, val value: String) : SetPropertyEffect
    data class Success(val key: String, val value: String) : SetPropertyEffect
    data class Fail(val key: String, val value: String, val msg: String) : SetPropertyEffect
}

@Stable
data class RTCMasterState(
    val scanning: BleMasterScanningStatus = BleMasterScanningStatus.ScanStopped,
    val connecting: BleMasterConnectedStatus = BleMasterConnectedStatus.Idle,
    val scanningDeviceList: List<BleDeviceVo> = listOf(TEST_DEVICE),
    val records: List<XBleRecord> = emptyList(),
    val services: List<XBleService> = emptyList(),
    val authEffect: AuthEffect = AuthEffect.Idle,
    val setPropertyEffect: SetPropertyEffect = SetPropertyEffect.Idle,
    val auth: Boolean = false,
    val params: List<ParameterDataVo> = emptyList(),
) {
    companion object {
        private val TEST_DEVICE = BleDeviceVo("测试", "00:00:00:00:00:00", -1)
    }
}

@OptIn(ExperimentalStdlibApi::class)
class RTCMasterViewModel :
    HDStateScreenModel<RTCMasterState>(DEFAULT), XLog by HDLog("RTCMasterViewModel", true) {
    companion object {
        private val DEFAULT by lazy {
            RTCMasterState()
        }
    }

    private val rtcMaster: RTCMaster = RTCMaster()

    init {

        screenModelScope.launch {
            launch {
                rtcMaster.connected.collectLatest {
                    mutableState.value = state.value.copy(
                        connecting = it,
                    )
                    if (!it.connected) {
                        mutableState.value = state.value.copy(
                            authEffect = AuthEffect.Idle,
                            auth = false,
                        )
                    }

                }
            }

            launch {
                rtcMaster.localPropertyMap.collectLatest {
                    val list = it.map { (k, v) ->
                        ParameterDataVo(k.name, v.toHexString(), k.text)
                    }
                    mutableState.value = state.value.copy(
                        params = list,
                    )
                }
            }

            launch {
                rtcMaster.scanning.collectLatest {
                    d("[launch rtcMaster.scanning] $it")
                    mutableState.value = state.value.copy(
                        scanning = it
                    )
                }
            }

            launch {
                rtcMaster.record.collectLatest {
                    mutableState.value = state.value.copy(
                        records = it
                    )
                }
            }

            launch {
                rtcMaster.scanningResult.collectLatest {
                    mutableState.value = state.value.copy(
                        scanningDeviceList = it.map(::BleDeviceVo)
                    )
                }
            }
            launch {
                rtcMaster.services.collectLatest {
                    mutableState.value = state.value.copy(
                        services = it
                    )
                }
            }
        }

    }


    fun startScan() {
        rtcMaster.startScan()
    }

    fun connect(bleDeviceVo: BleDeviceVo) {
        rtcMaster.connect(bleDeviceVo.asBleDevice())
    }

    fun enableNotify() {
        rtcMaster.enableNotify()
    }

    fun stopScan() {
        rtcMaster.stopScan()
    }


    fun disconnect() {
        rtcMaster.disconnect()
    }

    fun write() {
        screenModelScope.launch {
            try {
                //TODO
                //rtcMaster.write(Random.nextBytes(4))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun read() {
        // TODO
//        rtcMaster.read()
    }

    fun auth(key: String) {
        screenModelScope.launch {
            try {
                mutableState.value = state.value.copy(authEffect = AuthEffect.Start(key))
                // TODO
//                val auth = rtcMaster.auth(key.ifEmpty { RTC_ACCESS_KEY })
//                mutableState.value = state.value.copy(
//                    authEffect =
//                    if (auth) {
//                        AuthEffect.Success(key)
//                    } else
//                        AuthEffect.Fail(key, "auth key fail!"), auth = auth
//                )

            } catch (e: Exception) {
                mutableState.value = state.value.copy(
                    authEffect = AuthEffect.Fail(key, "auth key fail! ${e.message ?: e.toString()}")
                )
            }
        }.also {
            it.invokeOnCompletion {
                d("auth::invokeOnCompletion")
            }
        }
    }

    fun setProperty(key: String, value: String) {
        screenModelScope.launch {
            mutableState.value =
                state.value.copy(setPropertyEffect = SetPropertyEffect.Start(key, value))
            try {
                // TODO
//                val property = rtcMaster.setProperty(key, value)
//                if (property) {
//                    mutableState.value =
//                        state.value.copy(setPropertyEffect = SetPropertyEffect.Success(key, value))
//                } else {
//                    mutableState.value = state.value.copy(
//                        setPropertyEffect = SetPropertyEffect.Fail(
//                            key,
//                            value,
//                            "设置属性失败"
//                        )
//                    )
//                }
            } catch (e: Exception) {
                mutableState.value = state.value.copy(
                    setPropertyEffect = SetPropertyEffect.Fail(
                        key,
                        value,
                        "设置属性失败${e.message}"
                    )
                )
            }
        }
    }

    fun setTimestamp() {

    }


    fun authedTestCase() {
        // auth 鉴权
        // 写参数 成功
        // 写时间戳 成功
        // 超过x时间不掉线
    }

    fun unAuthedTestCase() {
        // 写参数 失败
        // 写时间戳 失败
        // 超过x时间一定掉线
    }

    override fun onDispose() {
        super.onDispose()
        rtcMaster.clear()
    }
}