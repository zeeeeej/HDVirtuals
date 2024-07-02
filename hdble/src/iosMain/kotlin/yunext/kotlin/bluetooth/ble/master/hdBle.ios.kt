package yunext.kotlin.bluetooth.ble.master

import com.yunext.kmp.context.HDContext
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleDevice
import yunext.kotlin.bluetooth.ble.core.XBleDownPayload
import yunext.kotlin.bluetooth.ble.core.XBleService

actual class HDBleMaster internal actual constructor(hdContext: HDContext):BleMaster {
    override fun init() {
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
        device: XBleDevice,
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterEnableNotifyCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun notify(
        device: XBleDevice,
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun read(
        device: XBleDevice,
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterReadCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun write(
        device: XBleDevice,
        service: XBleService,
        characteristic: XBleCharacteristics,
        data: XBleDownPayload,
        callback: XBleMasterWriteCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun disconnect(device: XBleDevice) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}

actual fun createBleMaster(): HDBleMaster {
    TODO("Not yet implemented")
}