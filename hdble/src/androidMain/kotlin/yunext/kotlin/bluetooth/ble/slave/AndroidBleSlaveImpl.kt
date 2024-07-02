package yunext.kotlin.bluetooth.ble.slave

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetCallback
import android.bluetooth.le.AdvertisingSetCallback.ADVERTISE_SUCCESS
import android.bluetooth.le.AdvertisingSetParameters
import android.bluetooth.le.AdvertisingSetParameters.TX_POWER_MAX
import android.bluetooth.le.PeriodicAdvertisingParameters
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.clj.fastble.callback.BleReadCallback
import com.yunext.kmp.common.logger.HDLog
import com.yunext.kmp.common.logger.XLog
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.application
import yunext.kotlin.bluetooth.ble.BluetoothConstant
import yunext.kotlin.bluetooth.ble.Work
import yunext.kotlin.bluetooth.ble.WorkQueue
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleException
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.XCharacteristicsProperty
import yunext.kotlin.bluetooth.ble.core.asCharacteristics
import yunext.kotlin.bluetooth.ble.core.asDevice
import yunext.kotlin.bluetooth.ble.core.asService
import yunext.kotlin.bluetooth.ble.core.generateBleService
import yunext.kotlin.bluetooth.ble.core.generateXBleDevice
import yunext.kotlin.bluetooth.ble.core.toPermission
import yunext.kotlin.bluetooth.ble.core.toProperty
import yunext.kotlin.bluetooth.ble.display
import yunext.kotlin.bluetooth.ble.logger.BleRecordCallback
import yunext.kotlin.bluetooth.ble.logger.RecordHelper
import yunext.kotlin.bluetooth.ble.logger.XBleRecord
import yunext.kotlin.bluetooth.ble.logger.XBleRecordType
import yunext.kotlin.bluetooth.ble.util.uuidFromShort
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

