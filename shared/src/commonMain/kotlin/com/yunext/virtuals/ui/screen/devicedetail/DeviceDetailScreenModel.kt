package com.yunext.virtuals.ui.screen.devicedetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.PropertyValue
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.valueStr
import com.yunext.virtuals.data.device.TwinsDevice
import com.yunext.virtuals.data.device.UnSupportDeviceException
import com.yunext.virtuals.module.devicemanager.DeviceStore
import com.yunext.virtuals.module.devicemanager.deviceManager
import com.yunext.virtuals.module.devicemanager.filterOrNull
import com.yunext.virtuals.module.toDeviceDTO
import com.yunext.virtuals.module.toMqttDevice
import com.yunext.virtuals.module.toPropertyDataList
import com.yunext.virtuals.ui.common.HDStateScreenModel
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.DeviceStatus
import com.yunext.virtuals.ui.data.DeviceType
import com.yunext.virtuals.ui.data.Effect
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch


internal data class DeviceDetailState(
    val device: DeviceAndStateViewData,
    val effect: Effect,
)

internal class DeviceDetailScreenModel(initialState: DeviceDetailState) :
    HDStateScreenModel<DeviceDetailState>(initialState) {
    init {
        HDLogger.d(TAG, "=========================== 进入设备详情 ===========================")
        HDLogger.d(TAG, "=== 设备信息(${initialState.device.hashCode()})")
        HDLogger.d(TAG, "=== name               :   ${initialState.device.name}")
        HDLogger.d(TAG, "=== model              :   ${initialState.device.model}")
        HDLogger.d(TAG, "=== communicationId    :   ${initialState.device.communicationId}")
        HDLogger.d(TAG, "=== status             :   ${initialState.device.status}")
        HDLogger.d(TAG, "=== 加载信息...")
        loadData()

    }

    companion object {
        private const val TAG = "DeviceDetailScreen"
    }

    private var loadDataJob: Job? = null
    private fun loadData() {
        loadDataJob?.cancel()
        loadDataJob = null

        screenModelScope.launch {
            delay(5000)
        }
        loadDataJob = screenModelScope.launch() {
            try {
                val device = state.value.device
                initMqtt(device)
                delay(5000)
            } catch (e: Throwable) {
//                e.printStackTrace()
                Napier.e("${TAG}::loadData error $e")
                if (e is CancellationException) throw e
            } finally {

            }
        }
    }


    private fun initMqtt(device: DeviceAndStateViewData) {
        HDLogger.d(TAG, "::initMqtt")
        screenModelScope.launch(Dispatchers.IO) {
            val checkDeviceStore =
                deviceManager.deviceStoreMapStateFlow.value.filterOrNull(device.communicationId)
            if (checkDeviceStore == null) {
                val newDeviceStore = deviceManager.add(device.toDeviceDTO().toMqttDevice())
                registerFlow(device, newDeviceStore)
            } else {
                registerFlow(device, checkDeviceStore)
            }
        }
    }

    private fun CoroutineScope.registerFlow(
        device: DeviceAndStateViewData,
        deviceStore: DeviceStore,
    ) {
//        launch {
//            deviceStore.deviceStateHolderFlow.collect { holder ->
//                HDLogger.d(TAG, "::initMqtt changed $holder")
//                try {
//                    val changedDevice =
//                        (holder.device as? TwinsDevice) ?: throw UnSupportDeviceException(
//                            holder.device::class
//                        )
//                    val oldState = state.value
////                    val tsl = holder.tsl
////                    if (tsl == null) {
////                        Napier.w("tsl没有加载成功")
////                        return@collect
////                    }
//                    mutableState.value = oldState.copy(
//                        device = DeviceAndStateViewData(
//                            name = device.name,
//                            communicationId = changedDevice.generateId(),
//                            model = changedDevice.deviceType,
//                            status = when (changedDevice.communicationType) {
//                                DeviceType.WIFI -> {
//                                    if (holder.connect) {
//                                        DeviceStatus.WiFiOnLine
//                                    } else DeviceStatus.WiFiOffLine
//                                }
//
//                                DeviceType.GPRS -> if (holder.connect) {
//                                    DeviceStatus.GPRSOnLine
//                                } else DeviceStatus.GPRSOffLine
//                            },
//
//                            // TODO
//                            propertyList = emptyList(),
//                            eventList = emptyList(),
//                            serviceList = emptyList()
//                        ),
//                    )
//
//                } catch (e: Throwable) {
//                    //e.printStackTrace()
//                    Napier.e("DeviceDetailScreenModel::initMqtt 退出错误$e")
//                }
//            }
//        }

        launch {
            deviceStore.deviceStateHolderFlow.collectLatest { deviceHolder ->
                HDLogger.d(TAG, "::initMqtt changed $deviceHolder")
                try {
                    val changedDevice =
                        (deviceHolder.device as? TwinsDevice) ?: throw UnSupportDeviceException(
                            deviceHolder.device::class
                        )
                    val oldState = state.value
//                    val tsl = holder.tsl
//                    if (tsl == null) {
//                        Napier.w("tsl没有加载成功")
//                        return@collect
//                    }

                    Napier.w {
                        "abcdefg oldState : ${oldState}"
                    }
                    mutableState.value = oldState.copy(
                        device = DeviceAndStateViewData(
                            name = device.name,
                            communicationId = changedDevice.generateId(),
                            model = changedDevice.deviceType,
                            status = when (changedDevice.communicationType) {
                                DeviceType.WIFI -> {
                                    if (deviceHolder.connect) {
                                        DeviceStatus.WiFiOnLine
                                    } else DeviceStatus.WiFiOffLine
                                }

                                DeviceType.GPRS -> if (deviceHolder.connect) {
                                    DeviceStatus.GPRSOnLine
                                } else DeviceStatus.GPRSOffLine
                            },

                            // TODO
                            propertyList = deviceHolder.properties.toPropertyDataList(),
//                            eventList = emptyList(),
//                            serviceList = emptyList()
                        ),
                    )

                } catch (e: Throwable) {
                    //e.printStackTrace()
                    Napier.e("DeviceDetailScreenModel::initMqtt 退出错误$e")
                }
            }
        }
    }

    override fun onDispose() {
        super.onDispose()
        loadDataJob?.cancel()
        screenModelScope.cancel()
        HDLogger.d(TAG, "=========================== 退出设备详情 ===========================")
    }

    private fun tryGetDevice() = state.value.device

    fun changeProperty(propertyValue: PropertyValue<*>) {
        val device = tryGetDevice()
        val checkDeviceStore =
            deviceManager.deviceStoreMapStateFlow.value.filterOrNull(device.communicationId)
                ?: return

        checkDeviceStore.sendProperty(propertyValue)
    }

}
