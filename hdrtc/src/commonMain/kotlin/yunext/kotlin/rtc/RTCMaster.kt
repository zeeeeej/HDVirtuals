package yunext.kotlin.rtc

import com.yunext.kmp.common.logger.HDLog
import com.yunext.kmp.common.logger.XLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import yunext.kotlin.bluetooth.ble.core.XBleDevice
import yunext.kotlin.bluetooth.ble.core.XBleDownPayload
import yunext.kotlin.bluetooth.ble.core.XBleException
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.generateBleServiceOnlyByUUID
import yunext.kotlin.bluetooth.ble.core.generateXBleCharacteristicsOnlyByUUID
import yunext.kotlin.bluetooth.ble.logger.XBleRecord
import yunext.kotlin.bluetooth.ble.master.BleMaster
import yunext.kotlin.bluetooth.ble.master.BleMasterConnectedStatus
import yunext.kotlin.bluetooth.ble.master.BleMasterNotifyDataChanged
import yunext.kotlin.bluetooth.ble.master.BleMasterNotifyFail
import yunext.kotlin.bluetooth.ble.master.BleMasterNotifySuccess
import yunext.kotlin.bluetooth.ble.master.BleMasterScanningStatus
import yunext.kotlin.bluetooth.ble.master.BleMasterWriteFail
import yunext.kotlin.bluetooth.ble.master.BleMasterWriteSuccess
import yunext.kotlin.bluetooth.ble.master.XBleMasterNotifyCallback
import yunext.kotlin.bluetooth.ble.master.XBleMasterScanResult
import yunext.kotlin.bluetooth.ble.master.XBleMasterWriteCallback
import yunext.kotlin.bluetooth.ble.master.connected
import yunext.kotlin.bluetooth.ble.master.createBleMaster
import yunext.kotlin.bluetooth.ble.util.uuidFromShort
import yunext.kotlin.rtc.procotol.AbstractRTCCMDDataStore
import yunext.kotlin.rtc.procotol.RTCCmdData
import yunext.kotlin.rtc.procotol.canWrite
import yunext.kotlin.rtc.procotol.rtc
import yunext.kotlin.rtc.procotol.toByteArray
//import yunext.kotlin.rtc.procotol.AuthenticationNotifyData
//import yunext.kotlin.rtc.procotol.AuthenticationWriteData
import yunext.kotlin.rtc.procotol.ParameterData
import yunext.kotlin.rtc.procotol.ParameterPacket
//import yunext.kotlin.rtc.procotol.ParameterWriteData
import yunext.kotlin.rtc.procotol.RTCCmd
import yunext.kotlin.rtc.procotol.ServiceAndCharacteristics
import yunext.kotlin.rtc.procotol.rtcCmdDataList
import yunext.kotlin.rtc.procotol.assci
import yunext.kotlin.rtc.procotol.characteristicsRealUUID
import yunext.kotlin.rtc.procotol.serviceRealUUID
import yunext.kotlin.rtc.util.createScope
import kotlin.coroutines.resume

