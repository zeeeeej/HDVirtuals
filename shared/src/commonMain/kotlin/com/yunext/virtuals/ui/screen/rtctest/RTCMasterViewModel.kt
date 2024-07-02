package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.virtuals.ui.common.HDStateScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import yunext.kotlin.bluetooth.ble.core.generateBleServiceOnlyByUUID
import yunext.kotlin.bluetooth.ble.core.generateXBleCharacteristicsOnlyByUUID
import yunext.kotlin.bluetooth.ble.core.generateXBleDevice
import yunext.kotlin.bluetooth.ble.master.BleMaster
import yunext.kotlin.bluetooth.ble.master.XBleMasterScanResult
import yunext.kotlin.bluetooth.ble.master.createBleMaster
import yunext.kotlin.bluetooth.ble.util.uuidFromShort

data class BleDeviceVo(val name: String, val mac: String, val rssi: Int)

private fun BleDeviceVo(d: XBleMasterScanResult) =
    BleDeviceVo(name = d.device.deviceName ?: "", mac = d.device.address, rssi = d.rssi)

private fun BleDeviceVo.asBleDevice() = generateXBleDevice(deviceName = this.name, address = this.mac)

@Stable
data class RTCMasterState(
    val master: Boolean,
    val scanning: Boolean,
    val scanningDeviceList: List<BleDeviceVo>,
) {
    companion object {
        val DEFAULT by lazy {
            RTCMasterState(
                false,
                false,
                listOf(BleDeviceVo("测试", "00:00:00:00:00:00", -1))
            )
        }
    }
}

class RTCMasterViewModel(private val master: Boolean) :
    HDStateScreenModel<RTCMasterState>(RTCMasterState.DEFAULT) {

    init {
        screenModelScope.launch {
            Napier.v("start...")
            delay(5000)
            Napier.v("end...")
        }
    }

    private val bleMaster: BleMaster by lazy {
        createBleMaster()
    }

    private fun clearScan() {
        mutableState.value = this.state.value.copy(
            scanningDeviceList = emptyList()
        )
    }

    fun startScan() {
        clearScan()
        bleMaster.startScan {
            val old = this.state.value
            val todo = BleDeviceVo(it)
            mutableState.value = old.copy(
                scanningDeviceList = old.scanningDeviceList.update(todo) {
                    this.mac
                }
            )
        }
    }

    fun connect(bleDeviceVo: BleDeviceVo) {
        bleMaster.connect((bleDeviceVo.asBleDevice())) {

        }
    }

    fun enableNotify(bleDeviceVo: BleDeviceVo) {
        bleMaster.enableNotify(
            (bleDeviceVo.asBleDevice()),
            service = generateBleServiceOnlyByUUID(uuidFromShort("a001")),
            characteristic = generateXBleCharacteristicsOnlyByUUID(uuidFromShort("b002"))
        ) {

        }
    }

    private fun <T> List<T>.update(t: T, key: T.() -> String): List<T> {
        return this - this.filter {
            it.key() == t.key()
        }.toSet() + t
    }

    fun stopScan() {
        bleMaster.stopScan()
    }

    override fun onDispose() {
        super.onDispose()
        bleMaster.clear()
    }
}