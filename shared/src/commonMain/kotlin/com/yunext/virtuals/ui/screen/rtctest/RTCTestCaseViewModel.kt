package com.yunext.virtuals.ui.screen.rtctest

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLog
import com.yunext.kmp.common.logger.XLog
import com.yunext.kmp.common.util.currentTime
import com.yunext.virtuals.ui.common.HDStateScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.core.generateXBleDevice
import yunext.kotlin.bluetooth.ble.logger.XBleRecord
import yunext.kotlin.bluetooth.ble.master.BleMasterConnectedStatus
import yunext.kotlin.bluetooth.ble.master.BleMasterScanningStatus
import yunext.kotlin.bluetooth.ble.master.XBleMasterScanResult
import yunext.kotlin.bluetooth.ble.master.connected
import yunext.kotlin.bluetooth.ble.master.display
import yunext.kotlin.rtc.RTCMaster
import yunext.kotlin.rtc.procotol.ParameterData
import yunext.kotlin.rtc.procotol.ParameterKey
import yunext.kotlin.rtc.procotol.RTC_ACCESS_KEY
import yunext.kotlin.rtc.procotol.ServiceAndCharacteristics
import yunext.kotlin.rtc.procotol.text
import kotlin.random.Random


sealed class RTCTestCase(val desc: String) {
    object C1 : RTCTestCase("测试连接后不鉴权自动断开连接")
    object C2 : RTCTestCase("测试连接后不鉴权发送数据")
    data class C3(val key: String) : RTCTestCase("测试连接后鉴权不自动断开")
    data class C4(val key: String) : RTCTestCase("测试连接后鉴权发送数据")
}

private fun BleDeviceVo(d: XBleMasterScanResult) =
    BleDeviceVo(name = d.device.deviceName ?: "", mac = d.device.address, rssi = d.rssi)

private fun BleDeviceVo.asBleDevice() =
    generateXBleDevice(deviceName = this.name, address = this.mac)


@Stable
data class RTCTestCaseState(
    val scanning: BleMasterScanningStatus = BleMasterScanningStatus.ScanStopped,
    val connecting: BleMasterConnectedStatus = BleMasterConnectedStatus.Idle,
    val scanningDeviceList: List<BleDeviceVo> = listOf(TEST_DEVICE),
    val records: List<XBleRecord> = emptyList(),
    val services: List<XBleService> = emptyList(),
    val authEffect: AuthEffect = AuthEffect.Idle,
    val testCase01EffectList: List<TestCaseEffect> = emptyList(),
    val testCase01Effect: TestCaseEffect = TestCaseEffect.Idle,
    val testCase02EffectList: List<TestCaseEffect> = emptyList(),
    val testCase02Effect: TestCaseEffect = TestCaseEffect.Idle,
    val testCase03EffectList: List<TestCaseEffect> = emptyList(),
    val testCase03Effect: TestCaseEffect = TestCaseEffect.Idle,
    val testCase04EffectList: List<TestCaseEffect> = emptyList(),
    val commonCaseEffectList: List<TestCaseEffect> = emptyList(),
    val testCase04Effect: TestCaseEffect = TestCaseEffect.Idle,
    val setPropertyEffect: SetPropertyEffect = SetPropertyEffect.Idle,
    val currentTestCase: RTCTestCase? =null,

    val auth: Boolean = false,
    val params: List<ParameterDataVo> = emptyList(),
) {
    companion object {
        private val TEST_DEVICE = BleDeviceVo("测试", "00:00:00:00:00:00", -1)
    }
}