// 参考 GattServerSessionScope
internal class AndroidBleSlaveImpl(
    hdContext: HDContext,
    override val configuration: SlaveConfiguration,
    private val helper: RecordHelper = RecordHelper(),
) :
    BleSlave {
    private val context: Context = hdContext.application.applicationContext
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter
    private var bluetoothGattServer: BluetoothGattServer? = null

    private val serviceList: List<XBleService>

    private val log: XLog = HDLog(BluetoothConstant.TAG, true) {
        onRecordChanged(this, it)
        this
    }

    init {
        log.i("[init serviceList]${configuration.services.size}")
        // merge 相通XBleService的XBleCharacteristics合并到一个XBleService里
        val map: MutableMap<String, XBleService> = mutableMapOf()
        configuration.services.forEach { slaveService ->
            val service = map[slaveService.uuid]
            if (service == null) {
                map[slaveService.uuid] = slaveService
            } else {
                val old = service.characteristics
                val add = slaveService.characteristics
                val result = (old + add).distinctBy { it.uuid }
                map[slaveService.uuid] = generateBleService(slaveService.uuid, result)
            }
        }
        serviceList = map.values.toList()
    }

    override val address: String
        get() = configuration.address

    override val deviceName: String
        get() = configuration.deviceName

    private var _broadcasting: BroadcastStatus = BroadcastStatus.BroadcastStopped

    override val broadcasting: BroadcastStatus
        get() = _broadcasting

    /**
     * 当前连接Device，只能连接一个
     */
    private var _connectStatus: Pair<ConnectStatus, BluetoothDevice?> =
        ConnectStatus.Disconnected to null

    override val connectStatus: ConnectStatus
        get() = _connectStatus.first


    /* * * * * * * * * * * * * * * * * * * * * * callbacks a * * * * * * * * * * * * * * * *  * * */
    //region callbacks
    private var serverCallback: BleSlaveCallback? = null
    private var statusCallback: BleSlaveStatusCallback? = null
    private var recordCallback: BleRecordCallback? = null


    private val advertisingSetCallback =
        @RequiresApi(Build.VERSION_CODES.O)
        object : AdvertisingSetCallback() {
            override fun onScanResponseDataSet(advertisingSet: AdvertisingSet?, status: Int) {
                super.onScanResponseDataSet(advertisingSet, status)
                log.i("[advertisingSetCallback]onScanResponseDataSet advertisingSet:${advertisingSet?.hashCode()} status:$status")
            }

            override fun onAdvertisingSetStarted(
                advertisingSet: AdvertisingSet?,
                txPower: Int,
                status: Int,
            ) {
                super.onAdvertisingSetStarted(advertisingSet, txPower, status)
                log.i("[advertisingSetCallback]onAdvertisingSetStarted advertisingSet:${advertisingSet?.hashCode()} status:$status txPower:$txPower")

//                onBroadcastingChanged(BroadcastStatus.Started(configuration))
//                initServices {
//                    serviceList.map {
//                        createService(it)
//                    }
//                }
            }

            override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet?) {
                super.onAdvertisingSetStopped(advertisingSet)
                log.i("[advertisingSetCallback]onAdvertisingSetStopped advertisingSet:${advertisingSet?.hashCode()}")
                onBroadcastingChanged(BroadcastStatus.BroadcastStopped)
            }

            override fun onAdvertisingEnabled(
                advertisingSet: AdvertisingSet?,
                enable: Boolean,
                status: Int,
            ) {
                super.onAdvertisingEnabled(advertisingSet, enable, status)
                log.i("[advertisingSetCallback]onAdvertisingEnabled advertisingSet:${advertisingSet?.hashCode()} status:$status enable:$enable")
            }

            override fun onAdvertisingDataSet(advertisingSet: AdvertisingSet?, status: Int) {
                super.onAdvertisingDataSet(advertisingSet, status)
                log.i("[advertisingSetCallback]onAdvertisingDataSet advertisingSet:${advertisingSet?.hashCode()} status:$status")
            }

            override fun onAdvertisingParametersUpdated(
                advertisingSet: AdvertisingSet?,
                txPower: Int,
                status: Int,
            ) {
                super.onAdvertisingParametersUpdated(advertisingSet, txPower, status)
                log.i("[advertisingSetCallback]onAdvertisingParametersUpdated advertisingSet:${advertisingSet?.hashCode()} status:$status txPower:$txPower")
            }

            override fun onPeriodicAdvertisingParametersUpdated(
                advertisingSet: AdvertisingSet?,
                status: Int,
            ) {
                super.onPeriodicAdvertisingParametersUpdated(advertisingSet, status)
                log.i("[advertisingSetCallback]onPeriodicAdvertisingParametersUpdated advertisingSet:${advertisingSet?.hashCode()} status:$status")
            }

            override fun onPeriodicAdvertisingDataSet(
                advertisingSet: AdvertisingSet?,
                status: Int,
            ) {
                super.onPeriodicAdvertisingDataSet(advertisingSet, status)
                log.i("[advertisingSetCallback]onPeriodicAdvertisingDataSet advertisingSet:${advertisingSet?.hashCode()} status:$status")
            }

            override fun onPeriodicAdvertisingEnabled(
                advertisingSet: AdvertisingSet?,
                enable: Boolean,
                status: Int,
            ) {
                super.onPeriodicAdvertisingEnabled(advertisingSet, enable, status)
                log.i("[advertisingSetCallback]onPeriodicAdvertisingEnabled advertisingSet:${advertisingSet?.hashCode()} status:$status enable:$enable")
            }
        }

    /**
     * 广播回调
     */
    private inner class MyAdvertiseCallback : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            log.i("[advertiseCallback]onStartSuccess 广播开启成功 = $settingsInEffect")
            onBroadcastingChanged(BroadcastStatus.Started(configuration))

