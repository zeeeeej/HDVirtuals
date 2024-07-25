package yunext.kotlin.bluetooth.ble.master

import com.yunext.kmp.context.HDContext
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleDevice
import yunext.kotlin.bluetooth.ble.core.XBleDownPayload
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.logger.BleRecordCallback

actual class HDBleMaster internal actual constructor(hdContext: HDContext):BleMaster {
    override val connectedStatus: BleMasterConnectedStatus
        get() = TODO("Not yet implemented")
    override val scanningStatus: BleMasterScanningStatus
        get() = TODO("Not yet implemented")

    override fun init(
        onConnectedChanged: OnXBleMasterConnectedStatusChanged,
        onScanningStatusChanged: OnXBleMasterScanningStatusChanged,
        recordCallback: BleRecordCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun startScan(onResult: (XBleMasterScanResult) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun stopScan() {
        TODO("Not yet implemented")
    }

    override fun connect(device: XBleDevice, callback: XBleMasterConnectCallback) {
        TODO("Not yet implemented")
    }

    override fun enableNotify(
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun notify(
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun read(
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterReadCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun write(
        service: XBleService,
        characteristic: XBleCharacteristics,
        data: XBleDownPayload,
        callback: XBleMasterWriteCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}

actual fun createBleMaster(): HDBleMaster {
    TODO("Not yet implemented")
}