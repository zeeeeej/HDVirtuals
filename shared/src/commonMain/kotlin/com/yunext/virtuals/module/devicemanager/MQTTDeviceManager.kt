package com.yunext.virtuals.module.devicemanager

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.hdContext
import com.yunext.virtuals.data.ProjectInfo
import com.yunext.virtuals.data.device.MQTTDevice
import com.yunext.virtuals.data.device.TwinsDevice
import com.yunext.virtuals.module.repository.DeviceRepository
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@Deprecated("delete", ReplaceWith("this.find(id)?.block()"))
internal suspend fun MQTTDeviceManager.suspendInvokeDeviceStoreWithId(
    id: String,
    block: suspend IDeviceStore.() -> Unit,
) {
    this.find(id)?.block()
}

internal lateinit var deviceManager: MQTTDeviceManager

fun initDeviceManager() {
    if (::deviceManager.isInitialized) {
        return
    }
    deviceManager = MQTTDeviceManager(
        context = hdContext,
        projectInfo = ProjectInfo(
            name = "HD孪生设备(骨架代码)",
            host = "ssl://emqtt-test.yunext.com:8904",
            secret = "skeleton_2se32Hssa_2212",
            brand = "skeleton"
        ),
        deviceRepository = DeviceRepository
    )
}


internal fun Map<String, DeviceStoreWrapper>.filterOrNull(id: String): DeviceStore? {
    return this.filter { (k, v) ->
        k == id
    }.values.singleOrNull()?.deviceStore
}

internal class MQTTDeviceManager internal constructor(
    private val context: HDContext,
    private val projectInfo: ProjectInfo,
//    private val logRepository: LogRepository,
    private val deviceRepository: DeviceRepository,
//    private val reportRepository: ReportRepository,


) {
    private val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.Main + SupervisorJob() + CoroutineName("MQTTDeviceManager")

//             +   CoroutineExceptionHandler { coroutineContext, throwable ->
//                    Napier.e("MQTTDeviceManager::coroutineScope error $throwable @${coroutineContext[CoroutineName]?.name}")
//                }
        )

    private val deviceStoreMapStateFlowInternal: MutableStateFlow<Map<String, DeviceStoreWrapper>> =
        MutableStateFlow(mapOf())
    val deviceStoreMapStateFlow = deviceStoreMapStateFlowInternal.asStateFlow()

    fun init() {
        ld("::init")
    }

    fun add(device: MQTTDevice, auto: Boolean = true): DeviceStore {
        if (device !is TwinsDevice) {
            error("当前device:${device::class},暂只支持TwinsDevice设备 ")
        }
        val id = device.generateId()
        val storeMap = deviceStoreMapStateFlowInternal.value
        if (storeMap.containsKey(id)) {
            ld("已经添加了该设备,device:$device")
            val store = storeMap[id]?.deviceStore
            if (store != null && !store.isConnected()) {
                ld("已经添加了该设备,但是未连接 $device")
                store.connect()
                return store
            }
        }
        val todoMap = fixDeviceStoreWhileFull(storeMap).toMutableMap()
        ld("添加设备")
        val deviceStore = DeviceStore(
            hdContext,
            projectInfo,
            device,
            coroutineScope,
        )
        todoMap[id] = deviceStore.wrap()
        deviceStoreMapStateFlowInternal.value = todoMap
        ld("添加设备完毕:${todoMap.size}")


        val job = deviceStore.deviceStateHolderFlow
            .onEach {
                // 更新当前设备状态
                val map = deviceStoreMapStateFlowInternal.value
                val store =
                    map.filter { (k, v) -> k == (it.device as? MQTTDevice)?.generateId() }.values.singleOrNull()
                        ?: return@onEach
                val editMap = map.toMutableMap()
                editMap[id] = store.deviceStore.wrap()
                ld("$$$ 更新设备信息$it")
                this.deviceStoreMapStateFlowInternal.update {
                    editMap
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(coroutineScope)
        // todo 缓存job 删除时移除job。
        // 开始连接
        if (auto) {
            deviceStore.connect()
        }
        return deviceStore
    }

    fun delete(deviceId: String) {
        val storeMap = deviceStoreMapStateFlowInternal.value
        ld("::delete size = ${storeMap.size}")
        if (!storeMap.containsKey(deviceId)) {
            return
        }
        val deviceStore = storeMap[deviceId] ?: return
        val editMap = storeMap.toMutableMap()
        editMap.remove(deviceId)
        deviceStoreMapStateFlowInternal.value = editMap
        deviceStore.deviceStore.disconnect()
        deviceStore.deviceStore.clear()
        ld("::delete end size = ${deviceStoreMapStateFlowInternal.value.size}")
    }

    fun find(deviceId: String): DeviceStore? {
        val storeMap = deviceStoreMapStateFlowInternal.value
        return storeMap[deviceId]?.deviceStore
    }

    fun clear() {
        val storeMap = deviceStoreMapStateFlowInternal.value
        if (storeMap.isEmpty()) return
        val iterator = storeMap.entries.iterator()
        while (iterator.hasNext()) {
            val (_, v) = iterator.next()
            v.deviceStore.disconnect()
            v.deviceStore.clear()
        }
    }

    private fun fixDeviceStoreWhileFull(old: Map<String, DeviceStoreWrapper>): Map<String, DeviceStoreWrapper> {
        ld("检查设备集合 ${old.size}")
        val map = old.toMutableMap()
        if (map.isEmpty()) return emptyMap()
        while (map.size >= MAX_CONNECT_COUNT) {
            lw("添加的设备已到${MAX_CONNECT_COUNT}，准备删除最旧的设备。")
            val findOldestCreateDeviceStore = findOldestCreateDeviceStore(map)
            if (findOldestCreateDeviceStore != null) {

                findOldestCreateDeviceStore.second.deviceStore.disconnect()
                findOldestCreateDeviceStore.second.deviceStore.clear()

                lw("删除前最久的设备:${findOldestCreateDeviceStore.first}")
                lw("删除前：${map.size} ${map.keys}")
                map.remove(findOldestCreateDeviceStore.first)
                lw("删除后：${map.size} ${map.keys}")
            }
        }
        lw("检查设备集合完毕！${map.size}")
        //deviceStoreMapStateFlowInternal.update { map }
        return map
    }

    private fun findOldestCreateDeviceStore(map: Map<String, DeviceStoreWrapper>): Pair<String, DeviceStoreWrapper>? {
        if (map.isEmpty()) return null
        val item = map.minByOrNull { (k, v) ->
            v.deviceStore.createTime
        }
        return item?.let {
            it.key to it.value
        }
    }

    companion object {
        private const val TAG = "MQTTDeviceManager"
        private const val MAX_CONNECT_COUNT = 2
        private fun ld(msg: String) {
            HDLogger.d(TAG, msg)
        }

        private fun lw(msg: String) {
            HDLogger.w(TAG, msg)
        }
    }
}