//            if (retryBroadcast.compareAndSet(true,false))
            if (retryBroadcast.get()) {
                initServices {
                    serviceList.map {
                        createService(it)
                    }
                }
            } else {
                log.e("重新广播！！！")
                stopBroadcast()
                startBroadcastInternal()
                retryBroadcast.set(true)
            }


        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            log.i("[advertiseCallback]onStartFailure 广播开启失败 errorCode = $errorCode ${errorCode.errorCode()}")
            onBroadcastingChanged(BroadcastStatus.BroadcastStopped)
        }

        private fun Int.errorCode(): String {
            return when (this) {
                ADVERTISE_SUCCESS -> "ADVERTISE_SUCCESS"
                ADVERTISE_FAILED_DATA_TOO_LARGE -> "ADVERTISE_FAILED_DATA_TOO_LARGE"
                ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS"
                ADVERTISE_FAILED_ALREADY_STARTED -> "ADVERTISE_FAILED_ALREADY_STARTED"
                ADVERTISE_FAILED_INTERNAL_ERROR -> "ADVERTISE_FAILED_INTERNAL_ERROR"
                ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "ADVERTISE_FAILED_FEATURE_UNSUPPORTED"
                else -> "未知"
            }
        }
    }

    private var advertiseCallback: MyAdvertiseCallback? = null
