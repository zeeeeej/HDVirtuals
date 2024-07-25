package yunext.kotlin.bluetooth.ble.master

import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.logger.BleRecordCallback

interface BleMaster {
    val connectedStatus: BleMasterConnectedStatus
    val scanningStatus: BleMasterScanningStatus
    fun init(
        onConnectedChanged: OnXBleMasterConnectedStatusChanged,
        onScanningStatusChanged: OnXBleMasterScanningStatusChanged,
        recordCallback: BleRecordCallback,
    )

    fun startScan(onResult: (XBleMasterScanResult) -> Unit)
    fun stopScan()

    fun connect(
        device: yunext.kotlin.bluetooth.ble.core.XBleDevice,
        callback: XBleMasterConnectCallback,
    )

    fun enableNotify(
        service: yunext.kotlin.bluetooth.ble.core.XBleService,
        characteristic: yunext.kotlin.bluetooth.ble.core.XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    )

    fun notify(
        service: yunext.kotlin.bluetooth.ble.core.XBleService,
        characteristic: yunext.kotlin.bluetooth.ble.core.XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    )

    fun read(
        service: yunext.kotlin.bluetooth.ble.core.XBleService,
        characteristic: yunext.kotlin.bluetooth.ble.core.XBleCharacteristics,
        callback: XBleMasterReadCallback,
    )

    fun write(
        service: yunext.kotlin.bluetooth.ble.core.XBleService,
        characteristic: yunext.kotlin.bluetooth.ble.core.XBleCharacteristics,
        data: yunext.kotlin.bluetooth.ble.core.XBleDownPayload,
        callback: XBleMasterWriteCallback,
    )

    fun disconnect()

    fun clear()
}


sealed interface BleMasterConnectedStatus {
    data object Idle : BleMasterConnectedStatus

    data class Connected(
        val device: yunext.kotlin.bluetooth.ble.core.XBleDevice,
        val
        services: List<XBleService>,
    ) :
        BleMasterConnectedStatus

    data class Connecting(val device: yunext.kotlin.bluetooth.ble.core.XBleDevice) :
        BleMasterConnectedStatus

    data class Disconnected(val device: yunext.kotlin.bluetooth.ble.core.XBleDevice) :
        BleMasterConnectedStatus

    data class Disconnecting(val device: yunext.kotlin.bluetooth.ble.core.XBleDevice) :
        BleMasterConnectedStatus
}

val BleMasterConnectedStatus.display:String
    get() = when(this){
        is BleMasterConnectedStatus.Connected -> "已连接"
        is BleMasterConnectedStatus.Connecting -> "连接中"
        is BleMasterConnectedStatus.Disconnected -> "已断开"
        is BleMasterConnectedStatus.Disconnecting -> "断开中"
        BleMasterConnectedStatus.Idle -> "没有选择设备"
    }

val BleMasterConnectedStatus.connected:Boolean
    get() = this is BleMasterConnectedStatus.Connected
sealed interface BleMasterScanningStatus {
    data object ScanStopped : BleMasterScanningStatus

    data class Scanning(val filter: ScanFilter = "") : BleMasterScanningStatus
}

typealias ScanFilter = String



