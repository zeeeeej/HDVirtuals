package com.yunext.virtuals.module.devicemanager

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.hdContext
import com.yunext.virtuals.data.ProjectInfo
import com.yunext.virtuals.data.device.MQTTDevice
import com.yunext.virtuals.data.device.TwinsDevice
import com.yunext.virtuals.module.repository.DBDeviceRepositoryImpl
import com.yunext.virtuals.module.repository.DeviceRepository
import com.yunext.virtuals.module.repository.MemoryDeviceRepositoryImpl
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Deprecated("delete", ReplaceWith("this.find(id)?.block()"))
suspend fun MQTTDeviceManager.suspendInvokeDeviceStoreWithId(
    id: String,
    block: suspend DeviceStore.() -> Unit,
) {
    this.find(id)?.block()
}

lateinit var deviceManager: MQTTDeviceManager

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
        deviceRepository = MemoryDeviceRepositoryImpl()
    )
}

class MQTTDeviceManager constructor(
    private val context: HDContext,
    private val projectInfo: ProjectInfo,
//    private val logRepository: LogRepository,
    private val deviceRepository: DeviceRepository,
//    private val reportRepository: ReportRepository,
) {
    private val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("MQTTDeviceManager"))

    private val deviceStoreMapStateFlowInternal: MutableStateFlow<Map<String, DeviceStore>> =
        MutableStateFlow(mapOf())
    val deviceStoreMapStateFlow = deviceStoreMapStateFlowInternal.asStateFlow()

    fun init() {
        ld("::init")
    }

    fun add(device: MQTTDevice) {
        if (device !is TwinsDevice) {
            error("当前device:${device::class},暂只支持TwinsDevice设备 ")
        }
        val id = device.generateId()
        val storeMap = deviceStoreMapStateFlowInternal.value
        ld("::add size = ${storeMap.size}")
        if (storeMap.containsKey(id)) {
            return
        }
        val deviceStore = DeviceStore(
            hdContext,
            projectInfo,
            device,
            coroutineScope,
        )
        val editMap = storeMap.toMutableMap()
        editMap[id] = deviceStore
        deviceStoreMapStateFlowInternal.value = editMap
        ld("::add end size = ${storeMap.size}")
        // 开始连接
        deviceStore.connect()
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
        deviceStore.disconnect()
        deviceStore.clear()
        ld("::delete end size = ${deviceStoreMapStateFlowInternal.value.size}")
    }

    fun find(deviceId: String): DeviceStore? {
        val storeMap = deviceStoreMapStateFlowInternal.value
        return storeMap[deviceId]
    }

    fun clear() {
        val storeMap = deviceStoreMapStateFlowInternal.value
        if (storeMap.isEmpty()) return
        val iterator = storeMap.entries.iterator()
        while (iterator.hasNext()) {
            val (_, v) = iterator.next()
            v.disconnect()
            v.clear()
        }
    }

    companion object {
        private const val TAG = "_MQTTDeviceManager_"
        private fun ld(msg: String) {
            HDLogger.d(TAG, msg)
        }
    }
}