//    private val advertiseCallback = object : AdvertiseCallback() {
//        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
//            super.onStartSuccess(settingsInEffect)
//            i("[advertiseCallback]onStartSuccess 广播开启成功 = $settingsInEffect")
//            onBroadcastingChanged(BroadcastStatus.Started(configuration))
//            initServices {
//                serviceList.map {
//                    createService(it)
//                }
//            }
//
//
//        }
//
//        override fun onStartFailure(errorCode: Int) {
//            super.onStartFailure(errorCode)
//            i("[advertiseCallback]onStartFailure 广播开启失败 errorCode = $errorCode ${errorCode.errorCode()}")
//            onBroadcastingChanged(BroadcastStatus.BroadcastStopped)
//        }
//
//        private fun Int.errorCode(): String {
//            return when (this) {
//                ADVERTISE_SUCCESS -> "ADVERTISE_SUCCESS"
//                ADVERTISE_FAILED_DATA_TOO_LARGE -> "ADVERTISE_FAILED_DATA_TOO_LARGE"
//                ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS"
//                ADVERTISE_FAILED_ALREADY_STARTED -> "ADVERTISE_FAILED_ALREADY_STARTED"
//                ADVERTISE_FAILED_INTERNAL_ERROR -> "ADVERTISE_FAILED_INTERNAL_ERROR"
//                ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "ADVERTISE_FAILED_FEATURE_UNSUPPORTED"
//                else -> "未知"
//            }
//        }
//    }


    /**
     * 服务回调
     */
    @OptIn(ExperimentalStdlibApi::class)
    private val bluetoothGattServerCallback = object : BluetoothGattServerCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(
            device: BluetoothDevice?,
            status: Int,
            newState: Int,
        ) {
            super.onConnectionStateChange(device, status, newState)
            log.i("[bluetoothGattServerCallback]onConnectionStateChange device=${device?.display} status=$status newState=$newState")
            val d = device ?: return
//            val callbackBlock: (Boolean) -> Unit = {
//                callServerCallback {
//                    this(
//                        BleSlaveOnConnectionStateChange(
//                            d.asDevice(), status, it
//                        )
//                    )
//                }
//            }
            val last = connectStatus

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    log.d("[bluetoothGattServerCallback]onConnectionStateChange 有新的客户端连接...last:${last} ,now:${device.address} ")

                    // 连接成功后，需要调用connect
                    bluetoothGattServer?.connect(d, false)

                    when (last) {
                        is ConnectStatus.Connected -> {
                            if (last.device.address != device.address) {
                                log.w("[onDeviceConnected] 已经被${last}连接，取消当前${device.address}连接。")
                                bluetoothGattServer?.cancelConnection(d)
                            } else {
                                // same device connected. ignore
                                log.w("[onDeviceConnected] 已经连接")
                            }
                        }

                        ConnectStatus.Disconnected -> {
                            // 无设备连接
                            log.d("[onDeviceConnected] 当前可连接")
                            // 停止广播
                            stopBroadcast()
                            // 更新连接状态
                            onConnectedChanged(
                                ConnectStatus.Connected(device = d.asDevice()),
                                device
                            )
                        }
                    }
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    log.w("[bluetoothGattServerCallback]onConnectionStateChange 已断开")

                    bluetoothGattServer?.cancelConnection(d)

                    when (last) {
                        is ConnectStatus.Connected -> {
                            if (last.device.address == d.address) {
                                disconnect()
                                stopBroadcast()
                                startBroadcastInternal()
                            }
                        }

                        ConnectStatus.Disconnected -> {
                            // all ready disconnect. ignore
                            log.w("[onDeviceConnected] 已经断开")
                        }
                    }

                }

                else -> {
                    log.w("[bluetoothGattServerCallback]onConnectionStateChange 未知情况")
                }
            }
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic,
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            log.i("[onCharacteristicReadRequest] ${device?.display} requestId:${requestId} offset:$offset characteristic:${characteristic.uuid.toString()}")
            val d = device ?: return
            onServerChanged(
                BleSlaveOnCharacteristicReadRequest(
                    d.asDevice(),
                    requestId,
                    offset,
                    characteristic.asCharacteristics()
                ), device
            )
        }

        override fun onExecuteWrite(
            device: BluetoothDevice?,
            requestId: Int,
            execute: Boolean,
        ) {
            super.onExecuteWrite(device, requestId, execute)
            log.i("[onExecuteWrite] ${device?.display} requestId:$requestId execute:$execute")
            val d = device ?: return
            onServerChanged(
                BleSlaveOnExecuteWrite(
                    d.asDevice(),
                    requestId,
                    execute,
                ), device
            )

        }

        override fun onDescriptorReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            descriptor: BluetoothGattDescriptor?,
        ) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor)
            log.i("[onDescriptorReadRequest] ${device?.display} requestId:$requestId offset:$offset descriptor:${descriptor?.uuid}")
            val d = device ?: return
            onServerChanged(

                BleSlaveOnDescriptorReadRequest(
                    d.asDevice(),
                    requestId,
                    offset,
                ), device
            )

        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?,
        ) {
            super.onCharacteristicWriteRequest(
                device, requestId, characteristic, preparedWrite, responseNeeded, offset, value
            )
            log.i(
                "[onCharacteristicWriteRequest] requestId:$requestId ${characteristic.uuid} preparedWrite:$preparedWrite responseNeeded:$responseNeeded offset:$offset value:${value?.toHexString()}"
            )
            val d = device ?: return
            onServerChanged(
                BleSlaveOnCharacteristicWriteRequest(
                    d.asDevice(),
                    requestId,
                    characteristic = characteristic.asCharacteristics(),
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value
                ), device
            )

        }

        @SuppressLint("MissingPermission")
        override fun onDescriptorWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            descriptor: BluetoothGattDescriptor?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?,
        ) {
            super.onDescriptorWriteRequest(
                device, requestId, descriptor, preparedWrite, responseNeeded, offset, value
            )
            log.i("[onDescriptorWriteRequest] ${device.display} requestId:${requestId} descriptor:${descriptor?.uuid} preparedWrite:$preparedWrite responseNeeded:$responseNeeded offset:$offset value:${value?.toHexString()}")
            val d = device ?: return

            onServerChanged(
                BleSlaveOnDescriptorWriteRequest(
                    d.asDevice(),
                    requestId,
//                       descriptor,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value

                ), device
            )


//            bluetoothGattServer?.run {
//                sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
//            }
        }

        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
            log.i("[onNotificationSent] ${device.display} status:$status")
            val d = device ?: return
            onServerChanged(
                BleSlaveOnNotificationSent(
                    d.asDevice(),
                    status
                ), device
            )

        }

        override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
            super.onMtuChanged(device, mtu)
            log.i("[onMtuChanged] ${device.display} mtu:$mtu")
            val d = device ?: return
            onServerChanged(
                BleSlaveOnMtuChanged(
                    d.asDevice(),
                    mtu
                ), device
            )

        }

        override fun onPhyRead(
            device: BluetoothDevice?,
            txPhy: Int,
            rxPhy: Int,
            status: Int,
        ) {
            super.onPhyRead(device, txPhy, rxPhy, status)
            log.i("[onPhyRead]${device.display} txPhy:$txPhy rxPhy:$rxPhy status:$status")
            val d = device ?: return
            onServerChanged(
                BleSlaveOnPhyRead(
                    d.asDevice(),
                    txPhy, rxPhy, status
                ), device
            )

        }

        override fun onPhyUpdate(
            device: BluetoothDevice?,
            txPhy: Int,
            rxPhy: Int,
            status: Int,
        ) {
            super.onPhyUpdate(device, txPhy, rxPhy, status)
            log.i("[onPhyUpdate]${device.display} txPhy:$txPhy rxPhy:$rxPhy status:$status")
            val d = device ?: return
            onServerChanged(
                BleSlaveOnPhyUpdate(
                    d.asDevice(),
                    txPhy, rxPhy, status
                ), device
            )

        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService) {
            super.onServiceAdded(status, service)
            log.i("[onServiceAdded] status:$status service:${service.uuid}/${service.characteristics}")
            log.d("【添加服务成功！】")
            log.d("------------------------")
            onServerChanged(
                BleSlaveOnServiceAdded(
                    generateXBleDevice("", address = configuration.address), service.asService()
                ), null
            )


        }
    }


    private val workQueue: WorkQueue<Work> = WorkQueue()
    private val pool = Executors.newSingleThreadExecutor()

    init {
        pool.submit {
            while (true) {
                workQueue.take {
                    when (this) {
                        is BleSlaveResponse -> {
                            sendResponseInternal(this)
                        }

                        is BleSlaveRequest -> {
                            notifyChangedInternal(this)
                        }

                    }
                }
            }
        }

    }
    //</editor-fold>
    //endregion
    /* * * * * * * * * * * * * * * * * * * * * * callbacks z * * * * * * * * * * * * * * * *  * * */

    private fun onConnectedChanged(status: ConnectStatus, device: BluetoothDevice?) {
        _connectStatus = status to device
        statusCallback?.invoke(status.asEvent())
    }

    private fun onBroadcastingChanged(status: BroadcastStatus) {
        log.i("[onBroadcastingChanged]$status")
        _broadcasting = status
        statusCallback?.invoke(status.asEvent())
    }

    private fun onServerChanged(status: BleSlaveServerEvent, device: BluetoothDevice?) {
        serverCallback?.invoke(status)
    }

    private fun onRecordChanged(msg: String, type: XBleRecordType) {
        val add = helper.add(msg, type)
        recordCallback?.invoke(add)
    }

    override fun init(
        statusCallback: BleSlaveStatusCallback, serverCallback: BleSlaveCallback,
        recordCallback: BleRecordCallback,
    ) {
        log.i("[init]")
        this.statusCallback = statusCallback
        this.serverCallback = serverCallback
        this.recordCallback = recordCallback
    }

    @SuppressLint("MissingPermission")
    override fun enable(enable: Boolean) {
        if (enable) {
            if (!adapter.isEnabled) {
                adapter.enable()
            }
        } else {
            if (adapter.isEnabled) {
                adapter.disable()
            }
        }
    }

    private val retryBroadcast = AtomicBoolean(false)
    override fun startBroadcast() {
        log.i("[startBroadcast] content:$deviceName")
        retryBroadcast.set(false)
        startBroadcastInternal()
    }

    private fun startBroadcastInternal() {
        log.i("[startBroadcastInternal] content:$deviceName")
        val content = this.deviceName
        val uuid = createUUID("AAAA")
        broadcastInternal(content, uuid)
    }


    /**
     * 开启广播
     */
    @SuppressLint("MissingPermission")
    private fun broadcastInternal(
        name: String,
        serviceUUID: UUID,
    ) {
        log.i("[broadcastInternal] name:$name ")
        onBroadcastingChanged(BroadcastStatus.Init(configuration))
        //val payload = "123456789012345".toByteArray()
//        val payload = ("12345678"+name).toByteArray()
//        d("【准备广播】payload:${payload.size}")

        if (!adapter.isEnabled) {
            throw XBleException("蓝牙已关闭！")
        }
        log.d("蓝牙状态 ok")
//        adapter.setName(mac)
        log.d("设置蓝牙名称:[$name]")
        val setNameResult = adapter.setName(name)
        log.d("设置蓝牙名称:[$name] 结果:$setNameResult")

        log.d("配置广播中...")
        val bluetoothLeAdvertiser = adapter.bluetoothLeAdvertiser ?: return


        val advertiseData =
            AdvertiseData.Builder()
                .setIncludeDeviceName(true)
//                .setIncludeTxPowerLevel(true)
//                .addManufacturerData(
//                    0x09,
//                    payload
//                )
//                .addManufacturerData(
//                    0x08,
//                    "xpl".toByteArray()
//                )

//                .addManufacturerData(
//                    0x0A,
//                    byteArrayOf(0x01)
//                )
//                .addServiceUuid(ParcelUuid(serviceUUID))
                .build()
//        val uuid = UUID.fromString(uuidFromShort("ae01"))
//        d("[uuid]$uuid")
        val scanResponseData =
            AdvertiseData.Builder().setIncludeDeviceName(false).setIncludeTxPowerLevel(false)
//                .addServiceUuid(ParcelUuid(uuid))
//                .addManufacturerData(
//                    0xFF, payload   /*+ content + byteArrayOf(1, 2)*/
//                )
                .setIncludeDeviceName(true)
                .build()
        log.d("开始广播...")





        if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val advertisingSetParameters = AdvertisingSetParameters.Builder()
                .setConnectable(true)
                //.setAnonymous(false)
                .setLegacyMode(true)
                // .setDiscoverable(true) // 34
                //.setIncludeTxPower(false)// 电源
                //.setInterval(INTERVAL_LOW)// 广播间隔
                .setScannable(true) // Advertising can't be both connectable and scannable
//                .setPrimaryPhy()
//                .setSecondaryPhy()
                .setTxPowerLevel(TX_POWER_MAX)
                .build()
            val periodicAdvertisingParameters = PeriodicAdvertisingParameters.Builder()
//                .setIncludeTxPower(false)
//                .setInterval()
                .build()

            bluetoothLeAdvertiser.startAdvertisingSet(
                advertisingSetParameters,
                advertiseData, scanResponseData,
                periodicAdvertisingParameters, scanResponseData,
                advertisingSetCallback
            )
        } else {
            val advertiseSettings =
                AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setTimeout(180000)
//                .setTimeout(18000)
                    .setConnectable(true)
                    .build()
            val callback = advertiseCallback
            advertiseCallback = null
            if (callback != null) {
                bluetoothLeAdvertiser.stopAdvertising(callback)
            }
            val newCallback = MyAdvertiseCallback()
            advertiseCallback = newCallback
//            clearUp(bluetoothLeAdvertiser)
            bluetoothLeAdvertiser.startAdvertising(
                advertiseSettings, advertiseData, scanResponseData, newCallback
            )
        }
    }

