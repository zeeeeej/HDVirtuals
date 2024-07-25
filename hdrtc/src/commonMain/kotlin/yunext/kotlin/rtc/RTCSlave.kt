package yunext.kotlin.rtc

import com.yunext.kmp.common.logger.HDLog
import com.yunext.kmp.common.util.currentTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.generateBleService
import yunext.kotlin.bluetooth.ble.core.generateBleServiceOnlyByUUID
import yunext.kotlin.bluetooth.ble.core.generateXBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.generateXBleCharacteristicsOnlyByUUID
import yunext.kotlin.bluetooth.ble.logger.XBleRecord
import yunext.kotlin.bluetooth.ble.slave.BleSlaveCallback
import yunext.kotlin.bluetooth.ble.slave.BleSlaveConfigurationBroadcasting
import yunext.kotlin.bluetooth.ble.slave.BleSlaveStatusCallback
import yunext.kotlin.bluetooth.ble.slave.BleSlaveConfigurationConnectedDevice
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnCharacteristicReadRequest
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnCharacteristicWriteRequest
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnConnectionStateChange
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnDescriptorReadRequest
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnDescriptorWriteRequest
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnExecuteWrite
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnMtuChanged
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnNotificationSent
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnPhyRead
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnPhyUpdate
import yunext.kotlin.bluetooth.ble.slave.BleSlaveOnServiceAdded
import yunext.kotlin.bluetooth.ble.slave.BleSlaveRequest
import yunext.kotlin.bluetooth.ble.slave.BleSlaveResponse
import yunext.kotlin.bluetooth.ble.slave.BroadcastStatus
import yunext.kotlin.bluetooth.ble.slave.ConnectStatus
import yunext.kotlin.bluetooth.ble.slave.SlaveConfiguration
import yunext.kotlin.bluetooth.ble.slave.createBleSlave
import yunext.kotlin.bluetooth.ble.slave.generateBleSlaveRequest
import yunext.kotlin.bluetooth.ble.slave.generateBleSlaveResponse
import yunext.kotlin.bluetooth.ble.util.uuidFromShort
import yunext.kotlin.rtc.procotol.AbstractRTCCMDDataStore
import yunext.kotlin.rtc.procotol.ParameterData
import yunext.kotlin.rtc.procotol.ParameterKey
import yunext.kotlin.rtc.procotol.RTCCmd
import yunext.kotlin.rtc.procotol.payload
import yunext.kotlin.rtc.procotol.rtc
import yunext.kotlin.rtc.procotol.toByteArray
import yunext.kotlin.rtc.procotol.parameterKeyOrNull
import yunext.kotlin.rtc.procotol.rtcCmdDataList
import kotlin.random.Random

internal interface IRTCSlave {
    val broadcasting: StateFlow<BroadcastStatus>
    val connected: StateFlow<ConnectStatus>
    val address: StateFlow<String>
    val accessKey: StateFlow<String>
    val localPropertyMap: StateFlow<Map<ParameterKey, ByteArray>>

    //fun setProperty(key: ParameterKey, value: ByteArray)
    fun setProperty(key: String, value: String)
    fun startBroadcasting()
    fun stopBroadcasting()
    fun disconnect()
    fun clear()
}