@OptIn(ExperimentalStdlibApi::class)
class RTCTestCaseViewModel :
    HDStateScreenModel<RTCTestCaseState>(DEFAULT), XLog by HDLog("RTCTestCaseViewModel", true) {
    companion object {
        private val DEFAULT by lazy {
            RTCTestCaseState()
        }

        internal const val authTimeout = 1 * 60 * 1000L
    }

    private val rtcMaster: RTCMaster = RTCMaster()

    init {

        screenModelScope.launch {
            launch {
                rtcMaster.connected.collectLatest {
                    mutableState.value = state.value.copy(
                        connecting = it,
                    )
                    i("设备连接状态回调 connected:$it ,connectChannel:$connectChannel")
                    if (!it.connected) {
                        mutableState.value = state.value.copy(
                            authEffect = AuthEffect.Idle,
                            auth = false,
                        )
                    } else {
                        connectChannel?.trySend(true)
                    }


                }
            }

            launch {
                rtcMaster.localPropertyMap.collectLatest {
                    val list = it.map { (k, v) ->
                        ParameterDataVo(k.name, v.toHexString(), k.text)
                    }
                    mutableState.value = state.value.copy(
                        params = list,
                    )
                }
            }

            launch {
                rtcMaster.scanning.collectLatest {
                    d("[launch rtcMaster.scanning] $it")
                    mutableState.value = state.value.copy(
                        scanning = it
                    )
                }
            }

            launch {
                rtcMaster.record.collectLatest {
                    mutableState.value = state.value.copy(
                        records = it
                    )
                }
            }

            launch {
                rtcMaster.scanningResult.collectLatest {
                    mutableState.value = state.value.copy(
                        scanningDeviceList = it.map(::BleDeviceVo)
                    )
                }
            }
            launch {
                rtcMaster.services.collectLatest {
                    mutableState.value = state.value.copy(
                        services = it
                    )
                }
            }
        }

    }


    fun startScan() {
        rtcMaster.startScan()
    }

    fun connect(bleDeviceVo: BleDeviceVo) {
        rtcMaster.connect(bleDeviceVo.asBleDevice())
    }

    fun enableNotify() {
        rtcMaster.enableNotify()
    }


    fun stopScan() {
        rtcMaster.stopScan()
    }


    fun disconnect() {
        cancelCaseJob()
        rtcMaster.disconnect()
    }

    fun write() {
        screenModelScope.launch {
            try {
                // TODO
                //rtcMaster.write(Random.nextBytes(4))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun read() {
        // TODO
        //rtcMaster.read()
    }

    fun auth(key: String) {
        screenModelScope.launch {
            try {
                mutableState.value = state.value.copy(authEffect = AuthEffect.Start(key))
                // TODO
//                val auth = rtcMaster.auth(key.ifEmpty { RTC_ACCESS_KEY })
//                mutableState.value = state.value.copy(
//                    authEffect =
//                    if (auth) {
//                        AuthEffect.Success(key)
//                    } else
//                        AuthEffect.Fail(key, "auth key fail!"), auth = auth
//                )

            } catch (e: Exception) {
                mutableState.value = state.value.copy(
                    authEffect = AuthEffect.Fail(key, "auth key fail! ${e.message ?: e.toString()}")
                )
            }
        }.also {
            it.invokeOnCompletion {
                d("auth::invokeOnCompletion")
            }
        }
    }

    fun setProperty(key: String, value: String) {
        screenModelScope.launch {
            mutableState.value =
                state.value.copy(setPropertyEffect = SetPropertyEffect.Start(key, value))
            try {
                // TODO
//                val property = rtcMaster.setProperty(key, value)
//                if (property) {
//                    mutableState.value =
//                        state.value.copy(setPropertyEffect = SetPropertyEffect.Success(key, value))
//                } else {
//                    mutableState.value = state.value.copy(
//                        setPropertyEffect = SetPropertyEffect.Fail(
//                            key,
//                            value,
//                            "设置属性失败"
//                        )
//                    )
//                }
            } catch (e: Exception) {
                mutableState.value = state.value.copy(
                    setPropertyEffect = SetPropertyEffect.Fail(
                        key,
                        value,
                        "设置属性失败${e.message}"
                    )
                )
            }
        }
    }

    fun setTimestamp() {

    }


    fun authedTestCase() {
        // auth 鉴权
        // 写参数 成功
        // 写时间戳 成功
        // 超过x时间不掉线
    }

    fun unAuthedTestCase() {
        // 写参数 失败
        // 写时间戳 失败
        // 超过x时间一定掉线
    }

    override fun onDispose() {
        super.onDispose()
        rtcMaster.clear()
        cancelCaseJob()
    }

    fun onSelected(bleDeviceVo: BleDeviceVo) {
        this.mutableState.value = this.state.value.copy(
            connecting = BleMasterConnectedStatus.Disconnected(bleDeviceVo.asBleDevice())
        )
    }

    fun onStartConnected() {
        val connecting = state.value.connecting
        if (connecting == BleMasterConnectedStatus.Idle) {
            w("请选择一个设备")
        }
        when (connecting) {
            is BleMasterConnectedStatus.Connected -> {
                w("设备已连接")
            }

            is BleMasterConnectedStatus.Connecting -> {
                w("设备连接中...")
            }

            is BleMasterConnectedStatus.Disconnected -> {
                rtcMaster.connect(connecting.device)
            }

            is BleMasterConnectedStatus.Disconnecting -> {
                w("设备断开中...")
            }

            BleMasterConnectedStatus.Idle -> {
                w("请选择一个设备")
            }
        }

    }

    fun onTestCase(rtcTestCase: RTCTestCase) {
        when (rtcTestCase) {
            RTCTestCase.C1 -> doCase01()
            RTCTestCase.C2 -> doCase02()
            is RTCTestCase.C3 -> doCase03(rtcTestCase.key)
            is RTCTestCase.C4 -> doCase04(rtcTestCase.key)
        }

    }

    private var case01Job: Job? = null
    private var case02Job: Job? = null
    private var case03Job: Job? = null
    private var case04Job: Job? = null

    private var connectChannel: Channel<Boolean>? = null
    private fun doCase01() {
        cancelCaseJob()
        case01Job = screenModelScope.launch {
            val connecting = state.value.connecting
            if (connecting == BleMasterConnectedStatus.Idle) {
                w("请选择一个设备")
            }
            when (connecting) {
                is BleMasterConnectedStatus.Connected -> {
                    w("设备已连接")
                }

                is BleMasterConnectedStatus.Connecting -> {
                    w("设备连接中...")
                }

                is BleMasterConnectedStatus.Disconnected -> {
                    refreshCurrentTestCase( RTCTestCase.C1)
                    refreshTestcase01EffectList(null)
                    refreshTestcase01Effect(TestCaseEffect.Start(RTCTestCase.C1.desc + " 开始！"))
                    connectChannel = Channel(1)
                    refreshTestcase01Effect(TestCaseEffect.Progress(RTCTestCase.C1.desc + "开始连接设备！"))
                    rtcMaster.connect(connecting.device)
                    val receive = connectChannel?.receive()
                    if (receive == true) {
                        val msg0 = "连接成功"
                        i(msg0)
                        refreshTestcase01Effect(TestCaseEffect.Progress(msg0))
                        launch {
                            val msg1 = "开始超时任务,超时时间：$authTimeout ms"
                            i(msg1)
                            refreshTestcase01Effect(TestCaseEffect.Progress(msg1))
                            delay(authTimeout)
                            val msg2 =
                                "${authTimeout}秒后检查连接状态 ${state.value.connecting.display}"
                            i(msg2)
                            refreshTestcase01Effect(TestCaseEffect.Progress(msg2))
                            if (state.value.connecting.connected) {
                                val msg3 = "${RTCTestCase.C1.desc} 测试不通过!"
                                e(msg3)
                                refreshTestcase01Effect(TestCaseEffect.Fail(msg3))
                            } else {
                                val msg3 = "${RTCTestCase.C1.desc} 除非意外断开外，测试通过!"
                                i(msg3)
                                refreshTestcase01Effect(TestCaseEffect.Success(msg3))
                            }
                        }
                        launch {
                            var time = authTimeout
                            while (isActive && time > 0) {
                                val msg = "计时中...${time}ms"
                                i(msg)
                                refreshTestcase01Effect(TestCaseEffect.Progress(msg))
                                delay(1000)
                                time -= 1000
                            }
                        }
                    }
                    connectChannel = null

                }

                is BleMasterConnectedStatus.Disconnecting -> {
                    w("设备断开中...")
                }

                BleMasterConnectedStatus.Idle -> {
                    w("请选择一个设备")
                }
            }
        }
    }

    private fun doCase02() {
        cancelCaseJob()
        case02Job = screenModelScope.launch {

            val connecting = state.value.connecting
            if (connecting == BleMasterConnectedStatus.Idle) {
                w("请选择一个设备")
            }
            when (connecting) {
                is BleMasterConnectedStatus.Connected -> {
                    w("设备已连接")
                }

                is BleMasterConnectedStatus.Connecting -> {
                    w("设备连接中...")
                }

                is BleMasterConnectedStatus.Disconnected -> {
                    refreshCurrentTestCase( RTCTestCase.C2)
                    refreshTestcase02EffectList(null)
                    refreshTestcase02Effect(TestCaseEffect.Start(RTCTestCase.C2.desc + " 开始！"))
                    connectChannel = Channel(1)
                    refreshTestcase02Effect(TestCaseEffect.Progress(RTCTestCase.C2.desc + "开始连接设备！"))
                    rtcMaster.connect(connecting.device)
                    val receive = connectChannel?.receive()
                    if (receive == true) {
                        val msg0 = "连接成功"
                        i(msg0)
                        refreshTestcase02Effect(TestCaseEffect.Progress(msg0))
                        delay(1000)
                        val serviceAndCharacteristics =
                            rtcMaster.serviceAndCharacteristics.value ?: run {
                                val m = "没有write的Service Characteristics信息"
                                i(m)
                                refreshTestcase02Effect(TestCaseEffect.Fail(msg0))
                                return@launch
                            }
                        // 写属性 写时间戳
                        testWrite02(serviceAndCharacteristics)

//                        launch {
//                            val msg1 = "开始超时任务,超时时间：$authTimeout ms"
//                            i(msg1)
//                            refreshTestcase02Effect(TestCaseEffect.Progress(msg1))
//                            delay(authTimeout)
//                            val msg2 =
//                                "${authTimeout}秒后检查连接状态 ${state.value.connecting.display}"
//                            i(msg2)
//                            refreshTestcase02Effect(TestCaseEffect.Progress(msg2))
//                            if (state.value.connecting.connected) {
//                                val msg3 = "${RTCTestCase.C1.desc} 测试不通过!"
//                                e(msg3)
//                                refreshTestcase02Effect(TestCaseEffect.Fail(msg3))
//                            } else {
//                                val msg3 = "${RTCTestCase.C1.desc} 除非意外断开外，测试通过!"
//                                i(msg3)
//                                refreshTestcase02Effect(TestCaseEffect.Success(msg3))
//                            }
//                        }
//                        launch {
//                            var time = authTimeout
//                            while (isActive && time > 0) {
//                                val msg = "计时中...${time}ms"
//                                i(msg)
//                                refreshTestcase02Effect(TestCaseEffect.Progress(msg))
//                                delay(1000)
//                                time -= 1000
//                            }
//                        }
                    }
                    connectChannel = null

                }

                is BleMasterConnectedStatus.Disconnecting -> {
                    w("设备断开中...")
                }

                BleMasterConnectedStatus.Idle -> {
                    w("请选择一个设备")
                }
            }
        }
    }

    private fun CoroutineScope.testWrite02(serviceAndCharacteristics: ServiceAndCharacteristics) {
        launch {
            var index = 0
            while (index <= 3) {
                delay(1000)
                refreshTestcase02Effect {
                    val m = "[设置属性测试$index]"
                    d(m)
                    TestCaseEffect.Progress(m)
                }
                val parameterKey = ParameterKey.entries.random()
                val parameterValue = "01"
                refreshTestcase02Effect {
                    val m =
                        "[设置属性测试$index]准备发送数据 key:$parameterKey value:$parameterValue"
                    d(m)
                    TestCaseEffect.Progress(m)
                }
                val setPropertyResult = rtcMaster.setPropertyV2(
                    serviceAndCharacteristics.service.uuid,
                    serviceAndCharacteristics.write.uuid,
                    parameterKey.name, parameterValue
                )

                if (setPropertyResult) {
                    refreshTestcase02Effect {
                        val m = "[设置属性测试$index] 未鉴权设置属性成功 测试不通过"
                        d(m)
                        TestCaseEffect.Fail(m)
                    }
                } else {
                    refreshTestcase02Effect {
                        val m = "[设置属性测试$index]未鉴权设置属性失败 测试通过"
                        d(m)
                        TestCaseEffect.Fail(m)
                    }
                }
                index++
            }

            var indexTimestamp = 0
            while (indexTimestamp <= 3) {
                delay(1000)
                refreshTestcase02Effect {
                    val m = "[设置时间戳测试$indexTimestamp]"
                    d(m)
                    TestCaseEffect.Progress(m)
                }
                val timestamp = currentTime()
                refreshTestcase02Effect {
                    val m =
                        "[设置时间戳测试$indexTimestamp]准备发送数据 timestamp:$timestamp"
                    d(m)
                    TestCaseEffect.Progress(m)
                }
                val setPropertyResult = rtcMaster.setTimeStamp(
                    serviceAndCharacteristics.service.uuid,
                    serviceAndCharacteristics.write.uuid,
                    timestamp
                )
                if (setPropertyResult) {
                    refreshTestcase02Effect {
                        val m =
                            "[设置时间戳测试$indexTimestamp] 未鉴权设置属性成功 测试不通过"
                        d(m)
                        TestCaseEffect.Fail(m)
                    }
                } else {
                    refreshTestcase02Effect {
                        val m =
                            "[设置时间戳测试$indexTimestamp]未鉴权设置属性失败 测试通过"
                        d(m)
                        TestCaseEffect.Fail(m)
                    }
                }
                indexTimestamp++
            }
        }
    }

    private fun CoroutineScope.testWrite04(serviceAndCharacteristics: ServiceAndCharacteristics) {
        launch {
            var index = 0
            while (index <= 3) {
                delay(1000)
                refreshTestcase04Effect {
                    val m = "[设置属性测试$index]"
                    d(m)
                    TestCaseEffect.Progress(m)
                }
                val parameterKey = ParameterKey.entries.random()
                val parameterValue = "01"
                refreshTestcase04Effect {
                    val m =
                        "[设置属性测试$index]准备发送数据 key:$parameterKey value:$parameterValue"
                    d(m)
                    TestCaseEffect.Progress(m)
                }
                val setPropertyResult = rtcMaster.setPropertyV2(
                    serviceAndCharacteristics.service.uuid,
                    serviceAndCharacteristics.write.uuid,
                    parameterKey.name, parameterValue
                )

                if (setPropertyResult) {
                    refreshTestcase04Effect {
                        val m = "[设置属性测试$index] 鉴权设置属性成功 测试通过"
                        d(m)
                        TestCaseEffect.Success(m)
                    }
                } else {
                    refreshTestcase04Effect {
                        val m = "[设置属性测试$index] 鉴权设置属性失败 测试不通过"
                        d(m)
                        TestCaseEffect.Fail(m)
                    }
                }
                index++
            }

            var indexTimestamp = 0
            while (indexTimestamp <= 3) {
                delay(1000)
                refreshTestcase04Effect {
                    val m = "[设置时间戳测试$indexTimestamp]"
                    d(m)
                    TestCaseEffect.Progress(m)
                }
                val timestamp = currentTime()
                refreshTestcase04Effect {
                    val m =
                        "[设置时间戳测试$indexTimestamp]准备发送数据 timestamp:$timestamp"
                    d(m)
                    TestCaseEffect.Progress(m)
                }
                val setPropertyResult = rtcMaster.setTimeStamp(
                    serviceAndCharacteristics.service.uuid,
                    serviceAndCharacteristics.write.uuid,
                    timestamp
                )
                if (setPropertyResult) {
                    refreshTestcase04Effect {
                        val m =
                            "[设置时间戳测试$indexTimestamp] 鉴权设置属性成功 测试通过"
                        d(m)
                        TestCaseEffect.Success(m)
                    }
                } else {
                    refreshTestcase04Effect {
                        val m =
                            "[设置时间戳测试$indexTimestamp]鉴权设置属性失败 测试不通过"
                        d(m)
                        TestCaseEffect.Fail(m)
                    }
                }
                indexTimestamp++
            }
        }
    }

    private fun doCase03(key: String) {
        cancelCaseJob()
        case03Job = screenModelScope.launch {
            val connecting = state.value.connecting
            if (connecting == BleMasterConnectedStatus.Idle) {
                w("请选择一个设备")
            }
            when (connecting) {
                is BleMasterConnectedStatus.Connected -> {
                    w("设备已连接")
                }

                is BleMasterConnectedStatus.Connecting -> {
                    w("设备连接中...")
                }

                is BleMasterConnectedStatus.Disconnected -> {
                    val case = RTCTestCase.C3(key)
                    refreshCurrentTestCase(case)
                    refreshTestcase03EffectList(null)
                    refreshTestcase03Effect(TestCaseEffect.Start(case.desc + " 开始！"))
                    connectChannel = Channel(1)
                    refreshTestcase03Effect(TestCaseEffect.Progress(case.desc + "开始连接设备！"))
                    rtcMaster.connect(connecting.device)
                    val receive = connectChannel?.receive()
                    connectChannel = null
                    if (receive == true) {
                        delay(1000)
                        val serviceAndCharacteristics =
                            rtcMaster.serviceAndCharacteristics.value ?: run {
                                val msg0 = "没有write的Service Characteristics信息"
                                i(msg0)
                                refreshTestcase03Effect(TestCaseEffect.Fail(msg0))
                                return@launch
                            }
                        refreshTestcase03Effect {
                            val msg = "enable notify!notify信息："
                            d(msg)
                            TestCaseEffect.Progress(msg)
                        }
                        refreshTestcase03Effect {
                            val msg =
                                "notify信息 serviceUUID:${serviceAndCharacteristics.service.uuid}"
                            d(msg)
                            TestCaseEffect.Progress(msg)
                        }
                        refreshTestcase03Effect {
                            val msg =
                                "notify信息 CharacteristicsUUID:${serviceAndCharacteristics.notify.uuid}"
                            d(msg)
                            TestCaseEffect.Progress(msg)
                        }
                        val enableNotifyResult = rtcMaster.enableNotifyV2(
                            serviceAndCharacteristics.service.uuid,
                            serviceAndCharacteristics.notify.uuid
                        )
                        if (!enableNotifyResult) {
                            val msg0 = "enable notify失败"
                            i(msg0)
                            refreshTestcase03Effect(TestCaseEffect.Fail(msg0))
                            return@launch
                        }


                        val realKey = key.ifEmpty { RTC_ACCESS_KEY }
                        val msg = "连接成功，开始鉴权！key = $realKey "
                        refreshTestcase03Effect(TestCaseEffect.Progress(msg))
                        val msg6 = "鉴权信息serviceUUID = ${serviceAndCharacteristics.service.uuid}"
                        val msg7 =
                            "鉴权信息CharacteristicsUUID = ${serviceAndCharacteristics.write.uuid}"
                        i(msg6)
                        i(msg7)
                        refreshTestcase03Effect(TestCaseEffect.Progress(msg6))
                        refreshTestcase03Effect(TestCaseEffect.Progress(msg7))
                        val auth = rtcMaster.auth(
                            realKey,
                            serviceAndCharacteristics.service.uuid,
                            serviceAndCharacteristics.write.uuid
                        )
                        refreshAuth(auth)
                        if (auth) {
                            val msg5 = "鉴权成功！key=$realKey"
                            i(msg5)
                            refreshTestcase03Effect(TestCaseEffect.Progress(msg5))
                            launch {
                                val msg1 = "连接成功，开始超时任务,超时时间：$authTimeout ms"
                                i(msg1)
                                refreshTestcase03Effect(TestCaseEffect.Progress(msg1))
                                delay(authTimeout)
                                val msg2 =
                                    "${authTimeout}秒后检查连接状态 ${state.value.connecting.display}"
                                i(msg2)
                                refreshTestcase03Effect(TestCaseEffect.Progress(msg2))
                                if (state.value.connecting.connected) {
                                    val msg3 = "${case.desc} 测试通过!"
                                    e(msg3)
                                    refreshTestcase03Effect(TestCaseEffect.Success(msg3))
                                } else {
                                    val msg3 = "${case.desc} 除非意外断开外，测试不通过!"
                                    i(msg3)
                                    refreshTestcase03Effect(TestCaseEffect.Fail(msg3))
                                }
                            }

                            launch {
                                var time = authTimeout
                                while (isActive && time > 0) {
                                    val msg = "计时中...${time}ms"
                                    i(msg)
                                    refreshTestcase03Effect(TestCaseEffect.Progress(msg))
                                    delay(1000)
                                    time -= 1000
                                }
                            }
                        } else {
                            val msg5 = "鉴权失败"
                            e(msg5)
                            refreshTestcase03Effect(TestCaseEffect.Fail(msg5))
                        }
                    }


                }

                is BleMasterConnectedStatus.Disconnecting -> {
                    w("设备断开中...")
                }

                BleMasterConnectedStatus.Idle -> {
                    w("请选择一个设备")
                }
            }
        }
    }

    private fun doCase04(key: String) {
        cancelCaseJob()
        case04Job = screenModelScope.launch {

            val connecting = state.value.connecting
            if (connecting == BleMasterConnectedStatus.Idle) {
                w("请选择一个设备")
            }
            when (connecting) {
                is BleMasterConnectedStatus.Connected -> {
                    w("设备已连接")
                }

                is BleMasterConnectedStatus.Connecting -> {
                    w("设备连接中...")
                }

                is BleMasterConnectedStatus.Disconnected -> {
                    val case = RTCTestCase.C4(key)
                    refreshCurrentTestCase( case)
                    refreshTestcase04EffectList(null)
                    refreshTestcase04Effect(TestCaseEffect.Start(case.desc + " 开始！"))
                    connectChannel = Channel(1)
                    refreshTestcase04Effect(TestCaseEffect.Progress(case.desc + "开始连接设备！"))
                    rtcMaster.connect(connecting.device)
                    val receive = connectChannel?.receive()
                    connectChannel = null
                    if (receive == true) {
                        delay(1000)
                        val serviceAndCharacteristics =
                            rtcMaster.serviceAndCharacteristics.value ?: run {
                                val msg0 = "没有write的Service Characteristics信息"
                                i(msg0)
                                refreshTestcase04Effect(TestCaseEffect.Fail(msg0))
                                return@launch
                            }
                        refreshTestcase04Effect {
                            val msg = "enable notify!notify信息："
                            d(msg)
                            TestCaseEffect.Progress(msg)
                        }
                        refreshTestcase04Effect {
                            val msg =
                                "notify信息 serviceUUID:${serviceAndCharacteristics.service.uuid}"
                            d(msg)
                            TestCaseEffect.Progress(msg)
                        }
                        refreshTestcase04Effect {
                            val msg =
                                "notify信息 CharacteristicsUUID:${serviceAndCharacteristics.notify.uuid}"
                            d(msg)
                            TestCaseEffect.Progress(msg)
                        }
                        val enableNotifyResult = rtcMaster.enableNotifyV2(
                            serviceAndCharacteristics.service.uuid,
                            serviceAndCharacteristics.notify.uuid
                        )
                        if (!enableNotifyResult) {
                            val msg0 = "enable notify失败"
                            i(msg0)
                            refreshTestcase04Effect(TestCaseEffect.Fail(msg0))
                            return@launch
                        }


                        val realKey = key.ifEmpty { RTC_ACCESS_KEY }
                        val msg = "连接成功，开始鉴权！key = $realKey "
                        refreshTestcase04Effect(TestCaseEffect.Progress(msg))
                        val msg6 = "鉴权信息serviceUUID = ${serviceAndCharacteristics.service.uuid}"
                        val msg7 =
                            "鉴权信息CharacteristicsUUID = ${serviceAndCharacteristics.write.uuid}"
                        i(msg6)
                        i(msg7)
                        refreshTestcase04Effect(TestCaseEffect.Progress(msg6))
                        refreshTestcase04Effect(TestCaseEffect.Progress(msg7))
                        val auth = rtcMaster.auth(
                            realKey,
                            serviceAndCharacteristics.service.uuid,
                            serviceAndCharacteristics.write.uuid
                        )
                        refreshAuth(auth)
                        if (auth) {
                            val msg5 = "鉴权成功！key=$realKey"
                            i(msg5)
                            refreshTestcase04Effect(TestCaseEffect.Progress(msg5))
//                            launch {
//                                val msg1 = "连接成功，开始超时任务,超时时间：$authTimeout ms"
//                                i(msg1)
//                                refreshTestcase03Effect(TestCaseEffect.Progress(msg1))
//                                delay(authTimeout)
//                                val msg2 =
//                                    "${authTimeout}秒后检查连接状态 ${state.value.connecting.display}"
//                                i(msg2)
//                                refreshTestcase03Effect(TestCaseEffect.Progress(msg2))
//                                if (state.value.connecting.connected) {
//                                    val msg3 = "${RTCTestCase.C1.desc} 测试通过!"
//                                    e(msg3)
//                                    refreshTestcase03Effect(TestCaseEffect.Success(msg3))
//                                } else {
//                                    val msg3 = "${RTCTestCase.C1.desc} 除非意外断开外，测试不通过!"
//                                    i(msg3)
//                                    refreshTestcase03Effect(TestCaseEffect.Fail(msg3))
//                                }
//                            }

                            // 写属性 写时间戳
                            testWrite04(serviceAndCharacteristics)

                        } else {
                            val msg5 = "鉴权失败"
                            e(msg5)
                            refreshTestcase04Effect(TestCaseEffect.Fail(msg5))
                        }
                    }


                }

                is BleMasterConnectedStatus.Disconnecting -> {
                    w("设备断开中...")
                }

                BleMasterConnectedStatus.Idle -> {
                    w("请选择一个设备")
                }
            }
        }
    }

    private fun refreshTestcase01Effect(effect: TestCaseEffect) {
        mutableState.value = state.value.copy(
            testCase01Effect = effect
        )
        refreshTestcase01EffectList(effect)
    }

    private fun refreshTestcase01EffectList(effect: TestCaseEffect?) {
//        val newList = if (effect == null) emptyList() else state.value.testCase01EffectList + effect
//        mutableState.value = state.value.copy(
//            testCase01EffectList = newList
//        )
        refreshCommonCaseList(effect)
    }

    private fun refreshTestcase02Effect(effect: TestCaseEffect) {
        mutableState.value = state.value.copy(
            testCase02Effect = effect
        )
        refreshTestcase02EffectList(effect)
    }

    private fun refreshTestcase02Effect(block: () -> TestCaseEffect) {
        val effect = block()
        mutableState.value = state.value.copy(
            testCase02Effect = effect
        )
        refreshTestcase02EffectList(effect)
    }

    private fun refreshTestcase04Effect(block: () -> TestCaseEffect) {
        val effect = block()
        mutableState.value = state.value.copy(
            testCase04Effect = effect
        )
        refreshTestcase04EffectList(effect)
    }

    private fun refreshTestcase02EffectList(effect: TestCaseEffect?) {
//        val newList = if (effect == null) emptyList() else state.value.testCase02EffectList + effect
//        mutableState.value = state.value.copy(
//            testCase02EffectList = newList
//        )
        refreshCommonCaseList(effect)
    }

    private fun refreshTestcase03EffectList(effect: TestCaseEffect?) {
//        val newList = if (effect == null) emptyList() else state.value.testCase03EffectList + effect
//        mutableState.value = state.value.copy(
//            testCase03EffectList = newList
//        )
        refreshCommonCaseList(effect)
    }


    private fun refreshAuth(auth: Boolean) {
        mutableState.value = state.value.copy(
            auth = auth
        )
    }

    private fun refreshTestcase03Effect(effect: TestCaseEffect) {
        mutableState.value = state.value.copy(
            testCase03Effect = effect
        )
        refreshTestcase03EffectList(effect)
    }

    private fun refreshTestcase03Effect(block: () -> TestCaseEffect) {
        val effect = block()
        mutableState.value = state.value.copy(
            testCase03Effect = effect
        )
        refreshTestcase03EffectList(effect)

    }

    private fun refreshTestcase04Effect(effect: TestCaseEffect) {
        mutableState.value = state.value.copy(
            testCase04Effect = effect
        )
        refreshTestcase04EffectList(effect)
    }

    private fun refreshTestcase04EffectList(effect: TestCaseEffect?) {
//        val newList = if (effect == null) emptyList() else state.value.testCase04EffectList + effect
//        mutableState.value = state.value.copy(
//            testCase04EffectList = newList
//        )
        refreshCommonCaseList(effect)
    }

    private fun refreshCommonCaseList(effect: TestCaseEffect?) {
        val newList = if (effect == null) emptyList() else state.value.commonCaseEffectList + effect
        mutableState.value = state.value.copy(
            commonCaseEffectList = newList
        )
    }

    private fun refreshCurrentTestCase(case:RTCTestCase?) {
        mutableState.value = state.value.copy(
            currentTestCase = case
        )
    }

    private fun cancelCaseJob() {
        refreshCommonCaseList(null)
        refreshCurrentTestCase(null)
        case01Job?.cancel()
        case02Job?.cancel()
        case03Job?.cancel()
        case04Job?.cancel()
        case01Job = null
        case02Job = null
        case03Job = null
        case04Job = null
        connectChannel?.cancel()
    }
}