//    private fun clearUp(bluetoothLeAdvertiser: BluetoothLeAdvertiser) {
//        try {
//            e("[clearUp] start!")
//            val method = bluetoothLeAdvertiser.javaClass.declaredMethods.singleOrNull {
//                e("[clearUp]    .....${it.name}")
//                it.name == "cleanup"
//            } ?: bluetoothLeAdvertiser.javaClass.getDeclaredMethod("cleanup")?:return
//            method.isAccessible = true
//            method.invoke(bluetoothLeAdvertiser)
//            e("[clearUp] ok!")
//        } catch (e: Throwable) {
//            e("[clearUp]${e.message}")
//            e.printStackTrace()
//        }
//    }

    /**
     * 初始化广播
     */
    @SuppressLint("MissingPermission")
    private fun initServices(block: () -> List<BluetoothGattService>) {
        log.i("[initServices]")
        log.d("初始化广播服务..")


        val curBluetoothGattServer =
            bluetoothManager.openGattServer(context, bluetoothGattServerCallback)
        val bluetoothGattServices = block()
        log.d("开始创建蓝牙服务...")
        var result: Boolean = true
        bluetoothGattServices.forEach {
            log.d("->添加服务...")
            val r = curBluetoothGattServer.addService(it)
            log.d("->添加服务${if (r) "成功" else "失败"}")
            if (!r) {
                result = false
                return@forEach
            }
        }
        log.d("开始创建蓝牙服务完毕$result")
        if (result) {
            bluetoothGattServer = curBluetoothGattServer
            onBroadcastingChanged(BroadcastStatus.Broadcasting(configuration))
        } else {
            curBluetoothGattServer.clearServices()
            curBluetoothGattServer.close()
            stopBroadcast()
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopBroadcast() {
        log.i(" log.[stopBroadcast]")
        try {
            log.d("[stopBroadcast]stopAdvertising")
            val callback = advertiseCallback ?: return
            adapter.bluetoothLeAdvertiser?.stopAdvertising(callback)
        } catch (e: Exception) {
            log.d("[stopBroadcast]stopAdvertising error ${e.message}")
        } finally {
            advertiseCallback = null
            onBroadcastingChanged(BroadcastStatus.BroadcastStopped)
        }

    }

    override fun sendResponse(response: BleSlaveResponse): Boolean {
        log.i("[sendResponse] status:${connectStatus} response:$response")
        if (!connectStatus.connected) return false
        workQueue.put(response)
        return true
    }

    private fun sendResponseInternal(response: BleSlaveResponse): Boolean {
        return sendResponseAndroid(
            response.requestId,
            if (response.success) BluetoothGatt.GATT_SUCCESS else BluetoothGatt.GATT_FAILURE,
            response.offset,
            response.value
        )
    }

    override fun notifyChanged(request: BleSlaveRequest): Boolean {
        log.i("[notifyChanged] status${connectStatus} request:$request")
        if (!connectStatus.connected) return false
        workQueue.put(request)
        return true
    }

    private fun tryGetCharacteristic(characteristic: XBleCharacteristics): BluetoothGattCharacteristic? {
        val server = bluetoothGattServer ?: return null
        server.services?.forEach {
            if (it.uuid.toString() == uuidFromShort(characteristic.serviceUUID)) {
                val characteristicsList = it.characteristics
                if (characteristicsList != null && characteristicsList.isNotEmpty()) {
                    characteristicsList.forEach { ch ->
                        if (ch.uuid.toString() == uuidFromShort(characteristic.uuid)) {
                            return ch
                        }
                    }
                }
            }
        }
        return null
    }

    private fun notifyChangedInternal(request: BleSlaveRequest): Boolean {
        val characteristic =
            tryGetCharacteristic(request.notifyCharacteristic)
                ?: throw XBleException("[BLE]XBleCharacteristics不存在： ${request.notifyCharacteristic}")
        return notifyChangedAndroid(
            notifyCharacteristic = characteristic,
            value = request.value,
            confirm = request.confirm
        )
    }


    @SuppressLint("MissingPermission")
    private fun sendResponseAndroid(
        requestId: Int,
        status: Int,
        offset: Int,
        value: ByteArray?,
    ): Boolean {
        return bluetoothGattServer?.let { server ->
            val (connectStatus, device) = _connectStatus
            if (connectStatus is ConnectStatus.Disconnected) return false
            if (device == null) return false
            return server.sendResponse(device, requestId, status, offset, value)
        } ?: false
    }

    @SuppressLint("MissingPermission")
    private fun notifyChangedAndroid(
        notifyCharacteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        confirm: Boolean = false,
    ): Boolean {
        val server = bluetoothGattServer ?: return false
        val (connectStatus, device) = _connectStatus
        if (connectStatus is ConnectStatus.Disconnected) return false
        if (device == null) return false
        notifyCharacteristic.setValue(value)
        return server.notifyCharacteristicChanged(
            device,
            notifyCharacteristic,
            confirm
        )
    }


    @SuppressLint("MissingPermission")
    override fun disconnect() {
        log.i("[disconnect]")
        try {
            val (_, device) = _connectStatus
            onConnectedChanged(ConnectStatus.Disconnected, null)
            val devicesGattServer =
                bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER)
            val devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
            log.d("[disconnect]devicesGattServer:${devicesGattServer.joinToString() { it.address }}")
            log.d("[disconnect]devices:${devices.joinToString { it.address }}")
//            masterDevices.forEach {
//                d("-->[disconnect]cancelConnection ${it.address}/${it.name}")
//                bluetoothGattServer?.cancelConnection(it)
//            }
            if (device != null) {
                bluetoothGattServer?.cancelConnection(device)
            }
            bluetoothGattServer?.clearServices()
            bluetoothGattServer?.close()
            bluetoothGattServer = null
            log.d("[disconnect]over")
            log.d("[disconnect]devicesGattServer: ${devicesGattServer.joinToString() { it.address }}")
            log.d("[disconnect]devices:${devices.joinToString { it.address }}")
        } catch (e: Exception) {
            log.e("[disconnect] error ${e.message}")
        }
    }


    override fun clear() {
        log.i("[clear]")
        stopBroadcast()
        disconnect()
        this.serverCallback = null
        this.statusCallback = null
        recordCallback = null

        try {
            pool.shutdownNow()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createUUID(short: String): UUID = UUID.fromString(uuidFromShort(short))

    private fun createService(broadcastService: XBleService): BluetoothGattService {
        log.i("[createService]初始化Service[${broadcastService.uuid}]")
        val serviceUUID = createUUID(broadcastService.uuid)
        val bluetoothGattService =
            BluetoothGattService(serviceUUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val characteristicList = broadcastService.characteristics
        characteristicList.forEach { characteristic ->
            log.d("--初始化Characteristic[${characteristic.uuid}] ${characteristic.properties} ${characteristic.permissions}")
            val characteristicUUID = createUUID(characteristic.uuid)
            val permissions = characteristic.permissions
            var permissionValue = 0x00
            permissions.forEach { permission ->
                log.d("----解析Permission$permission")
                permissionValue = permissionValue or permission.toPermission()
            }

            val properties = characteristic.properties

            var propertyValue = 0x00
            properties.forEach { property ->
                log.d("----解析Property$property")
                propertyValue = propertyValue or property.toProperty()
            }
            val characteristicCreated = BluetoothGattCharacteristic(
                characteristicUUID, propertyValue, permissionValue
            )
            if (properties.contains(XCharacteristicsProperty.Notify)) {
                val descriptorUUID = createUUID("2902")
                val bluetoothGattDescriptor = BluetoothGattDescriptor(
                    descriptorUUID, BluetoothGattDescriptor.PERMISSION_WRITE
                )
                characteristicCreated.addDescriptor(bluetoothGattDescriptor)
            }
            bluetoothGattService.addCharacteristic(characteristicCreated)
            log.d("--初始化Characteristic[${characteristic.uuid}] 完毕")
        }
        log.d("初始化Service[${broadcastService.uuid}]完毕")
        return bluetoothGattService
    }


}