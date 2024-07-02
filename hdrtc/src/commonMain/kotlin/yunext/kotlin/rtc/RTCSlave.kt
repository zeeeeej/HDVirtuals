package yunext.kotlin.rtc

import com.yunext.kmp.common.logger.HDLogger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.generateBleService
import yunext.kotlin.bluetooth.ble.core.generateXBleCharacteristics
import yunext.kotlin.bluetooth.ble.logger.XBleRecord
import yunext.kotlin.bluetooth.ble.logger.XBleRecordType
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
import yunext.kotlin.bluetooth.ble.slave.HDBleSlave
import yunext.kotlin.bluetooth.ble.slave.SlaveConfiguration
import yunext.kotlin.bluetooth.ble.slave.createBleSlave
import yunext.kotlin.bluetooth.ble.slave.generateBleSlaveRequest
import yunext.kotlin.bluetooth.ble.slave.generateBleSlaveResponse
import yunext.kotlin.bluetooth.ble.util.uuidFromShort
import yunext.kotlin.rtc.procotol.ParameterData
import yunext.kotlin.rtc.procotol.ParameterKey
import yunext.kotlin.rtc.procotol.payload
import yunext.kotlin.rtc.procotol.rtc
import yunext.kotlin.rtc.testcase.AuthenticationNotifyData
import yunext.kotlin.rtc.testcase.ParameterWriteData
import yunext.kotlin.rtc.testcase.rtcCmdDataList

internal interface IRTCSlave {
    val broadcasting: StateFlow<BroadcastStatus>
    val connected: StateFlow<ConnectStatus>
    val address: StateFlow<String>
    val localPropertyMap: StateFlow<Map<ParameterKey, ByteArray>>

    fun setProperty(key: ParameterKey, value: ByteArray)
    fun startBroadcasting()
    fun stopBroadcasting()
    fun disconnect()
    fun clear()
}

class RTCSlave(initAddress: String) : IRTCSlave,
    CoroutineScope by CoroutineScope(Dispatchers.Main + SupervisorJob() + CoroutineName("RTCSlave[$initAddress]")) {

    private var _broadcasting: MutableStateFlow<BroadcastStatus> =
        MutableStateFlow(BroadcastStatus.BroadcastStopped)
    override val broadcasting: StateFlow<BroadcastStatus> = _broadcasting.asStateFlow()

    private var _connected: MutableStateFlow<ConnectStatus> =
        MutableStateFlow(ConnectStatus.Disconnected)
    override val connected: StateFlow<ConnectStatus> = _connected.asStateFlow()

    private val _configuration: MutableStateFlow<SlaveConfiguration> =
        MutableStateFlow(generateConfiguration(initAddress))

    private val _record: MutableStateFlow<List<XBleRecord>> =
        MutableStateFlow(emptyList())

    val record = _record.asStateFlow()

    override val address: StateFlow<String>
        get() = _configuration.map {
            it.address
        }.stateIn(this, SharingStarted.Eagerly, "")
    private val keys = ParameterKey.entries.toList()

    private val _localPropertyMap: MutableStateFlow<Map<ParameterKey, ByteArray>> =
        MutableStateFlow(keys.associateWith {
            byteArrayOf()
        })

    override val localPropertyMap: StateFlow<Map<ParameterKey, ByteArray>> =
        _localPropertyMap.asStateFlow()

    private val serverCallback: BleSlaveCallback = { event ->
        HDLogger.d("[BLE]RTCSlave", "serverCallback : $event")
        when (event) {
            is BleSlaveOnCharacteristicReadRequest -> {}
            is BleSlaveOnCharacteristicWriteRequest -> {}
            is BleSlaveOnConnectionStateChange -> {}
            is BleSlaveOnDescriptorReadRequest -> {

            }

            is BleSlaveOnDescriptorWriteRequest -> {
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
    private val configurationCallback: BleSlaveStatusCallback = {

        when (it) {
            is BleSlaveConfigurationBroadcasting -> {
                HDLogger.d("[BLE]RTCSlave", "configurationCallback : ${it.broadcasting}")
                _broadcasting.value = it.broadcasting
            }

            is BleSlaveConfigurationConnectedDevice -> {
                HDLogger.d("[BLE]RTCSlave", "configurationCallback : ${it.connectedDevice}")
                _connected.value = it.connectedDevice
            }
        }
    }
    private val slave = createBleSlave(_configuration.value).apply {
        this.init(configurationCallback, serverCallback, recordCallback = {
            _record.value = _record.value + it
        })
    }

    override fun startBroadcasting() {
        launch {
            // fix android 设备广播名称不变的问题
//            slave.enable(false)
//            delay(1000)
//            slave.enable(true)
//            delay(500)
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

    fun setProperty(key: String, value: String) {
        val k = keys.singleOrNull() {
            it.name == key
        } ?: return
        val v = if (value.isNotEmpty()) value.encodeToByteArray() else byteArrayOf(0x01, 0x02, 0x03)
        setProperty(k, v)
    }

    override fun setProperty(key: ParameterKey, value: ByteArray) {
        val old = _localPropertyMap.value.toMutableMap()
        old[key] = value
        _localPropertyMap.value = old.toMap()
        var sel: XBleCharacteristics? = null
        _configuration.value.services.forEach { service ->
            service.characteristics.forEach { xBleCharacteristics ->
                if (service.uuid == (AuthenticationNotifyData.serviceUUID)
                    && (xBleCharacteristics.uuid == (AuthenticationNotifyData.characteristicsUUID))
                ) {
                    sel = xBleCharacteristics
                }
            }
        }
        val final = sel ?: return
        val parameterData = ParameterData(key.name.encodeToByteArray(), value)
        val bleSlaveRequest = generateBleSlaveRequest(
            final, parameterData.payload, false
        )
        notifyCharacteristicChanged(bleSlaveRequest)
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

    private fun generateConfiguration(targetAddress: String) =
        SlaveConfiguration(
            address = targetAddress,
            deviceName = generateDeviceNameByAddress(targetAddress),
            services = rtcCmdDataList.map {
                generateBleService(
                    it.serviceUUID,
                    listOf(
                        generateXBleCharacteristics(
                            uuid = it.characteristicsUUID,
                            serviceUUID = it.serviceUUID,
                            properties = it.cmd.characteristicsType.properties.toList(),
                            permissions = listOf(it.cmd.characteristicsType.permission),
                            value = byteArrayOf(),
                            descriptors = emptyList()
                        )
                    )
                )
            }
        )


}