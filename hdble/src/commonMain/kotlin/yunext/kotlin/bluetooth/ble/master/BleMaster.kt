package yunext.kotlin.bluetooth.ble.master

interface BleMaster {

    fun init()
    fun startScan(onResult: (XBleMasterScanResult) -> Unit)
    fun stopScan()

    fun connect(
        device: yunext.kotlin.bluetooth.ble.core.XBleDevice,
        callback: XBleMasterConnectCallback,
    )

    fun enableNotify( device: yunext.kotlin.bluetooth.ble.core.XBleDevice,
                      service:  yunext.kotlin.bluetooth.ble.core.XBleService,
                      characteristic: yunext.kotlin.bluetooth.ble.core.XBleCharacteristics,
                      callback: XBleMasterEnableNotifyCallback)

    fun notify(
        device: yunext.kotlin.bluetooth.ble.core.XBleDevice,
        service:  yunext.kotlin.bluetooth.ble.core.XBleService,
        characteristic: yunext.kotlin.bluetooth.ble.core.XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    )

    fun read(
        device: yunext.kotlin.bluetooth.ble.core.XBleDevice,
        service:  yunext.kotlin.bluetooth.ble.core.XBleService,
        characteristic: yunext.kotlin.bluetooth.ble.core.XBleCharacteristics,
        callback: XBleMasterReadCallback,
    )

    fun write(
        device: yunext.kotlin.bluetooth.ble.core.XBleDevice,
        service:  yunext.kotlin.bluetooth.ble.core.XBleService,
        characteristic: yunext.kotlin.bluetooth.ble.core.XBleCharacteristics,
        data: yunext.kotlin.bluetooth.ble.core.XBleDownPayload,
        callback: XBleMasterWriteCallback,
    )

    fun disconnect(device: yunext.kotlin.bluetooth.ble.core.XBleDevice)

    fun clear()
}