internal interface IRTCMaster
class RTCMaster : IRTCMaster, CoroutineScope by createScope("RTCMaster"),
    XLog by HDLog("RTCMaster", true), AbstractRTCCMDDataStore() {

    private val bleMaster: BleMaster = createBleMaster()

    private val _authed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _connected: MutableStateFlow<BleMasterConnectedStatus> =
        MutableStateFlow(BleMasterConnectedStatus.Idle)
    private val _scanning: MutableStateFlow<BleMasterScanningStatus> =
        MutableStateFlow(BleMasterScanningStatus.ScanStopped)
    private val _scanningList: MutableStateFlow<List<XBleMasterScanResult>> = MutableStateFlow(
        emptyList()
    )
    private val _record: MutableStateFlow<List<XBleRecord>> = MutableStateFlow(emptyList())
    private val _serviceList: MutableStateFlow<List<XBleService>> = MutableStateFlow(emptyList())
    private val _serviceAndCharacteristics: MutableStateFlow<ServiceAndCharacteristics?> = MutableStateFlow(null)


    val connected = _connected.asStateFlow()
    val scanning = _scanning.asStateFlow()
    val scanningResult = _scanningList.asStateFlow()
    val record = _record.asStateFlow()
    val services = _serviceList.asStateFlow()
    val serviceAndCharacteristics = _serviceAndCharacteristics.asStateFlow()
    private val authed = _authed.asStateFlow()


    init {
        launch {

            connected.collectLatest {
                when (it) {
                    is BleMasterConnectedStatus.Connected -> {
                        //<editor-fold desc="过滤预先定义的">
//                        _serviceList.value = it.services.filter { service ->
//                            rtcCmdDataList.any { cmdData ->
//                                uuidFromShort(cmdData.serviceShortUUID) == service.uuid
//                            }
//                        }
                        //</editor-fold>

                        //<editor-fold desc="所有的">
//                        _serviceList.value = it.services
                        //</editor-fold>

                        //<editor-fold desc="4G双模温控器需要自己去查找输入输出通道。">
                        // 输入 属性： read write write-without-response
                        // 输出 属性： read notify
                        val serviceAndCharacteristics =  rtc {
                            tryGetService(it.services)
                        }
                        _serviceAndCharacteristics.value = serviceAndCharacteristics
                        _serviceList.value = serviceAndCharacteristics?.run {
                            listOf(this.service)
                        }?: emptyList()
                        //</editor-fold>

                    }

                    is BleMasterConnectedStatus.Connecting -> {


                    }

                    is BleMasterConnectedStatus.Disconnected -> {
                        authChannel?.cancel()
                        authChannel = null
                        _authed.value = false
                    }

                    is BleMasterConnectedStatus.Disconnecting -> {}
                    BleMasterConnectedStatus.Idle -> {}
                }
            }
        }
    }

    init {
        bleMaster.init(onConnectedChanged = {
            d("连接 : $it")
            this._connected.value = it
            if (it.connected) {
                clearScan()
            }
        }, onScanningStatusChanged = {
            d("搜索 : $it")
            this._scanning.value = it
        }, {
            // d("日志 : $it")
            this._record.value = record.value + it
        })
    }

    private var authChannel: Channel<Boolean>? = null
    private var enableNotifyChannel: Channel<Boolean>? = null

    @OptIn(ExperimentalStdlibApi::class)
    private val notifyCallback: XBleMasterNotifyCallback = {
        when (it) {
            is BleMasterNotifyDataChanged -> {
                d("收到Server数据了，${it.data} ,hex:${it.data.data.toHexString()}")
                val rtcData = rtc {
                    parseDataFromSlave(it.data.data)
                }
                d("解析rtcData=$rtcData")
                    // 5aa5 b2 00 0100 b2
                when (rtcData.cmd) {
                    RTCCmd.AuthenticationNotify.byte -> {
                        val payload = rtcData.payload
                        val auth = if (payload.size == 1) payload[0].toInt() == 1 else false
                        authChannel?.trySend(auth)
                    }

                    else -> {

                    }
                }


            }

            is BleMasterNotifyFail -> {
                enableNotifyChannel?.trySend(false)
            }
            is BleMasterNotifySuccess -> {
                enableNotifyChannel?.trySend(true)
            }
        }
    }

    fun startScan() {
        d("startScan")
        clearScan()
        bleMaster.startScan {
            val old = _scanningList.value
            _scanningList.value = old.update(it) {
                this.device.toString()
            }.filter {
                rtc {
                    checkBroadcastByName(it.device.deviceName ?: "")
                }
            }
        }
    }

    fun stopScan() {
        bleMaster.stopScan()
    }

    fun connect(device: XBleDevice) {
        bleMaster.connect(device) {

        }
    }

    fun enableNotify() {
        enableNotifyInternal()
    }

    suspend fun enableNotifyV2(serviceUUID:String,characteristicsUUID:String):Boolean{
        enableNotifyChannel = Channel(1)
        bleMaster.enableNotify(
            service = generateBleServiceOnlyByUUID(serviceUUID),
            characteristic = generateXBleCharacteristicsOnlyByUUID(characteristicsUUID),
            notifyCallback
        )
        val receive = enableNotifyChannel?.receive()
        enableNotifyChannel=null
        return receive?:false
    }

//    suspend fun setProperty(key: String, value: String, auth: Boolean = false): Boolean {
//        d("[setProperty]key:$key value:$value auth:$auth")
//        if (!connected.value.connected) throw XBleException("设备未连接")
//        val (k, v) = createParameter(key, value)
//        val old = _localPropertyMap.value.toMutableMap()
//        old[k] = v
//        _localPropertyMap.value = old.toMap()
//        val parameterData = ParameterData(k.assci, v)
//        val parameterPacket = ParameterPacket(parameterData)
//        val data = rtc {
//            rtcDataForParameterWrite(
//                listOf(
//                    parameterPacket
//                )
//            )
//        }
//        val result = writeSuspend(ParameterWriteData, data.toByteArray())
//        d("[setProperty]key:$key value:$value auth:$auth result=${result}")
//        return when (result) {
//            is BleMasterWriteFail -> false
//            is BleMasterWriteSuccess -> true
//        }
//    }
    suspend fun setPropertyV2(serviceUUID: String,characteristicUUID: String,key: String, value: String, auth: Boolean = false): Boolean {
        d("[setProperty]key:$key value:$value auth:$auth")
        if (!connected.value.connected) throw XBleException("设备未连接")
        val (k, v) = createParameter(key, value)
        val old = _localPropertyMap.value.toMutableMap()
        old[k] = v
        _localPropertyMap.value = old.toMap()
        val parameterData = ParameterData(k.assci, v)
        val parameterPacket = ParameterPacket(parameterData)
        val data = rtc {
            rtcDataForParameterWrite(
                listOf(
                    parameterPacket
                )
            )
        }
        val rtcCMDData = RTCCmdData(
            cmd = RTCCmd.ParameterWrite,
            serviceUUID = serviceUUID,
            characteristicsUUID =  characteristicUUID
        )
        val result = writeSuspend(rtcCMDData, data.toByteArray())
        d("[setPropertyV2]key:$key value:$value auth:$auth result=${result}")
        return when (result) {
            is BleMasterWriteFail -> false
            is BleMasterWriteSuccess -> true
        }
    }

    suspend fun setTimeStamp(serviceUUID: String,characteristicUUID: String,timestamp:Long): Boolean {
        d("[setTimeStamp]serviceUUID:$serviceUUID ,characteristicUUID:${characteristicUUID} ,timestamp:$timestamp")
        if (!connected.value.connected) throw XBleException("设备未连接")
        val data = rtc {
            rtcDataForTimestampWrite(
                timestamp
            )
        }
        val rtcCMDData = RTCCmdData(
            cmd = RTCCmd.ParameterWrite,
            serviceUUID = serviceUUID,
            characteristicsUUID =  characteristicUUID
        )
        val result = writeSuspend(rtcCMDData, data.toByteArray())
        d("[setTimeStamp]serviceUUID:$serviceUUID ,characteristicUUID:${characteristicUUID} ,timestamp:$timestamp ,result=$result")
        return when (result) {
            is BleMasterWriteFail -> false
            is BleMasterWriteSuccess -> true
        }
    }

//    suspend fun write(value: ByteArray) = writeSuspend(AuthenticationWriteData, value)

//    suspend fun auth(accessKey: String): Boolean {
//        val device = tryGetDevice() ?: throw XBleException("未连接")
//        val deviceName = device.deviceName ?: ""
//        val data = rtc {
//            val broadcastAddress = parseMacFromBroadcast(deviceName.encodeToByteArray())
//                ?: throw XBleException("broadcastAddress 错误")
//            d("[auth]发起鉴权 accessKey:$accessKey,broadcastAddress:${broadcastAddress}")
//            rtcDataForAuthenticationWrite(accessKey, broadcastAddress)
//        }
//
//        authChannel = Channel(1)
//        val result = writeSuspend(AuthenticationWriteData, data.toByteArray())
//        d("[auth]accessKey:$accessKey result:$result")
//        return when (result) {
//            is BleMasterWriteFail -> false
//            is BleMasterWriteSuccess -> {
//                val receive: Boolean? = authChannel?.receive()
//                val authResult = receive == true
//                authChannel = null
//                _authed.value = authResult
//                d("[auth]accessKey:$accessKey authResult:$authResult ")
//                return authResult
//            }
//        }
//    }

    suspend fun auth(accessKey: String,serviceUUID:String,characteristicUUID:String): Boolean {
        d("[auth] accessKey:$accessKey,serviceUUID:${serviceUUID} characteristicUUID:${characteristicUUID}")
        val device = tryGetDevice() ?: throw XBleException("未连接")
        val deviceName = device.deviceName ?: ""
        val data = rtc {
            val broadcastAddress = parseMacFromBroadcast(deviceName.encodeToByteArray())
                ?: throw XBleException("broadcastAddress 错误")
            d("[auth]发起鉴权 accessKey:$accessKey,broadcastAddress:${broadcastAddress}")
            rtcDataForAuthenticationWrite(accessKey, broadcastAddress)
        }
        d("[auth]等待消息")
        authChannel = Channel(1)
        val result = writeSuspend(RTCCmdData(
            RTCCmd.AuthenticationWrite,serviceUUID=serviceUUID,characteristicsUUID=characteristicUUID
        ), data.toByteArray())
        d("[auth]accessKey:$accessKey result:$result")
        return when (result) {
            is BleMasterWriteFail -> false
            is BleMasterWriteSuccess -> {
                val receive: Boolean? = authChannel?.receive()
                val authResult = receive == true
                authChannel = null
                _authed.value = authResult
                d("[auth]accessKey:$accessKey authResult:$authResult ")
                return authResult
            }
        }
    }

//    fun read() {
//        val authCmd = AuthenticationNotifyData
//        val service = generateBleServiceOnlyByUUID((authCmd.serviceRealUUID))
//        val characteristic =
//            generateXBleCharacteristicsOnlyByUUID((authCmd.characteristicsRealUUID))
//        bleMaster.read(
//            service = service,
//            characteristic = characteristic,
//        ) {
//
//        }
//    }

    fun disconnect() {
        bleMaster.disconnect()
    }

    fun clear() {
        bleMaster.clear()
    }

    private fun enableNotifyInternal() {

        bleMaster.enableNotify(
            service = generateBleServiceOnlyByUUID(uuidFromShort("a001")),
            characteristic = generateXBleCharacteristicsOnlyByUUID(uuidFromShort("b002")),
            notifyCallback
        )
    }

    private fun writeInternal(
        serviceUUID: String,
        characteristicUUID: String,
        value: ByteArray,
        callback: XBleMasterWriteCallback,
    ){
        val service = generateBleServiceOnlyByUUID(serviceUUID)
        val characteristic =
            generateXBleCharacteristicsOnlyByUUID(characteristicUUID)
        bleMaster.write(
            service = service,
            characteristic = characteristic,
            data = XBleDownPayload(value),
            callback = callback
        )
    }
    private fun writeInternal(
        rtcData: RTCCmdData,
        value: ByteArray,
        callback: XBleMasterWriteCallback,
    ) {
        val service = generateBleServiceOnlyByUUID((rtcData.serviceRealUUID))
        val characteristic =
            generateXBleCharacteristicsOnlyByUUID((rtcData.characteristicsRealUUID))
        if (!rtcData.cmd.characteristicsType.canWrite) throw XBleException("$characteristic 没有write权限")
        bleMaster.write(
            service = service,
            characteristic = characteristic,
            data = XBleDownPayload(value),
            callback = callback
        )

    }

    private suspend fun writeSuspend(rtcData: RTCCmdData, value: ByteArray) =
        suspendCancellableCoroutine { con ->
            writeInternal(rtcData, value) {
                when (it) {
                    is BleMasterWriteFail -> con.resume(it)
                    is BleMasterWriteSuccess -> con.resume(it)
                }
            }

            con.invokeOnCancellation {
                // ignore
            }
        }

    private fun tryGetDevice() = when (val s = connected.value) {
        is BleMasterConnectedStatus.Connected -> s.device
        is BleMasterConnectedStatus.Connecting -> s.device
        is BleMasterConnectedStatus.Disconnected -> s.device
        is BleMasterConnectedStatus.Disconnecting -> s.device
        BleMasterConnectedStatus.Idle -> null
    }

    private fun <T> List<T>.update(t: T, key: T.() -> String): List<T> {
        return this - this.filter {
            it.key() == t.key()
        }.toSet() + t
    }

    private fun clearScan() {
        _scanningList.value = emptyList()
    }
}
