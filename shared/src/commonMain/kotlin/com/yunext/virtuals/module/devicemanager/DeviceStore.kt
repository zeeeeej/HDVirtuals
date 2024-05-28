package com.yunext.virtuals.module.devicemanager

import androidx.compose.runtime.Stable
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.http.core.HDResult
import com.yunext.kmp.http.core.display
import com.yunext.kmp.mqtt.HDMqttClient
import com.yunext.kmp.mqtt.core.OnHDMqttActionListener
import com.yunext.kmp.mqtt.core.OnHDMqttMessageChangedListener
import com.yunext.kmp.mqtt.createHdMqttClient
import com.yunext.kmp.mqtt.data.HDMqttParam
import com.yunext.kmp.mqtt.data.isConnected
import com.yunext.kmp.mqtt.hdClientId
import com.yunext.kmp.mqtt.hdMqttConnect
import com.yunext.kmp.mqtt.hdMqttInit
import com.yunext.kmp.mqtt.hdMqttState
import com.yunext.kmp.mqtt.hdMqttSubscribeTopic
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.event.EventKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.event.tslHandleTsl2EventKeys
import com.yunext.kmp.mqtt.virtuals.coroutine.hdMqttDisconnectSuspend
import com.yunext.kmp.mqtt.virtuals.coroutine.hdMqttPublishSuspend
import com.yunext.kmp.mqtt.virtuals.protocol.ProtocolMQTTMessage
import com.yunext.kmp.mqtt.virtuals.protocol.ReplyServiceMQTTMessage
import com.yunext.kmp.mqtt.virtuals.protocol.ReportMQTTMessage
import com.yunext.kmp.mqtt.virtuals.protocol.payload
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.Tsl
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.TslValueParser
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.display
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.DatePropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.IntPropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.toDefaultValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleToJsonValues
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleTsl2PropertyValues
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.tslHandleUpdatePropertyValues
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.service.ServiceKey
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.service.tslHandleTsl2ServiceKeys
import com.yunext.virtuals.data.ProjectInfo
import com.yunext.virtuals.data.UpLog
import com.yunext.virtuals.data.device.HDDevice
import com.yunext.virtuals.data.device.MQTTDevice
import com.yunext.virtuals.data.device.TwinsDevice
import com.yunext.virtuals.data.device.UnSupportDeviceException
import com.yunext.virtuals.data.device.generateTopic
import com.yunext.virtuals.data.device.providerMqttConvertor
import com.yunext.virtuals.module.repository.LogRepository
import com.yunext.virtuals.module.repository.TslRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * 设备信息
 */
internal interface IDeviceStore {

    val device: HDDevice
    fun init()
    fun connect()
    fun isConnected(): Boolean
    fun disconnect()
    fun publish(message: ProtocolMQTTMessage)

    fun clear()

}

data class DeviceStateHolder(
    val device: HDDevice,
    val tsl: Tsl? = null,
    val connect: Boolean = false,
    val properties: Map<String, PropertyValue<*>>,
    val events: Map<String, EventKey>,
    val services: Map<String, ServiceKey>,
    val serviceHandlers: List<ServiceHandler>,
)

data class ServiceHandler(
    val serviceKey: ServiceKey,
    val input: List<PropertyValue<*>> = emptyList(),
    val output: List<PropertyValue<*>> = emptyList(),
    val auto: Boolean = false,
)

