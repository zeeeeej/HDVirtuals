package yunext.kotlin.bluetooth.ble.master

import android.bluetooth.BluetoothGatt
import android.os.Build
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.yunext.kmp.common.logger.HDLog
import com.yunext.kmp.common.logger.XLog
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.application
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleDevice
import yunext.kotlin.bluetooth.ble.core.XBleDownPayload
import yunext.kotlin.bluetooth.ble.core.XBleException
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.XBleUpPayload
import yunext.kotlin.bluetooth.ble.core.asDevice
import yunext.kotlin.bluetooth.ble.core.generateBleServiceOnlyByUUID
import yunext.kotlin.bluetooth.ble.core.generateXBleCharacteristicsOnlyByUUID
import yunext.kotlin.bluetooth.ble.util.uuidFromShort
import java.util.UUID
import kotlin.random.Random

internal class HDBleFastBleImpl internal constructor(hdContext: HDContext) : BleMaster,
    XLog by HDLog("HDBleFastBleImpl", true) {
    private val context = hdContext.application
    private val bleScope =
        CoroutineScope(Dispatchers.Main + SupervisorJob() + CoroutineName("HDBleMaster-Android"))

    private val scanCacheFastBle: MutableMap<String, com.clj.fastble.data.BleDevice> =
        mutableMapOf()

    private val connectCacheFastBle: MutableMap<String, com.clj.fastble.data.BleDevice> =
        mutableMapOf()

    init {
        init()
    }

    override fun init() {
        d("[init] ${Build.VERSION.SDK_INT}")
        BleManager.getInstance().let { bleManager ->
            bleManager.initScanRule(
                BleScanRuleConfig.Builder()
                    //.setServiceUuids(arrayOf(HBle.adUUID()))
                    .build()
            )
            //connectOverTime = DEFAULT_CONNECT_TIMEOUT * 10
            bleManager.splitWriteNum = 240

            bleManager.init(this@HDBleFastBleImpl.context)
        }
    }


    override fun startScan(onResult: (XBleMasterScanResult) -> Unit) {
        d("[startScan]")
        BleManager.getInstance().initScanRule(
            BleScanRuleConfig.Builder().setScanTimeOut(15000).build()
        )
        val bleScanCallback = object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                d("[startScan]onScanStarted success:$success")
            }

            override fun onScanning(bleDevice: com.clj.fastble.data.BleDevice?) {
                bleDevice ?: return
                val deviceName = bleDevice.name ?: ""
                d("[startScan]onScanning bleDevice:${bleDevice.mac}($deviceName)")
                if (deviceName.isEmpty()) return
                scanCacheFastBle[bleDevice.mac] = bleDevice
                onResult(XBleMasterScanResult(bleDevice.asDevice(), bleDevice.rssi))
            }

            override fun onScanFinished(scanResultList: MutableList<com.clj.fastble.data.BleDevice>?) {
                d("[startScan]onScanFinished scanResultList:$scanResultList")
            }

        }
        BleManager.getInstance().scan(bleScanCallback)
    }

    override fun stopScan() {
        d("[stopScan]")
        BleManager.getInstance().cancelScan()
    }

    override fun connect(device: XBleDevice, callback: XBleMasterConnectCallback) {
        i("[connect]>>> $device")
        BleManager.getInstance().cancelScan()
        val address = device.address
        // 检查连接列表是否存在
        val connectDevice = connectCacheFastBle[address]
        if (connectDevice != null) {
            if (BleManager.getInstance().isConnected(connectDevice)) {
                val msg = "[connect] 已经连接$device"
                w(msg)
                //callback(HDBleConnectFail(device, HBleException(msg)))
                return
            }
        }

        val bluetoothDevice =
            scanCacheFastBle[address] ?: connectCacheFastBle[address] ?: return run {
                val msg = "列表不存在:${device}"
                w(msg)
//            callback(HDBleConnectFail(device, HBleException(msg)))
            }
        if (BleManager.getInstance().isConnected(bluetoothDevice)) {
            debugForRead(device)
            val msg = "[connect] 已经连接$device"
            w(msg)
            //callback(HDBleConnectFail(device, HBleException(msg)))
            return
        }
        connectCacheFastBle[address] = bluetoothDevice
        BleManager.getInstance().connect(bluetoothDevice, object : BleGattCallback() {
            override fun onStartConnect() {
                d("[connect] onStartConnect")
            }

            override fun onConnectFail(
                bleDevice: com.clj.fastble.data.BleDevice?,
                exception: BleException?,
            ) {
                val msg = "[connect] onConnectFail ${bleDevice?.mac} $exception"
                d("[connect] $msg")
                callback(
                    BleMasterConnectFail(
                        device,
                        XBleException(msg)
                    )
                )
            }

            override fun onConnectSuccess(
                bleDevice: com.clj.fastble.data.BleDevice?,
                gatt: BluetoothGatt?,
                status: Int,
            ) {
                d("[connect] onConnectSuccess ${bleDevice?.mac} ")
                callback(BleMasterConnectSuccess(device))
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
                d("[connect] onDisConnected $msg")
                callback(
                    BleMasterConnectFail(
                        device,
                        XBleException(msg)
                    )
                )
            }

        })
    }

    override fun enableNotify(
        device: XBleDevice,
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterEnableNotifyCallback,
    ) {
        notify(
            device,
            generateBleServiceOnlyByUUID(uuidFromShort("a001")),
            characteristic = generateXBleCharacteristicsOnlyByUUID(uuidFromShort("b002"))
        ) {
            w("enableNotify:$it")
        }
    }

    private fun debugForNotify(device: XBleDevice, gatt: BluetoothGatt?) {
        //val AuthenticationNotifyData = RTCCmdData(RTCCmd.AuthenticationNotify, "a001", "b002")
        notify(
            device,
            generateBleServiceOnlyByUUID(uuidFromShort("a001")),
            characteristic = generateXBleCharacteristicsOnlyByUUID(uuidFromShort("b002"))
        ) {
            w("debugForNotify:$it")
        }
    }

    override fun notify(
        device: XBleDevice,
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterNotifyCallback,
    ) {
        val bleDevice = connectCacheFastBle[device.address] ?: return
        if (!BleManager.getInstance().isConnected(bleDevice)) {
            debugForRead(device)
            val msg = "[connect] 断开连接$device"
            w(msg)
            callback(
                BleMasterNotifyFail(
                    device,
                    XBleException(msg)
                )
            )
            return
        }
        BleManager.getInstance()
            .notify(bleDevice, service.uuid, characteristic.uuid, object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    d("[notify]onNotifySuccess ${service.uuid}/${characteristic.uuid} ")
                    callback(BleMasterNotifySuccess(device))
                }

                override fun onNotifyFailure(exception: BleException?) {
                    val msg = "${service.uuid}/${characteristic.uuid} $exception"
                    e("[notify]onNotifyFailure $msg")
                    callback(
                        BleMasterNotifyFail(
                            device,
                            XBleException(msg)
                        )
                    )
                }

                override fun onCharacteristicChanged(data: ByteArray?) {
                    d("[notify]onCharacteristicChanged ${service.uuid}/${characteristic.uuid} $data ${data?.decodeToString()}")
                    data ?: return
                    callback(BleMasterNotifyDataChanged(device, XBleUpPayload(data)))
                }
            })
    }

    override fun read(
        device: XBleDevice,
        service: XBleService,
        characteristic: XBleCharacteristics,
        callback: XBleMasterReadCallback,
    ) {
        val bleDevice = connectCacheFastBle[device.address] ?: return
        if (!BleManager.getInstance().isConnected(bleDevice)) {
            debugForRead(device)
            val msg = "[connect] 断开连接$device"
            w(msg)
            callback(BleMasterReadFail(device, XBleException(msg)))
            return
        }
        BleManager.getInstance()
            .read(bleDevice, service.uuid, characteristic.uuid, object : BleReadCallback() {
                override fun onReadSuccess(data: ByteArray?) {
                    d("[read]onReadSuccess ${service.uuid}/${characteristic.uuid}  $data ${data?.decodeToString()}")
                    data ?: return
                    callback(BleMasterReadSuccess(device, XBleUpPayload(data)))
                }

                override fun onReadFailure(exception: BleException?) {
                    val msg = "${service.uuid}/${characteristic.uuid} $exception"
                    e("[read]onReadFailure $msg")
                    callback(
                        BleMasterReadFail(
                            device,
                            XBleException(msg)
                        )
                    )
                }
            })
    }

    override fun write(
        device: XBleDevice,
        service: XBleService,
        characteristic: XBleCharacteristics,
        data: XBleDownPayload,
        callback: XBleMasterWriteCallback,
    ) {
        val bleDevice = connectCacheFastBle[device.address] ?: return
        if (!BleManager.getInstance().isConnected(bleDevice)) {
            debugForRead(device)
            val msg = "[connect] 断开连接$device"
            w(msg)
            callback(
                BleMasterWriteFail(
                    device,
                    XBleException(msg)
                )
            )
            return
        }
        BleManager.getInstance().write(bleDevice,
            service.uuid,
            characteristic.uuid,
            data.data,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                    d("[write]onWriteSuccess ${service.uuid}/${characteristic.uuid}  $total / $justWrite / ${justWrite?.decodeToString()}")
                    callback(BleMasterWriteSuccess(device))
                }

                override fun onWriteFailure(exception: BleException?) {
                    val msg = "${service.uuid}/${characteristic.uuid} $exception"
                    e("[write]onWriteFailure $msg")
                    callback(BleMasterWriteFail(device, XBleException(msg)))
                }

            })
    }

    override fun disconnect(device: XBleDevice) {
        val bleDevice = connectCacheFastBle[device.address] ?: return
        BleManager.getInstance().disconnect(bleDevice)
        connectCacheFastBle.remove(device.address)
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

    private fun debugForWrite(device: XBleDevice) {
        bleScope.launch(Dispatchers.IO) {
            coroutineScope {
                delay(1000)
                val service = UUID.fromString(uuidFromShort("A001"))
                val characteristicB004 = UUID.fromString(uuidFromShort("B004"))
                while (isActive) {
                    delay(1000)
                    write(
                        device,
                        generateBleServiceOnlyByUUID(service.toString()),
                        generateXBleCharacteristicsOnlyByUUID(characteristicB004.toString()),
                        XBleDownPayload(Random.nextBytes(4)),
                    ) {}
                }
            }
        }
    }


    override fun clear() {
        d("[clear]")
        try {
            scanCacheFastBle.clear()
            connectCacheFastBle.clear()
            BleManager.getInstance().disconnectAllDevice()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


}