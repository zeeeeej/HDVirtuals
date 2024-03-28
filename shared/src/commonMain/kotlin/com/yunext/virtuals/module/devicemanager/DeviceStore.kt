package com.yunext.virtuals.module.devicemanager

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.mqtt.HDMqttClient
import com.yunext.kmp.mqtt.core.OnHDMqttActionListener
import com.yunext.kmp.mqtt.data.HDMqttMessage
import com.yunext.kmp.mqtt.hdMqttConnect
import com.yunext.kmp.mqtt.hdMqttSubscribeTopic
import com.yunext.kmp.mqtt.virtuals.coroutine.MqttResultAction
import com.yunext.kmp.mqtt.virtuals.coroutine.MqttResultError
import com.yunext.kmp.mqtt.virtuals.coroutine.MqttResultMessageChanged
import com.yunext.kmp.mqtt.virtuals.coroutine.MqttResultStateChanged
import com.yunext.kmp.mqtt.virtuals.coroutine.hdMqttConnectFlow
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * 设备信息
 */
interface IDeviceStore {

    val device: HDDevice
    fun connect()
    fun disconnect()
    fun publish(topic: String, mqttMessage: HDMqttMessage)

    fun clear()

}

class DeviceStore(
    private val hdContext: HDContext,
    private val projectInfo: ProjectInfo,
    override val device: HDDevice,
    private val coroutineScope: CoroutineScope,
) : IDeviceStore {
    private val mqttClient: HDMqttClient = HDMqttClient(hdContext)

    // jobs
    private var connectJob: Job? = null

    override fun connect() {
        if (device !is MQTTDevice) error("暂不支持MQTTDevice意外的设备。${device::class}")
        val mqttParam = device.createMqttParam(projectInfo)
        logger.i(TAG, "::connect mqttParam $mqttParam")
        connectJob?.cancel()
        coroutineScope.launch(Dispatchers.IO) {
            mqttClient.hdMqttConnect(
                mqttParam,
                listener = object : OnHDMqttActionListener {
                    override fun onSuccess(token: Any?) {
                        logger.i(TAG, "::connect MqttResultAction onSuccess")
                    }

                    override fun onFailure(token: Any?, exception: Throwable?) {
                        logger.i(TAG, "::connect MqttResultAction onFailure")
                    }

                },
                onHDMqttStateChangedListener = { _, s ->
                    logger.i(TAG, "::connect MqttResultStateChanged $s")
                },
                onHDMqttMessageChangedListener = { _, t, m ->
                    logger.i(TAG, "::connect MqttResultMessageChanged $t ${m.payload}")
                }

            )

            delay(1000)
            val supportTopics = device.supportTopics()
            val testTopic: String = device.generateTopic(projectInfo, supportTopics[0])

            mqttClient.hdMqttSubscribeTopic(testTopic,object :OnHDMqttActionListener{
                override fun onSuccess(token: Any?) {
                    logger.i(TAG, "::connect ========>  ok")
                }

                override fun onFailure(token: Any?, exception: Throwable?) {
                    logger.i(TAG, "::connect ========> error:$exception")
                }

            })
        }
//        connectJob = mqttClient.hdMqttConnectFlow(mqttParam).onEach {
//            when (it) {
//                is MqttResultAction -> {
//                    logger.i(TAG, "::connect MqttResultAction $it")
//                }
//
//                is MqttResultError -> {
//                    logger.i(TAG, "::connect MqttResultError $it")
//                }
//
//                is MqttResultMessageChanged -> {
//                    logger.i(TAG, "::connect MqttResultMessageChanged $it")
//                }
//
//                is MqttResultStateChanged -> {
//                    logger.i(TAG, "::connect MqttResultStateChanged $it")
//                }
//            }
//        }.onCompletion {
//            logger.i(TAG, "::connect onCompletion $it")
//        }.launchIn(coroutineScope)

    }

    override fun disconnect() {
        logger.i(TAG, "::disconnect")
        coroutineScope.launch {
            mqttClient.hdMqttDisconnectSuspend()
        }
    }

    override fun publish(topic: String, mqttMessage: HDMqttMessage) {
        logger.i(TAG, "::publish $mqttMessage")
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
        logger.i(TAG, "::clear")
        coroutineScope.launch {
            //
        }
    }


    companion object {
        private val logger = HDLogger
        private const val TAG = "DeviceStore"
    }
}

