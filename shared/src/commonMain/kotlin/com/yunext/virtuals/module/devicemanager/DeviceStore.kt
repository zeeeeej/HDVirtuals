package com.yunext.virtuals.module.devicemanager

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.mqtt.HDMqttClient
import com.yunext.kmp.mqtt.core.OnHDMqttActionListener
import com.yunext.kmp.mqtt.createHdMqttClient
import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.data.isConnected
import com.yunext.kmp.mqtt.hdMqttConnect
import com.yunext.kmp.mqtt.hdMqttInit
import com.yunext.kmp.mqtt.hdMqttState
import com.yunext.kmp.mqtt.hdMqttSubscribeTopic
import com.yunext.kmp.mqtt.virtuals.coroutine.hdMqttDisconnectSuspend
import com.yunext.kmp.mqtt.virtuals.coroutine.hdMqttPublishSuspend
import com.yunext.virtuals.data.ProjectInfo
import com.yunext.virtuals.data.device.HDDevice
import com.yunext.virtuals.data.device.MQTTDevice
import com.yunext.virtuals.data.device.generateTopic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 设备信息
 */
interface IDeviceStore {

    val device: HDDevice
    fun connect()
    fun isConnected(): Boolean
    fun disconnect()
    fun publish(topic: String, mqttMessage: HDMqttMessage)

    fun clear()

}

data class DeviceStateHolder(val device: HDDevice, val connect: Boolean = false)

internal fun DeviceStore.wrap() = DeviceStoreWrapper(this)
class DeviceStore(
    private val hdContext: HDContext,
    private val projectInfo: ProjectInfo,
    override val device: HDDevice,
    private val coroutineScope: CoroutineScope,
) : IDeviceStore {
    //private val mqttClient: HDMqttClient = HDMqttClient(hdContext)
    private val mqttClient: HDMqttClient = createHdMqttClient()

    val createTime: Long = currentTime()

    // jobs
    private var connectJob: Job? = null
    private val deviceStateHolderFlowInternal: MutableStateFlow<DeviceStateHolder> =
        MutableStateFlow(DeviceStateHolder(device = device))
    val deviceStateHolderFlow: StateFlow<DeviceStateHolder> =
        deviceStateHolderFlowInternal.asStateFlow()

    override fun connect() {
        if (device !is MQTTDevice) error("暂不支持MQTTDevice意外的设备。${device::class}")
        val mqttParam = device.createMqttParam(projectInfo)
        li("::connect mqttParam $mqttParam")
        connectJob?.cancel()
        coroutineScope.launch(Dispatchers.IO) {
            mqttClient.hdMqttInit()
            mqttClient.hdMqttConnect(
                mqttParam,
                listener = object : OnHDMqttActionListener {
                    override fun onSuccess(token: Any?) {
                        li("::connect MqttResultAction onSuccess")
                    }

                    override fun onFailure(token: Any?, exception: Throwable?) {
                        li("::connect MqttResultAction onFailure")
                    }

                },
                onHDMqttStateChangedListener = { _, s ->
                    li("::connect MqttResultStateChanged $s")
                    deviceStateHolderFlowInternal.update {
                        deviceStateHolderFlow.value.copy(connect = s.isConnected)
                    }
                },
                onHDMqttMessageChangedListener = { _, t, m ->
                    li("::connect MqttResultMessageChanged $t ${m.payload}")
                }

            )
            delay(1000)
            val supportTopics = device.supportTopics()
            val iterator = supportTopics.iterator()
            while (iterator.hasNext()) {
                delay(1000)
                val mqttTopic = iterator.next()
                val realTopic: String = device.generateTopic(projectInfo, mqttTopic)
                mqttClient.hdMqttSubscribeTopic(realTopic, object : OnHDMqttActionListener {
                    override fun onSuccess(token: Any?) {
                        li("::connect ========>  ok")
                    }

                    override fun onFailure(token: Any?, exception: Throwable?) {
                        le("::connect ========> error:$exception")
                    }
                })
            }
        }
    }

    override fun isConnected(): Boolean {
        return mqttClient.hdMqttState.isConnected
    }

    override fun disconnect() {
        li("::disconnect")
        coroutineScope.launch {
            mqttClient.hdMqttDisconnectSuspend()
        }
    }

    override fun publish(topic: String, mqttMessage: HDMqttMessage) {
        li("::publish $mqttMessage")
        coroutineScope.launch {
            mqttClient.hdMqttPublishSuspend(
                topic,
                mqttMessage.payload,
                mqttMessage.qos,
                mqttMessage.retained
            )
        }
    }

    override fun clear() {
        li("::clear")
        coroutineScope.launch {
            //
        }
    }


    companion object {
        private const val TAG = "DeviceStore"
        private const val debug: Boolean = true
        private fun li(msg: String) {
            if (!debug) return
            HDLogger.i(TAG, msg)
        }

        private fun lw(msg: String) {
            if (!debug) return
            HDLogger.w(TAG, msg)
        }

        private fun ld(msg: String) {
            if (!debug) return
            HDLogger.d(TAG, msg)
        }

        private fun le(msg: String) {
            if (!debug) return
            HDLogger.e(TAG, msg)
        }
    }
}

data class DeviceStoreWrapper(
    val deviceStore: DeviceStore,
    private val time: Long = currentTime(),
)