class RTCSlave(broadcastAddress: String, accessKey: String) : IRTCSlave, AbstractRTCCMDDataStore(),
    CoroutineScope by CoroutineScope(Dispatchers.Main + SupervisorJob() + CoroutineName("RTCSlave[$broadcastAddress]")) {

    private var _accessKey: MutableStateFlow<String> = MutableStateFlow(accessKey)

    private var _broadcasting: MutableStateFlow<BroadcastStatus> =
        MutableStateFlow(BroadcastStatus.BroadcastStopped)

    private var _currentServiceList: MutableStateFlow<List<XBleService>> =
        MutableStateFlow(emptyList())

    override val broadcasting: StateFlow<BroadcastStatus> = _broadcasting.asStateFlow()

    private var _connected: MutableStateFlow<ConnectStatus> =
        MutableStateFlow(ConnectStatus.Disconnected)
    override val connected: StateFlow<ConnectStatus> = _connected.asStateFlow()

    private val _configuration: MutableStateFlow<SlaveConfiguration> =
        MutableStateFlow(generateConfiguration(broadcastAddress))

    private val _record: MutableStateFlow<List<XBleRecord>> =
        MutableStateFlow(emptyList())

    val record = _record.asStateFlow()
    private val currentServiceList = _currentServiceList.asStateFlow()

    override val address: StateFlow<String> = _configuration.mapLatest {
        it.broadcastAddress
    }.stateIn(this, SharingStarted.Eagerly, "")

//    private val keys = ParameterKey.entries.toList()
//
//    private val _localPropertyMap: MutableStateFlow<Map<ParameterKey, ByteArray>> =
//        MutableStateFlow(keys.associateWith {
//            byteArrayOf()
//        })
//
//    override val localPropertyMap: StateFlow<Map<ParameterKey, ByteArray>> =
//        _localPropertyMap.asStateFlow()


    override val accessKey = _accessKey.asStateFlow()
    private var loggerIndex: Long = 0

    private val log = HDLog("[BLE][RTCSlave]", true) {
        loggerIndex++
        if (loggerIndex > 1000) {
            loggerIndex = 0
        }
        val newLog = XBleRecord(
            index = loggerIndex,
            tag = "RTCSlave",
            msg = this,
            type = it,
            timestamp = currentTime()
        )
        _record.value = _record.value + newLog
        this
    }

    // GattServer
    // private var serviceCallbackChannel: Channel<>? = null

    private val serverCallback: BleSlaveCallback = { event ->
        log.d("serverCallback : $event")
        when (event) {
            is BleSlaveOnCharacteristicReadRequest -> {

                // 见 ReadCharacteristic
                log.d("读数据")
                val generateBleSlaveResponse =
                    generateBleSlaveResponse(
                        event.requestId,
                        event.offset,
                        Random.nextBytes(4),
                        true
                    )
                log.d("读数据 sendResponse 前")
                sendResponse(generateBleSlaveResponse)
                log.d("读数据 sendResponse 后")
//
//                val service =
//                    generateBleServiceOnlyByUUID(uuidFromShort(AuthenticationNotifyData.serviceShortUUID))
//                val ch =
//                    generateXBleCharacteristicsOnlyByUUID(uuidFromShort(AuthenticationNotifyData.characteristicsShortUUID))
//                val request = generateBleSlaveRequest(service, ch, Random.nextBytes(4), false)
//                log.d("读数据 notifyCharacteristicChanged 前")
//                notifyCharacteristicChanged(request)
//                log.d("读数据 notifyCharacteristicChanged 后")
            }

            is BleSlaveOnCharacteristicWriteRequest -> {
                // 处理client Characteristic write
                onClientDataChanged(event)
            }

            is BleSlaveOnConnectionStateChange -> {}
            is BleSlaveOnDescriptorReadRequest -> {

            }

            is BleSlaveOnDescriptorWriteRequest -> {
                // 处理client enable notify Characteristic
                val generateBleSlaveResponse =
                    generateBleSlaveResponse(event.requestId, event.offset, event.value, true)
                sendResponse(generateBleSlaveResponse)
            }

            is BleSlaveOnExecuteWrite -> {}
            is BleSlaveOnMtuChanged -> {}
            is BleSlaveOnNotificationSent -> {}
            is BleSlaveOnPhyRead -> {}
            is BleSlaveOnPhyUpdate -> {}
            is BleSlaveOnServiceAdded -> {}
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun onClientDataChanged(event: BleSlaveOnCharacteristicWriteRequest) {
        val value = event.value ?: return
        log.i("[onClientDataChanged] value:${value.toHexString()}")
        try {
            val rtcData = rtc { parseDataFromSlave(value) }
            when (rtcData.cmd) {
                RTCCmd.AuthenticationWrite.byte -> {
                    log.i("[onClientDataChanged]收到鉴权请求,开始鉴权！")
                    val generateBleSlaveResponse =
                        generateBleSlaveResponse(event.requestId, event.offset, event.value, true)
                    sendResponse(generateBleSlaveResponse)
                    doCheckAuth(rtcData.payload)
                }

                RTCCmd.ParameterWrite.byte -> {
                    log.i("[onClientDataChanged]收到参数设置请求,开始设置参数！")
                    // 检查
                    val success = doCheckParameterWrite(rtcData.payload)
                    val generateBleSlaveResponse =
                        generateBleSlaveResponse(
                            event.requestId,
                            event.offset,
                            event.value,
                            success
                        )
                    sendResponse(generateBleSlaveResponse)

                }

                RTCCmd.TimestampWrite.byte -> {}
            }
        } catch (e: Exception) {
            log.w("[onClientDataChanged] 非业务数据 ${e.message}")
        }

    }

    private fun doCheckParameterWrite(data: ByteArray): Boolean {
        try {
            val list = rtc {
                parseParameterPacketListFromPayload(data)
            }
            val checkList = list.mapNotNull {
                val parameterKey = parameterKeyOrNull(it.data.key)
                if (parameterKey != null) {
                    parameterKey to it.data.value
                } else null
            }
            if (checkList.isEmpty()) {
                return false
            }
            checkList.forEach { (k, v) ->
                setPropertyInternal(k, v, false)
            }
            return true
        } catch (e: Exception) {
            log.d("[doCheckParameterWrite]error:${e.message}")
        }
        return false

    }

    private val configurationCallback: BleSlaveStatusCallback = {

        when (it) {
            is BleSlaveConfigurationBroadcasting -> {
                _broadcasting.value = it.broadcasting
                _currentServiceList.value = it.services
            }

            is BleSlaveConfigurationConnectedDevice -> {
                _connected.value = it.connectedDevice
                _currentServiceList.value = it.services
                startAuthTimeout()
            }
        }
    }

    private var startAuthTimeoutJob: Job? = null
    private fun startAuthTimeout() {
        startAuthTimeoutJob?.cancel()
        startAuthTimeoutJob = null
        startAuthTimeoutJob = launch {
            delay(30 * 1000L)
            startBroadcasting()
        }
    }

    private fun stopAuthTimeout() {
        startAuthTimeoutJob?.cancel()
        startAuthTimeoutJob = null
    }

    private val slave = createBleSlave(_configuration.value).apply {
        this.init(configurationCallback, serverCallback, recordCallback = {
            _record.value = _record.value + it
        })
    }

    private var authJob: Job? = null

    @OptIn(ExperimentalStdlibApi::class)
    private fun doCheckAuth(authed: ByteArray) {
        log.d("[doCheckAuth] ---------------------")
        authJob?.cancel()
        authJob = null
        log.d("[doCheckAuth] authed:${authed.toHexString()}")
        authJob = launch {
            val timeoutJob: Job = launch {
                launch {
                    while (isActive) {
                        delay(1000)
                        log.w("=====timeout=====")
                    }
                }
                delay(30 * 1000L)
                log.e("鉴权超时，断开连接。")
                stopBroadcasting()
                startBroadcasting()
                log.d("[doCheckAuth] ---------------------z")
                throw CancellationException("鉴权超时，断开连接。")
            }.also {
                it.invokeOnCompletion {
                    log.i("鉴权超时取消${it.hashCode()}")
                }
            }

            launch {
                val key = accessKey.value
                val curAddress = address.value
                val curAddress2 = _configuration.value.broadcastAddress
                log.d("鉴权中...key:$key address:$curAddress ,curAddress2=$curAddress2")

                val success = rtc {
                    rtcDataForAuthenticationNotify(authed, accessKey = key, mac = curAddress)
                }
                delay(5000)
                log.d("鉴权完毕 success=$success")
                if (success) {
                    log.d("鉴权完毕 成功了 取消超时任务${timeoutJob.hashCode()}")
                    timeoutJob.cancel()
                    stopAuthTimeout()
                }
                val rtcData = rtc {
                    rtcDataForAuthenticationNotify(success)
                }

                // TODO 鉴权过程
                // 找出notify
                val serviceAndCharacteristics = rtc {
                    tryGetService(currentServiceList.value)
                }
                if (serviceAndCharacteristics!=null){
                    val serviceUUID = serviceAndCharacteristics.service.uuid
                    val characteristicsUUID = serviceAndCharacteristics.notify.uuid
                    val request = generateBleSlaveRequest(
                        service = generateBleServiceOnlyByUUID(serviceUUID),
                        characteristics = generateXBleCharacteristicsOnlyByUUID(characteristicsUUID),
                        value = rtcData.toByteArray(), confirm = false
                    )



//                val request = generateBleSlaveRequest(
//                    service = generateBleServiceOnlyByUUID(uuidFromShort("1234"/*AuthenticationNotifyData.serviceShortUUID)*/)),
//                    characteristics = generateXBleCharacteristicsOnlyByUUID(
//                        uuidFromShort(
//                            "1234"
////                            AuthenticationNotifyData.characteristicsShortUUID
//                        )
//                    ),
//                    value = rtcData.toByteArray(), confirm = false
//                )
                //5aa5 b2 00 0100 b2
                log.d("鉴权完毕，回复：${rtcData.toByteArray().toHexString()}")
                notifyCharacteristicChanged(request)
                }else{
                    log.e("serviceAndCharacteristics 为空哈！")
                }
                log.d("[doCheckAuth] ---------------------z")
            }
        }

    }

    override fun startBroadcasting() {
        launch {
            // fix android 设备广播名称不变的问题
//            slave.enable(false)
//            delay(1000)
//            slave.enable(true)
//            delay(500)
            stopAuthTimeout()
            slave.stopBroadcast()
            delay(500)
            slave.startBroadcast()
        }

    }

    override fun stopBroadcasting() {
        slave.stopBroadcast()
    }

    override fun disconnect() {
        slave.disconnect()
    }

    override fun clear() {
        slave.clear()
        this.cancel()
    }

    override fun setProperty(key: String, value: String) {
        val (k, v) = createParameter(key, value)
        setPropertyInternal(k, v, true)
    }

    private fun setPropertyInternal(key: ParameterKey, value: ByteArray, notify: Boolean) {
        val old = _localPropertyMap.value.toMutableMap()
        old[key] = value
        _localPropertyMap.value = old.toMap()

        if (notify) {
            var sel: Pair<XBleService, XBleCharacteristics>? = null
            _configuration.value.services.forEach { service ->
                service.characteristics.forEach { xBleCharacteristics ->
                    // TODO 1234
                    if (service.uuid == uuidFromShort("1234"/*AuthenticationNotifyData.serviceShortUUID*/)
                        && (xBleCharacteristics.uuid == uuidFromShort("1234"/*AuthenticationNotifyData.characteristicsShortUUID*/))
                    ) {
                        sel = service to xBleCharacteristics
                    }
                }
            }
            val (s, c) = sel ?: return
            val parameterData = ParameterData(key.name.encodeToByteArray(), value)
            val bleSlaveRequest = generateBleSlaveRequest(
                s,
                c, parameterData.payload, false
            )
            notifyCharacteristicChanged(bleSlaveRequest)
        }

    }


    private fun notifyCharacteristicChanged(request: BleSlaveRequest) {
        slave.notifyChanged(request)
    }

    private fun sendResponse(response: BleSlaveResponse) {
        slave.sendResponse(response)
    }

    private fun <R> rtcBlock(block: () -> R): R {
        return block()
    }

    private fun generateDeviceNameByAddress(editAddress: String) = rtcBlock {
        rtc {
            createBroadcastContent(editAddress)
        }
    }

    fun changeAccessKey(key: String) {
        this._accessKey.value = key
    }

    private fun generateConfiguration(targetAddress: String) =
        SlaveConfiguration(
            broadcastAddress = targetAddress,
            deviceName = generateDeviceNameByAddress(targetAddress),
            services = rtcCmdDataList.map {
                generateBleService(
                    uuidFromShort(it.serviceShortUUID),
                    listOf(
                        generateXBleCharacteristics(
                            uuid = uuidFromShort(it.characteristicsShortUUID),
                            serviceUUID = uuidFromShort(it.serviceShortUUID),
                            properties = it.cmd.characteristicsType.properties.toList(),
                            permissions = (it.cmd.characteristicsType.permissions).toList(),
                            value = byteArrayOf(),
                            descriptors = emptyList()
                        )
                    )
                )
            }
        )


}