class DeviceStoreWrapper(
    val deviceStore: DeviceStore,
    private val time: Long = currentTime(),
)
//class DeviceStore(
//    context: Context,
//    private val projectInfo: ProjectInfo,
//    val device: MQTTDevice,
//    private val coroutineScope: CoroutineScope,
//    private val mqttManager: MQTTDeviceManager,
//    private val logRepository: LogRepository,
//    private val deviceRepository: DeviceRepository,
//    private val reportRepository: ReportRepository,
//    /**
//     * 设备通用的属性值初始化
//     */
//    private val deviceInitializer: DeviceInitializer,
//
//    ) : ILogger by DefaultLogger(TAG, true) {
//    private var mClient: HadlinksMqttClient? = null
//    private var mTsl: Tsl? = null
//    private var mConnectJob: Job? = null
//    private val localDevice = LocalDevice()
//
//    private val reportManager: Reporter = ReportManager(coroutineScope, this, reportRepository)
//
//    val clientId: String?
//        get() = mClient?.clientId
//
//    private val mDeviceHandleList: MutableList<DeviceHandle> =
//        mutableListOf(DefaultDeviceHandle(coroutineScope, context))
//
//    val state: MqttState
//        get() = deviceStateFlow.value
//
//    /**
//     * tsl
//     */
//    private val _tslFlow: MutableStateFlow<Tsl?> =
//        MutableStateFlow(null)
//
//    val tslFlow = _tslFlow.asStateFlow()
//
//    /**
//     * 属性property
//     */
//    private val _propertiesFlow: MutableStateFlow<Map<String, PropertyValue<*>>> =
//        MutableStateFlow(mapOf())
//
//    val propertiesFlow: StateFlow<Map<String, PropertyValue<*>>> =
//        _propertiesFlow.asStateFlow()
//
//    private val mServiceDownEffect: MutableSharedFlow<String> = MutableSharedFlow()
//    val serviceDownEffect = mServiceDownEffect.asSharedFlow()
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
//
//    private val _deviceStateFlow: MutableStateFlow<MqttState> = MutableStateFlow(MqttState.Init)
//    val deviceStateFlow: StateFlow<MqttState> = _deviceStateFlow.asStateFlow()
//
//    fun connect(context: Context, auto: Boolean = true) {
//        ld("connect")
//        mConnectJob?.cancel()
//        mConnectJob = coroutineScope.launch {
//            val result = connectInterval(context)
//            ld("连接结果：$result")
//            if (!result) {
//                ensureActive()
//                delay(10_000)
//                ensureActive()
//                ld("重新连接...")
//                connect(context, auto)
//            }
//        }
//    }
//
//    private val mapToken = object :TypeToken<Map<String,Any>>(){}.type
//    init {
//        NodeRed.init(context){json->
//            coroutineScope.launch {
////                val jsonObject = JSONObject(json)
////                val map =  gson.fromJson<Map<String,Any>>(it,mapToken)
////                ld("NodeRed::receiver $jsonObject")
////                publish(SetMQTTMessage(jsonObject),"node-red")
//
//                // 修改本地数据
//                val map = tslHandleUpdatePropertyValuesFromJson(
//                    propertiesFlow.value,
//                    json
//                )
//                _propertiesFlow.value = (map.first)
//            }
//        }
//    }
//
//    private var mCurrentMqttParam: MqttParam? = null
//
//    private suspend fun connectInterval(context: Context): Boolean {
//        val mqttParam = device.createMqttParam(projectInfo)
//        mCurrentMqttParam = mqttParam
//        return withContext(Dispatchers.IO) {
//            try {
//                val myOnMessageChangedListener =
//                    OnMessageChangedListener { client, topic, message ->
//                        val msg = device.providerMqttConvertor().decode(message.payload)
//                        val dm = DeviceAndMessage(device, topic, msg)
//                        NodeRed.transformIn(gson.toJson(dm).toByteArray()) // NodeRed
//                        onMessageChanged(client, dm)
//                    }
//
//                val myOnStateChangedListener = OnStateChangedListener { client, state ->
//                    coroutineScope.launch {
//                        onStateChanged(client, state)
//                    }
//                }
//                val hadlinksMqttClient = HadlinksMqttClient(context).apply {
//                    registerOnMessageChangedListener(myOnMessageChangedListener)
//                    registerOnStateChangedListener(myOnStateChangedListener)
//                }
//                val connect =
//                    try {
//                        hadlinksMqttClient.suspendConnect(mqttParam)
//                    } catch (e: Throwable) {
//                        false
//                    }
//                if (connect) {
//                    mClient = hadlinksMqttClient
//                    //onStateChanged(hadlinksMqttClient, MqttState.Connected)
//                    val topics = device.supportTopics().iterator()
//                    while (topics.hasNext()) {
//                        val topic: String = device.generateTopic(projectInfo, topics.next())
//                        val success = subscribeTopicInner(hadlinksMqttClient, topic)
//                        ld(TAG, "[$topic] $success")
//                    }
//
//                    true
//                } else {
//                    hadlinksMqttClient.disconnect()
//                    false
//                }
//            } catch (e: Throwable) {
//                e.printStackTrace()
//                false
//            }
//        }
//    }
//
//    private var mFirstReportJob: Job? = null
//
//    /**
//     * mqtt 在离线状态
//     */
//    private fun onStateChanged(client: HadlinksMqttClient, state: MqttState) {
//        coroutineScope.launch {
//            _deviceStateFlow.emit(state)
//            syncDeviceManagerChanged("onStateChanged $state")
//            //startReport(state)
//        }
//
//        // online
//        coroutineScope.launch {
//            logRepository.add(
//                OnlineLog(
//                    timestamp = System.currentTimeMillis(),
//                    deviceId = device.generateId(),
//                    clientId = client.clientId,
//                    onLine = state == MqttState.Connected
//                )
//            )
//        }
//        mFirstReportJob?.cancel()
//        mFirstReportJob = coroutineScope.launch {
//            if (state == MqttState.Connected) {
//                ld("--> report first properties ")
//                var reported = false
//                var count: Int = 0
//                try {
//                    delay(1000) // todo
////                    while (!reported && count < 3) {
//                    ld("--> report first properties $reported $count")
//                    val r = withContext(Dispatchers.IO) {
//                        reportRepository.takeFirstReport(device.generateId())
//                    }
//                    if (r != null) {
//                        val list = r.list
//                        if (list.isEmpty()) return@launch
//                        val pros = propertiesFlow.value
//
//                        val firstList = list.map {
//                            pros[it.id]
//                        }.filterNotNull()
//                        ld("--> report first properties : ${firstList.size}")
//                        val result = reportProperties(*firstList.toTypedArray())
//                        if (result) {
//                            reported = true
//                            ld("--> report first properties success")
//
//                        } else {
//                            reported = false
//                            count++
//                            delay(1000)
//                        }
//                    }
////                    }
//
//                } catch (e: Throwable) {
//
//                }
//
//            }
//        }
//    }
//
//    fun initTsl(tsl: Tsl, update: Boolean = false) {
//        if (update) {
//            // .
//        } else {
//            val cur = mTsl
//            if (cur != null) {
//                if (cur.version == tsl.version) {
//                    return
//                }
//            }
//        }
//
//        li("initTsl")
//        mTsl = tsl
//        _tslFlow.value = tsl
//
//        _propertiesFlow.value = tsl.tslHandleTsl2PropertyValues()
//        _eventFlow.value = tsl.tslHandleTsl2EventKeys()
//        _serviceFLow.value = tsl.tslHandleTsl2ServiceKeys()
//
//        // 尝试从本地初始化值
//        coroutineScope.launch {
//            delay(500)
//            val json = deviceRepository.loadDeviceTslValue(device.generateId())
//            ld("tsl load json = $json")
//            val map = tslHandleUpdatePropertyValuesFromJson(
//                propertiesFlow.value,
//                json
//            )
//            _propertiesFlow.value = (map.first)
//
//            @NeedFix("应该在解析完TSL的时候选择性初始化属性值。")
//            val initializerValues = deviceInitializer.init(_propertiesFlow.value)
//            _propertiesFlow.value = initializerValues
//
//            // 在初始化过后在执行
//            @NeedFix("TODO 和DeviceInitializer合并")
//            localDevice.randomRssi()
//        }
//        // 尝试从本地初始化值
//
//
//        coroutineScope.launch {
//            val custom = reportRepository.take(device.generateId())
//            startReport(custom)
//        }
//
//
//    }
//
//    @Deprecated("")
//    private fun startReport(state: MqttState) {
//        // 开始上报
//        coroutineScope.launch(Dispatchers.IO) {
//            if (state == MqttState.Connected) {
//                val data = reportRepository.take(device.generateId())
//                if (data != null) {
//                    reportManager.setData(data)
//                }
//                reportManager.start()
//            } else {
//                reportManager.stop()
//            }
//        }
//    }
//
//    private var mStartReportJob: Job? = null
//    fun startReport(data: DeviceIdAndReportData?) {
//        ld("startReport $data")
//        mStartReportJob?.cancel()
//        reportManager.stop()
//        mStartReportJob = coroutineScope.launch(Dispatchers.IO) {
//            if (data == null) {
//                reportManager.stop()
//                return@launch
//            }
//            val reportData = try {
//                reportRepository.take(device.generateId())
//            } catch (e: Throwable) {
//                null
//            }
//                ?: return@launch
////             ?: kotlin.run {
////                reportRepository.put(device.generateId(),data)
////                data
////            }
//
//            reportManager.setData(reportData)
//            reportManager.start()
//        }
//    }
//
//    fun setReportData(data: DeviceIdAndReportData) {
//        coroutineScope.launch(Dispatchers.IO) {
//            reportManager.setData(data)
//            reportManager.start()
//        }
//
//    }
//
//    private fun tryAction(action: () -> Unit) {
//        try {
//            action()
//        } catch (e: Throwable) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * 同步更新本地的数据
//     *
//     * 1.从服务器下发的数据
//     */
//    internal fun notifyProperties(map: Map<String, PropertyValue<*>>) {
//        _propertiesFlow.value = map
//        syncDeviceManagerChanged("notifyProperties ${map.size}")
//    }
//
//    private fun onMessageChanged(client: HadlinksMqttClient, dm: DeviceAndMessage) {
//        coroutineScope.launch {
//            try {
//                if (dm.device.generateId() != device.generateId()) return@launch
//                li("onMessageChanged:${gson.toJson(dm)}")
//                // 处理业务
//                mDeviceHandleList.forEach { handle ->
//                    if (handle.handle(this@DeviceStore, dm)) {
//                        // 处理过了就直接返回
//                        return@launch
//                    }
//                }
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            }
//        }
//
//        // down
//        coroutineScope.launch {
//            logRepository.add(
//                DownLog(
//                    timestamp = System.currentTimeMillis(),
//                    deviceId = device.generateId(),
//                    clientId = client.clientId,
//                    topic = dm.topic,
//                    cmd = dm.message?.cmd ?: "",
//                    payload = dm.message?.params?.toString() ?: "",
//                )
//            )
//        }
//    }
//
//    private fun syncDeviceManagerChanged(tag: String) {
//        mqttManager.onDeviceChanged(this)
//
//        ld("syncDeviceManagerChanged $tag")
//        // 保存设备状态
//        coroutineScope.launch {
//            val list = _propertiesFlow.value.values.toList()
//            val map = list.tslHandleToJsonValues()
//            if (map.isEmpty()) return@launch
//            val json = MQTT_GSON.toJson(map)
//            ld("tsl save  json = $json")
//            if (json.isNullOrEmpty()) return@launch
//            deviceRepository.saveDeviceTslValue(device.generateId(), json)
//        }
//    }
//
//    private fun tryGetEventKey(id: String): EventKey? = eventFlow.value.values.singleOrNull {
//        it.identifier == id
//    }
//
//    private var checkEventKey: Job? = null
//
//    /**
//     * @param keys 设置的属性 是否包含key 才会检测key
//     * FIXME 具体业务 需要解耦出去
//     */
//    @NeedFix("处理事件")
//    private fun checkEvent(keys: List<String>) {
//        ld("checkEvent ")
//        checkEventKey?.cancel()
//        checkEventKey = coroutineScope.launch {
//            propertiesFlow.value.forEach {
//                when (it.key) {
//                    "rawTDS" -> {
//                        // 模拟tds异常时发出通知
//                        try {
//                            if (!keys.contains(it.key)) return@launch
//                            val v = it.value as IntPropertyValue
//                            v.value ?: return@launch
//                            if ((v.value > 0) and (v.value <= 100)) {
//                                tryGetEventKey("waterAlert")?.let { eventKey ->
//                                    val key = eventKey.outputData.singleOrNull() { item ->
//                                        item.identifier == "code"
//                                    }
//                                    sendEvent(
//                                        eventKey,
//                                        listOf(
//                                            IntEnumPropertyValue.from(
//                                                key as IntEnumPropertyKey,
//                                                1
//                                            )
//                                        )
//                                    )
//                                }
//                            }
//                        } catch (e: Throwable) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                }
//            }
//        }
//
//    }
//
//    fun disconnect() {
//        try {
//            reportManager.stop()
//            mClient?.disconnect()
//            mConnectJob?.cancel()
//            mFirstReportJob?.cancel()
//            mStartReportJob?.cancel()
//            localDevice.cancel()
//        } catch (e: Throwable) {
//            e.printStackTrace()
//        }
//
//    }
//
//    private suspend fun subscribeTopicInner(
//        mqttManager: HadlinksMqttClient,
//        topic: String,
//        count: Int = 0,
//    ): Boolean {
//        if (count > 3) throw IllegalStateException("尝试注册topic[$topic]${count}次失败")
//        val result = mqttManager.suspendSubscribeTopic(topic)
//        return if (result) {
//            true
//        } else {
//            subscribeTopicInner(mqttManager, topic, count + 1)
//        }
//    }
//
//    /**
//     * 主动设置属性
//     * mqtt里pub为服务器
//     */
//    suspend fun sendProperty(vararg property: PropertyValue<*>, publish: Boolean = true): Boolean {
//        // 更新自己的数据
//        val map =
//            tslHandleUpdatePropertyValues(_propertiesFlow.value, property.toList())
//        _propertiesFlow.value = map
//        checkEvent(property.map { it.key.identifier })
//
//        // 上报服务器
////        mqttManager.publishWithMessageTypeForProperty<ReportMQTTMessage>(
////            device as TwinsDevice,
////            *property
////        )
//        val reportMap = property.toList().tslHandleToJsonValues()
//        if (reportMap.isEmpty()) return true
//        if (publish) {
//            publish(ReportMQTTMessage(reportMap),"::sendProperty")
//        }
//        syncDeviceManagerChanged("sendProperty")
//        return true
//    }
//
//    /**
//     *
//     * 回复set
//     */
//    suspend fun replySet(vararg property: PropertyValue<*>): Boolean {
//        val reportMap = property.toList().tslHandleToJsonValues()
//        publish(SetRepayMQTTMessage(reportMap),"::replySet")
////        mqttManager.publishWithMessageTypeForProperty<SetRepayMQTTMessage>(
////            device as TwinsDevice,
////            *property
////        )
//        return true
//    }
//
//    /**
//     * 主动触发事件
//     * todo
//     */
//    suspend fun sendEvent(key: EventKey, value: List<PropertyValue<*>>): Boolean {
//        ld("sendEvent ${key.identifier}")
//        val properties: MutableMap<String, PropertyValue<*>> = mutableMapOf()
//        value.forEach { entry ->
//            key.outputData.forEach { pk ->
//                if (pk.identifier == entry.key.identifier) {
//                    properties[pk.identifier] = entry
//                }
//            }
//        }
//
//        val finalMap = properties.values.toList().tslHandleToJsonValues()
//        if (finalMap.isEmpty()) return true
//        //val finalMap = properties.values.toList().tslHandleToJsonObject()
//        val map = mapOf(key.identifier to finalMap)
//
//        return publish(ReportMQTTMessage(map),"::sendEvent")
//    }
//
//    /**
//     * 回复data到服务器
//     */
//    suspend fun replyProperty(vararg keys: String): Boolean {
//        if (keys.isEmpty()) return true
//        val newMap: MutableMap<String, PropertyValue<*>> = mutableMapOf()
//        propertiesFlow.value.forEach { entry ->
//            keys.forEach { k ->
//                if (k == entry.key) {
//                    newMap[k] = entry.value
//                }
//            }
//        }
//        // 回复
//        val reply = newMap.map {
//            it.value
//        }
//
//        val finalMap = reply.toList().tslHandleToJsonValues()
//        if (finalMap.isEmpty()) return true
//        publish(DataReplyMQTTMessage(finalMap),"::replyProperty")
////        mqttManager.publishWithMessageTypeForProperty<DataReplyMQTTMessage>(
////            device as TwinsDevice,
////            *reply
////        )
//        syncDeviceManagerChanged("replyProperty")
//        return true
//    }
//
//    suspend fun publish(mqttMessage: MQTTMessage,tag:String ): Boolean {
//        ld("publish from $tag")
//        return mClient?.let { mqttClient ->
//            val topic: String = device.generateTopic(projectInfo, mqttMessage.topic)
//            val qos = mqttMessage.qos
//            val retention = mqttMessage.retain == 1
//            val payload = device.providerMqttConvertor().encode(mqttMessage)
//            val r = mqttClient.suspendPublish(
//                topic = topic, payload = payload,
//                qos = qos, retained = retention
//            )
//
//            // up
//            coroutineScope.launch {
//                val log =   UpLog(
//                    timestamp = System.currentTimeMillis(),
//                    deviceId = device.generateId(),
//                    clientId = mqttClient.clientId,
//                    topic = topic,
//                    cmd = mqttMessage.cmd.cmd,
//                    payload = gson.toJson(mqttMessage.data),
//                    state = r
//                )
//                val log2 =   mapOf(
//                    "timestamp" to System.currentTimeMillis(),
//                    "deviceId" to device.generateId(),
//                    "clientId" to mqttClient.clientId,
//                    "topic" to topic,
//                    "cmd" to mqttMessage.cmd.cmd,
//                    "payload" to mqttMessage.data,
//                    "state" to r
//                )
//                logRepository.add(log)
//                NodeRed.transformOut(
//                    gson.toJson(log2).toByteArray()
//                ) // NodeRed
//            }
//
//
//            r
//
//
//        } ?: false
//    }
//
//    suspend fun reportProperties(vararg property: PropertyValue<*>): Boolean {
//        val map = property.toList().tslHandleToJsonValues()
//        if (map.isEmpty()) return true
//        return publish(ReportMQTTMessage(map),"::reportProperties")
//    }
//
//    fun handleService(serviceKey: ServiceKey, values: List<PropertyValue<*>>?) {
//        val newMap = tslHandleUpdatePropertyValues(propertiesFlow.value, values ?: listOf())
//        _propertiesFlow.value = newMap
//
//        coroutineScope.launch {
//            li("handleService ${serviceKey.identifier}")
//            val msg = serviceKey.name + "  " + values?.joinToString(",") {
//                it.displayValue
//            }
//            mServiceDownEffect.emit(msg)
//        }
//    }
//
//    private inner class LocalDevice {
//        private var job: Job? = null
//        fun randomRssi() {
//            li("randomRssi")
//            job?.cancel()
//            job = coroutineScope.launch {
//                launch {
//                    while (true) {
//                        delay(1000)//
//                        val rssi = Random.nextInt(99)
//                        li("randomRssi - rssi:$rssi")
//                        propertiesFlow.value.forEach {
//                            li("randomRssi  比较${it.key}")
//                            if (it.key == "rssi") {
//                                val v = it.value as IntPropertyValue
//                                li("randomRssi -3 rssi:$rssi $v")
//                                sendProperty(IntPropertyValue(v.key, -rssi), publish = true)
//                            }
//                        }
//
//                    }
//                }
//            }
//
//
//        }
//
//        fun cancel() {
//            job?.cancel()
//        }
//
//    }
//
//
//    companion object {
//        private const val TAG = "_DeviceStore_"
//        private val gson = Gson()
//    }
//
//
//}