internal fun DeviceStore.wrap() = DeviceStoreWrapper(this)
class DeviceStore(
    private val hdContext: HDContext,
    private val projectInfo: ProjectInfo,
    override val device: HDDevice,
    private val coroutineScope: CoroutineScope,
) : IDeviceStore {

    private val mqttClient: HDMqttClient = createHdMqttClient()
    private val tslRepository: TslRepository = TslRepository
    private val logRepository: LogRepository = LogRepository
    private val tslParser: TslValueParser = TslValueParser

    val createTime: Long = currentTime()

    fun tryGetTsl(): Tsl? {
        return deviceStateHolderFlow.value.tsl
    }

    /* ********* flow a *********/
    private val deviceStateHolderFlowInternal: MutableStateFlow<DeviceStateHolder> =
        MutableStateFlow(
            DeviceStateHolder(
                device = device,
                properties = emptyMap(),
                events = emptyMap(),
                services = emptyMap(),
                serviceHandlers = emptyList()
            )
        )
    val deviceStateHolderFlow: StateFlow<DeviceStateHolder> =
        deviceStateHolderFlowInternal.asStateFlow()

    /**
     * 属性property
     */
//    private val _propertiesFlow: MutableStateFlow<Map<String, PropertyValue<*>>> =
//        MutableStateFlow(mapOf())
//
//    val propertiesFlow: StateFlow<Map<String, PropertyValue<*>>> =
//        _propertiesFlow.asStateFlow()
//
//    /**
//     * 时间event
//     */
//    private val _eventFlow: MutableStateFlow<Map<String, EventKey>> =
//        MutableStateFlow(mapOf())
//
//    val eventFlow: StateFlow<Map<String, EventKey>> =
//        _eventFlow.asStateFlow()
//
//    /**
//     * 服务Service
//     */
//    private val _serviceFLow: MutableStateFlow<Map<String, ServiceKey>> =
//        MutableStateFlow(mapOf())
//
//    val serviceFlow: StateFlow<Map<String, ServiceKey>> =
//        _serviceFLow.asStateFlow()
    /* ********* flow z *********/

    /* ********* jobs a *********/
    private var connectJob: Job? = null
    private var refreshTslJob: Job? = null
    private var registerTopicsJob: Job? = null
    private var initWatcherJob: Job? = null
    private var iniDeviceInfoMakerJob: Job? = null
    /* ********* jobs z *********/

    init {
        init()
    }

    override fun init() {
//        if (device !is MQTTDevice) throw UnSupportDeviceException(device::class)
//        val mqttParam = device.createMqttParam(projectInfo)
//        li("::init mqttParam $mqttParam")
//        refreshTsl(mqttParam) {
//            connect()
//        }
//        initWatcher()
        initReporter()
        iniDeviceInfoMaker()
    }

    private fun refreshTsl(mqttParam: HDMqttParam, onSuccess: () -> Unit) {
        refreshTslJob?.cancel()
        refreshTslJob = coroutineScope.launch(Dispatchers.IO) {
            val tslResult = tslRepository.load(mqttParam.clientId)
            ld("tsl : ${tslResult.display { it.display }}")
            val tsl = when (tslResult) {
                is HDResult.Fail -> null
                is HDResult.Success -> tslResult.data
            }

            coroutineScope.launch(Dispatchers.Main) {
                onChanged("init") {
                    it.copy(
                        tsl = tsl,
                        properties = defaultProperty(tsl),
                        events = defaultEvent(tsl),
                        services = defaultService(tsl)
                    )
                }

                initWatcher()
                onSuccess()
            }
        }
    }

    override fun connect() {

        if (device !is MQTTDevice) throw UnSupportDeviceException(device::class)
        val mqttParam = device.createMqttParam(projectInfo)
        if (deviceStateHolderFlow.value.tsl == null) {
            refreshTsl(mqttParam) {
                connect()
            }
            return
        }
        li("::connect mqttParam $mqttParam")
        connectJob?.cancel()
        connectJob = coroutineScope.launch(Dispatchers.IO) {

            try {
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
                        onChanged("onHDMqttStateChangedListener $s") {
                            it.copy(connect = s.isConnected)
                        }
                        coroutineScope.launch {
                            if (s.isConnected) {
                                registerTopics(device as TwinsDevice)
//
                            }
                        }
                    },
                    onHDMqttMessageChangedListener = onMessageChanged

                )
            } catch (e: Exception) {
                le("::connect error $e")
            }
        }.also {
            it.invokeOnCompletion { e ->
                le("::connect invokeOnCompletion  $e")
            }
        }
    }

    private val onMessageChanged = OnHDMqttMessageChangedListener { client, topic, m ->
        li("::connect MqttResultMessageChanged$client $topic ${m.payload}")
        if (mqttClient != client) {
            lw("::connect MqttResultMessageChanged client不对")
            return@OnHDMqttMessageChangedListener
        }
        if (device !is TwinsDevice) throw UnSupportDeviceException(device::class)
        val msg = device.providerMqttConvertor().decode(m.payload)
        DefaultDeviceHandle(coroutineScope).handle(this, msg)
    }

    private fun registerTopics(device: TwinsDevice) {
        registerTopicsJob?.cancel()
        registerTopicsJob = null
        registerTopicsJob = coroutineScope.launch {
//            if (device !is TwinsDevice) throw UnSupportDeviceException(device::class)
            val supportTopics = device.supportTopics()
            val iterator = supportTopics.iterator()
            while (iterator.hasNext()) {
                delay(500)
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

    override fun publish(message: ProtocolMQTTMessage) {
        li("[::publish] $message")
        coroutineScope.launch {

            li("[::publish] ${device is TwinsDevice} 000000 ")
            require(device is TwinsDevice) {
                " throw UnSupportDeviceException(device::class)"
            }
            li("[::publish] 1111111 ")
            val topic: String = device.generateTopic(projectInfo, message.topic)
            val qos = message.qos
            val retention = message.retain == 1
            val payload = device.providerMqttConvertor().encode(message)
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    li("[::publish] 333333   hdMqttPublishSuspend a")
                    val result =  mqttClient.hdMqttPublishSuspend(
                        topic,
                        payload,
                        qos = qos,
                        retained = retention
                    )
                    li("[::publish] 333333   hdMqttPublishSuspend z:${result.success}")
                    li("[::publish] 333333   add a")
                    logRepository.add(
                        UpLog(
                            id = 0,
                            timestamp = currentTime(),
                            deviceId = device.id,
                            clientId = mqttClient.hdClientId,//?:device.createMqttParam(projectInfo).clientId,
                            topic = topic,
                            cmd = message.cmd.cmd,
                            payload = payload.decodeToString(),
                            state = result.success
                        )
                    )
                    li("[::publish] 333333   add z")
                } catch (e: Exception) {
                    li("[::publish] error $e")
                }
            }

        }.invokeOnCompletion {
            li("[::publish] invokeOnCompletion $it")
        }
    }

    private fun publishInternal(mqttMessage: ProtocolMQTTMessage, tag: String) {
        li("::publish $mqttMessage @$tag")


        publish(mqttMessage)
    }

    override fun clear() {
        li("::clear")
        refreshTslJob?.cancel()
        coroutineScope.launch {
            //
        }
    }

    private fun onChanged(tag: String, block: (DeviceStateHolder) -> DeviceStateHolder) {
        val oldValue = this.deviceStateHolderFlow.value
        val newValue = block(oldValue)
        li("::onChanged [$tag]")
        deviceStateHolderFlowInternal.value = newValue
    }

    private fun tryGetEventKey(id: String): EventKey? =
        deviceStateHolderFlow.value.events.entries.singleOrNull { (k, v) ->
            k == id || v.identifier == id
        }?.value

    /**
     * 监听环境触发[EventKey]
     */
    private val watcherList: MutableList<HDWatcher> = mutableListOf()
    private fun initWatcher() {
        initWatcherJob?.cancel()
        initWatcherJob = null
        watcherList.clear()
        val eventKey = tryGetEventKey("waterAlert")
        if (eventKey != null) {
            watcherList.add(TdsWatcher(eventKey))
        }

        initWatcherJob = coroutineScope.launch {
            deviceStateHolderFlow.collect { holder ->
                watcherList.forEach { hdWatcher ->
                    val watch = hdWatcher.watch(holder.properties)
                    if (watch != null) {
                        sendEventInternal(
                            watch.first,
                            watch.second
                        )
                    }
                }
            }
        }
    }

    /**
     * 上报
     */
    private fun initReporter() {
        coroutineScope.launch {


        }
    }


    /**
     * 设备属性随机生成器
     */
    private fun iniDeviceInfoMaker() {
        iniDeviceInfoMakerJob?.cancel()
        iniDeviceInfoMakerJob = null
        iniDeviceInfoMakerJob = coroutineScope.launch {
            while (true) {

                if (mqttClient.hdMqttState.isConnected) {
                    val rssi = Random.nextInt(99)
                    li("randomRssi - rssi:$rssi")
                    Napier.e("")
                    deviceStateHolderFlow.value.properties.forEach { (k, v) ->
                        //li("randomRssi  比较${k}")
                        if (k == "rssi" || k == "signalStrength") {
                            val value = v as IntPropertyValue
                            sendProperty(
                                IntPropertyValue.createValue(value.key, -rssi),
                                publishLimit = true
                            )
                        }
                    }
                }
                delay(10000)

            }
        }
    }

    /* ********** api ************ a */
    /**
     * 更新属性
     * @param publishLimit 是否立即上报mqtt broke
     */
    fun sendProperty(vararg property: PropertyValue<*>, publishLimit: Boolean = true): Boolean {
        val oldProperties = deviceStateHolderFlow.value.properties

        // 1.更新自己的数据 并缓存
        val map =
            tslHandleUpdatePropertyValues(oldProperties, property.toList())
        // onPropertiesChanged("sendProperty", map.values.toList())
        onChanged("sendProperty") { holder ->
            // Map<String,PropertyValue<*>>
//            val oldMap = holder.properties
//            val newMap = tslHandleUpdatePropertyValues(oldMap, values)
            holder.copy(properties = map)
        }

        savePropertiesLocal(map, "sendProperty")

        if (publishLimit) {
            // 2.是否上报mqtt broke
            // val test = IntPropertyValue.createValue(null, -99)
            // val payload = tslParser.toJson(property.toList() + test).encodeToByteArray()
            val payload = tslParser.toJson(property.toList()).encodeToByteArray()
            publishInternal(ReportMQTTMessage(payload), "::sendProperty")
        }
        return true
    }

    /**
     * 主动触发事件
     * todo
     */
    fun sendEvent(key: String, value: List<PropertyValue<*>>): Boolean {
        val stateHolder = deviceStateHolderFlow.value
        val eventKey = stateHolder.events[key]
        if (eventKey == null) {
            lw("key:$key 不存在")
            return false
        }
        return sendEventInternal(eventKey, value)
    }

    private fun sendEventInternal(key: EventKey, value: List<PropertyValue<*>>): Boolean {
        ld("sendEvent ${key.identifier}")
        val properties: MutableMap<String, PropertyValue<*>> = mutableMapOf()
        value.forEach { entry ->
            key.outputData.forEach { pk ->
                if (pk.identifier == entry.key.identifier) {
                    properties[pk.identifier] = entry
                }
            }
        }
        val payload = TslValueParser.eventToJson(key, value).encodeToByteArray()
        publishInternal(ReportMQTTMessage(payload), "::sendEvent")
        return true
    }

    suspend fun handleService(
        key: String,
        input: List<PropertyValue<*>>,
        delay: Long = 3000L,
    ): Boolean {
        val stateHolder = deviceStateHolderFlow.value
        val serviceKey = stateHolder.services[key]
        if (serviceKey == null) {
            lw("key:$key 不存在")
            return false
        }
        delay(delay)
        handleServiceInternal(serviceKey, input)
        return true
    }

    internal fun handleServiceInternal(serviceKey: ServiceKey, input: List<PropertyValue<*>>) {
        val identifier = serviceKey.identifier
        when (identifier) {
            "requestTime" -> {
                val outputValue = serviceKey.outputData.map {
                    it.toDefaultValue()
                }.map { propertyValue ->
                    when {
                        "time" == propertyValue.key.identifier && propertyValue is DatePropertyValue -> {
                            // 处理input
                            val size = input.size
                            DatePropertyValue.createValue(
                                propertyValue.key,
                                currentTime().toString()
                            )
                        }

                        "time2" == propertyValue.key.identifier && propertyValue is DatePropertyValue -> {
                            // 处理input
                            DatePropertyValue.createValue(
                                propertyValue.key,
                                currentTime().toString()
                            )
                        }

                        else -> propertyValue
                    }
                }
                val payload =
                    TslValueParser.serviceToJson(serviceKey, input, outputValue).encodeToByteArray()
                publishInternal(
                    ReplyServiceMQTTMessage(payload, identifier),
                    "::handleServiceInternal"
                )
            }

            else -> {
                lw("handleServiceInternal 不支持的服务$serviceKey")
            }
        }
    }

    /* ********** api ************ z */

    private fun savePropertiesLocal(source: Map<String, PropertyValue<*>>, tag: String) {
        ld("syncDeviceManagerChanged $tag")
        // 保存设备状态
        coroutineScope.launch {
            // TODO error    kotlinx.serialization.SerializationException: Serializer for class 'Any' is not found.
//            val list = source.values.toList()
//            val map = list.tslHandleToJsonValues()
//            if (map.isEmpty()) return@launch
//            val json = hdJson.encodeToString(map)
//            ld("tsl save  json = $json")
//            if (json.isEmpty()) return@launch

            // todo 保存到本地
            // deviceRepository.saveDeviceTslValue(device.generateId(), json)
        }
    }


//    private fun onPropertiesChanged(tag: String, values: List<PropertyValue<*>>) {
//        onChanged("onPropertiesChanged/$tag") { holder ->
//            // Map<String,PropertyValue<*>>
//            val oldMap = holder.properties
//            val newMap = tslHandleUpdatePropertyValues(oldMap, values)
//            holder.copy(properties = newMap)
//        }
//    }

    private fun defaultProperty(tsl: Tsl?): Map<String, PropertyValue<*>> {
        val map: Map<String, PropertyValue<*>> = tsl?.tslHandleTsl2PropertyValues() ?: emptyMap()
        li("::defaultProperty ")
        map.forEach { (k, v) ->
//            if (v is StructArrayPropertyValue){
            li(
                """
                    $k      :   ${v.displayValue}
                """.trimIndent()
            )
//            }
        }
        return map
    }

    private fun defaultEvent(tsl: Tsl?): Map<String, EventKey> {
        val map: Map<String, EventKey> = tsl?.tslHandleTsl2EventKeys() ?: emptyMap()
        return map
    }

    private fun defaultService(tsl: Tsl?): Map<String, ServiceKey> {
        val map: Map<String, ServiceKey> = tsl?.tslHandleTsl2ServiceKeys() ?: emptyMap()
        return map
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
