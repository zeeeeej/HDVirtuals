package yunext.kotlin.bluetooth.ble.master

import android.bluetooth.BluetoothGatt
import android.os.Build
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleMtuChangedCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.yunext.kmp.common.logger.HDLog
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.application
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleDevice
import yunext.kotlin.bluetooth.ble.core.XBleDownPayload
import yunext.kotlin.bluetooth.ble.core.XBleException
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.XBleUpPayload
import yunext.kotlin.bluetooth.ble.core.asDevice
import yunext.kotlin.bluetooth.ble.core.asService
import yunext.kotlin.bluetooth.ble.core.generateBleServiceOnlyByUUID
import yunext.kotlin.bluetooth.ble.core.generateXBleCharacteristicsOnlyByUUID
import yunext.kotlin.bluetooth.ble.display
import yunext.kotlin.bluetooth.ble.logger.BleRecordCallback
import yunext.kotlin.bluetooth.ble.logger.RecordHelper
import yunext.kotlin.bluetooth.ble.util.uuidFromShort
import java.lang.RuntimeException


internal class AndroidBleFastBleImpl internal constructor(
    hdContext: HDContext,
    helper: RecordHelper = RecordHelper(),
) : BleMaster {
    private val context = hdContext.application

    private var _connectStatus: Pair<BleMasterConnectedStatus, BleDevice?> =
        BleMasterConnectedStatus.Idle to null
    private var _scanningStatus: BleMasterScanningStatus = BleMasterScanningStatus.ScanStopped

    override val connectedStatus: BleMasterConnectedStatus
        get() = _connectStatus.first
    override val scanningStatus: BleMasterScanningStatus
        get() = _scanningStatus

    private val bleManager: BleManager
        get() = BleManager.getInstance()

    private var onConnectedChanged: OnXBleMasterConnectedStatusChanged? = null
    private var onScanningStatusChanged: OnXBleMasterScanningStatusChanged? = null

    private var recordCallback: BleRecordCallback? = null

    private val log = HDLog("AndroidBleFastBleImpl", true) {
        val add = helper.add(this, it)
        recordCallback?.invoke(add)
        this
    }

    override fun init(
        onConnectedChanged: OnXBleMasterConnectedStatusChanged,
        onScanningStatusChanged: OnXBleMasterScanningStatusChanged,
        recordCallback: BleRecordCallback,
    ) {
        log.i("[init] ${Build.VERSION.SDK_INT}")
        //connectOverTime = DEFAULT_CONNECT_TIMEOUT * 10
        bleManager.splitWriteNum = 240
        bleManager.init(this@AndroidBleFastBleImpl.context)
        this.onConnectedChanged = onConnectedChanged
        this.onScanningStatusChanged = onScanningStatusChanged
        this.recordCallback = recordCallback
        bleManager.setOperateTimeout(10000)
    }


    override fun startScan(onResult: (XBleMasterScanResult) -> Unit) {
        log.i("[startScan]")

        bleManager.initScanRule(
            BleScanRuleConfig.Builder()
                .setScanTimeOut(15000)
                //.setServiceUuids(arrayOf(HBle.adUUID()))
                .build()
        )
        val bleScanCallback = object : BleScanCallback() {

            override fun onScanStarted(success: Boolean) {
                log.i("[startScan]onScanStarted success:$success")
            }

            override fun onScanning(bleDevice: com.clj.fastble.data.BleDevice?) {
                val d = bleDevice ?: return
                val deviceName = d.name ?: ""
                log.i("[startScan]onScanning bleDevice:${d.mac}($deviceName)")
                if (deviceName.isEmpty()) return
                onResult(XBleMasterScanResult(d.asDevice(), d.rssi))
            }


            override fun onScanFinished(scanResultList: MutableList<com.clj.fastble.data.BleDevice>?) {
                log.i("[startScan]onScanFinished scanResultList:$scanResultList")
                onScanningChanged(BleMasterScanningStatus.ScanStopped)
            }

        }
        bleManager.scan(bleScanCallback)
        onScanningChanged(BleMasterScanningStatus.Scanning())
    }

    override fun stopScan() {
        log.i("[stopScan]")
        try {
            BleManager.getInstance().cancelScan()
        } catch (e: Exception) {
            log.e("[stopScan]error:${e.localizedMessage}")
        }
    }

    private fun onScanningChanged(status: BleMasterScanningStatus) {
        this._scanningStatus = status
        this.onScanningStatusChanged?.invoke(status)
    }

//    private fun tryPutScanToCache(deviceName: String, d: BleDevice) {
//        scanCache.removeAll { (k, _) ->
//            k.same(deviceName, d.mac)
//        }
//        scanCache.add(generateXBleDevice(deviceName = deviceName, address = d.mac) to d)
//    }

//    private fun tryGetFastBleDeviceFromScanCache(device: XBleDevice): BleDevice? {
//        return scanCache.singleOrNull { (k, _) ->
//            k.same(device)
//        }?.second
//    }

//    private fun curDevice() = when (val s = connectedStatus) {
//        is BleMasterConnectedStatus.Connected -> s.device
//        is BleMasterConnectedStatus.Connecting -> s.device
//        is BleMasterConnectedStatus.Disconnected -> s.device
//        is BleMasterConnectedStatus.Disconnecting -> s.device
//        BleMasterConnectedStatus.Idle -> null
//    }

//    private fun tryGetDevice(device: XBleDevice) =
//        tryGetFastBleDeviceFromScanCache(device)

    private fun tryGetFastBleDevice() = _connectStatus.second

    override fun connect(device: XBleDevice, callback: XBleMasterConnectCallback) {
        log.i("[connect]>>> $device")
        BleManager.getInstance().cancelScan()
        onScanningChanged(BleMasterScanningStatus.ScanStopped)
        when (connectedStatus) {
            is BleMasterConnectedStatus.Connected -> return
            is BleMasterConnectedStatus.Connecting -> return
            is BleMasterConnectedStatus.Disconnected -> {}
            is BleMasterConnectedStatus.Disconnecting -> {}
            BleMasterConnectedStatus.Idle -> {}
        }
        onConnectChanged(BleMasterConnectedStatus.Connecting(device), null)
        BleManager.getInstance().connect(device.address, object : BleGattCallback() {
            override fun onStartConnect() {
                log.i("[connect] onStartConnect")
            }

            override fun onConnectFail(
                bleDevice: com.clj.fastble.data.BleDevice?,
                exception: BleException?,
            ) {
                val msg = "[connect] onConnectFail ${bleDevice?.mac} $exception"
                log.i("[connect] $msg")
                callback(
                    BleMasterConnectFail(
                        device,
                        XBleException(msg)
                    )
                )
                onConnectChanged(BleMasterConnectedStatus.Disconnected(device), bleDevice)
            }

            override fun onConnectSuccess(
                bleDevice: com.clj.fastble.data.BleDevice,
                gatt: BluetoothGatt?,
                status: Int,
            ) {
                log.i("[connect] onConnectSuccess ${bleDevice.mac} gatt:${gatt.display} ")

                callback(BleMasterConnectSuccess(device))
                bleManager.setMtu(bleDevice, 240, object : BleMtuChangedCallback() {
                    override fun onSetMTUFailure(exception: BleException?) {
                        log.i("[connect] onSetMTUFailure exception:${exception} ")
                    }

                    override fun onMtuChanged(mtu: Int) {
                        log.i("[connect] onMtuChanged mtu:$mtu ")
                    }

                })
                onConnectChanged(BleMasterConnectedStatus.Connected(device, gatt?.services?.map {
                    it.asService()
                } ?: emptyList()), bleDevice)

                // debugForRead(device)

                // debugForWrite(device)

                //debugForNotify(device, gatt)
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                bleDevice: com.clj.fastble.data.BleDevice?,
                gatt: BluetoothGatt?,
                status: Int,
            ) {
                val msg =
                    "${device.address}  isActiveDisConnected:$isActiveDisConnected status:$status"
                log.i("[connect] onDisConnected $msg")
                callback(
                    BleMasterConnectFail(
                        device,
                        XBleException(msg)
                    )
                )

                onConnectChanged(BleMasterConnectedStatus.Disconnected(device), bleDevice)
            }

        })
    }

    private fun onConnectChanged(status: BleMasterConnectedStatus, device: BleDevice?) {
        this._connectStatus = status to device
        this.onConnectedChanged?.invoke(status)
    }

    override fun enableNotify(
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    ) {
        notify(
            generateBleServiceOnlyByUUID(service.uuid),
            characteristic = generateXBleCharacteristicsOnlyByUUID(characteristic.uuid)
        ) {
//            log.i("enableNotify:$it")
            callback.invoke(it)

        }
    }

//    private fun debugForNotify(device: XBleDevice, gatt: BluetoothGatt?) {
//        //val AuthenticationNotifyData = RTCCmdData(RTCCmd.AuthenticationNotify, "a001", "b002")
//        notify(
//            device,
//            generateBleServiceOnlyByUUID(uuidFromShort("a001")),
//            characteristic = generateXBleCharacteristicsOnlyByUUID(uuidFromShort("b002"))
//        ) {
//            log.i("debugForNotify:$it")
//        }
//    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun notify(
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    ) {
        log.i("[notify]notify 1->${service.uuid}/${characteristic.uuid}")
        val bleDevice = tryGetFastBleDevice() ?: return
        log.i("[notify]notify 2->${service.uuid}/${characteristic.uuid} ${bleDevice.mac}")
        if (!BleManager.getInstance().isConnected(bleDevice)) {
            val msg = "[notify] 断开连接$bleDevice"
            log.i(msg)
            callback(
                BleMasterNotifyFail(
                    bleDevice.asDevice(),
                    XBleException(msg)
                )
            )
            return
        }
        bleManager
            .notify(bleDevice, service.uuid, characteristic.uuid, object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    log.i("[notify]onNotifySuccess ${service.uuid}/${characteristic.uuid} ")
                    callback(BleMasterNotifySuccess(bleDevice.asDevice()))
                }

                override fun onNotifyFailure(exception: BleException?) {
                    val msg = "${service.uuid}/${characteristic.uuid} $exception"
                    log.i("[notify]onNotifyFailure $msg")
                    callback(
                        BleMasterNotifyFail(
                            bleDevice.asDevice(),
                            XBleException(msg)
                        )
                    )
                    try {
                        throw RuntimeException()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }

                override fun onCharacteristicChanged(data: ByteArray?) {
                    log.i("[notify]onCharacteristicChanged ${service.uuid}/${characteristic.uuid} $data ${data?.toHexString()}")
                    data ?: return
                    callback(BleMasterNotifyDataChanged(bleDevice.asDevice(), XBleUpPayload(data)))
                }
            })
    }

    override fun read(
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterReadCallback,
    ) {
        log.i("[read]${service.uuid}/${characteristic.uuid}")
        val bleDevice = tryGetFastBleDevice() ?: throw XBleException("未知设备")
        if (!BleManager.getInstance().isConnected(bleDevice)) {
//            debugForRead(device)
            val msg = "[connect] 断开连接$bleDevice"
            log.w(msg)
            callback(BleMasterReadFail(bleDevice.asDevice(), XBleException(msg)))
            return
        }
        BleManager.getInstance()
            .read(bleDevice, service.uuid, characteristic.uuid, object : BleReadCallback() {
                override fun onReadSuccess(data: ByteArray?) {
                    log.i("[read]onReadSuccess ${service.uuid}/${characteristic.uuid}  $data ${data?.decodeToString()}")
                    data ?: return
                    callback(BleMasterReadSuccess(bleDevice.asDevice(), XBleUpPayload(data)))
                }

                override fun onReadFailure(exception: BleException?) {
                    val msg = "${service.uuid}/${characteristic.uuid} $exception"
                    log.e("[read]onReadFailure $msg")
                    callback(
                        BleMasterReadFail(
                            bleDevice.asDevice(),
                            XBleException(msg)
                        )
                    )

                    try {
                        throw XBleException("测试read")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun write(
        service: XBleService,
        characteristic: XBleCharacteristics,
        data: XBleDownPayload,
        callback: XBleMasterWriteCallback,
    ) {
        val device = tryGetFastBleDevice() ?: throw XBleException("未知设备")
        if (!BleManager.getInstance().isConnected(device)) {
            debugForRead(device.asDevice())
            val msg = "[connect] 断开连接$device"
            log.w(msg)
            callback(
                BleMasterWriteFail(
                    device.asDevice(),
                    XBleException(msg)
                )
            )
            return
        }
        BleManager.getInstance().write(device,
            service.uuid,
            characteristic.uuid,
            data.data,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                    log.i("[write]onWriteSuccess ${service.uuid}/${characteristic.uuid}  $total / $justWrite / ${justWrite?.toHexString()}")
                    callback(BleMasterWriteSuccess(device.asDevice()))
                }

                override fun onWriteFailure(exception: BleException?) {
                    val msg = "${service.uuid}/${characteristic.uuid} $exception"
                    log.e("[write]onWriteFailure $msg")
                    callback(BleMasterWriteFail(device.asDevice(), XBleException(msg)))
                }

            })
    }

    override fun disconnect() {
        val (status, bleDevice) = _connectStatus
        if (bleDevice != null) {
            BleManager.getInstance().disconnect(bleDevice)
        }
        when (status) {
            is BleMasterConnectedStatus.Connected -> {
                onConnectChanged(BleMasterConnectedStatus.Disconnected(status.device), bleDevice)
            }

            is BleMasterConnectedStatus.Connecting -> {}
            is BleMasterConnectedStatus.Disconnected -> {}
            is BleMasterConnectedStatus.Disconnecting -> {}
            BleMasterConnectedStatus.Idle -> {}
        }


    }


    private fun debugForRead(device: XBleDevice) {
//        bleScope.launch {
//            coroutineScope {
//                delay(1000)
//                val service = UUID.fromString(uuid("A001"))
//                val characteristicB001 = UUID.fromString(uuid("B001"))
//                val characteristicB002 = UUID.fromString(uuid("B002"))
//                val characteristicB003 = UUID.fromString(uuid("B003"))
//                val characteristicB004 = UUID.fromString(uuid("B004"))
//                read(
//                    device,
//                    XBleService(service.toString()),
//                    XBleCharacteristic(
//                        characteristicB001.toString(),
//                        emptyArray<CharacteristicsProperty>(),
//                        CharacteristicsPermission.NONE
//                    )
//                ) {}
//
//                delay(500)
//                read(
//                    device,
//                    XBleService(service.toString()),
//                    XBleCharacteristic(
//                        characteristicB001.toString(),
//                        emptyArray<CharacteristicsProperty>(),
//                        CharacteristicsPermission.NONE
//                    )
//
//                ) {}
//                delay(500)
//                read(
//                    device,
//                    XBleService(service.toString()),
//                    XBleCharacteristic(
//                        characteristicB001.toString(),
//                        emptyArray<CharacteristicsProperty>(),
//                        CharacteristicsPermission.NONE
//                    )
//                ) {}
//                delay(500)
//                read(
//                    device,
//                    XBleService(service.toString()),
//                    XBleCharacteristic(
//                        characteristicB001.toString(),
//                        emptyArray<CharacteristicsProperty>(),
//                        CharacteristicsPermission.NONE
//                    )
//                ) {}
//            }
//        }
    }

//    private fun debugForWrite(device: XBleDevice) {
//        bleScope.launch(Dispatchers.IO) {
//            coroutineScope {
//                delay(1000)
//                val service = UUID.fromString(uuidFromShort("A001"))
//                val characteristicB004 = UUID.fromString(uuidFromShort("B004"))
//                while (isActive) {
//                    delay(1000)
//                    write(
//                        device,
//                        generateBleServiceOnlyByUUID(service.toString()),
//                        generateXBleCharacteristicsOnlyByUUID(characteristicB004.toString()),
//                        XBleDownPayload(Random.nextBytes(4)),
//                    ) {}
//                }
//            }
//        }
//    }


    override fun clear() {
        log.i("[clear]")
        try {
            this.onScanningStatusChanged = null
            this.onConnectedChanged = null
            this.recordCallback = null
//            scanCache.clear()
//            connectCacheFastBle.clear()
            BleManager.getInstance().disconnectAllDevice()
//            bleScope.cancel()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


}