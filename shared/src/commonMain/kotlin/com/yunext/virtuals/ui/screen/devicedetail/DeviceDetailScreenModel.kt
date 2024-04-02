package com.yunext.virtuals.ui.screen.devicedetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.data.device.TwinsDevice
import com.yunext.virtuals.data.device.UnSupportDeviceException
import com.yunext.virtuals.module.devicemanager.deviceManager
import com.yunext.virtuals.module.devicemanager.filterOrNull
import com.yunext.virtuals.module.toDeviceDTO
import com.yunext.virtuals.module.toMqttDevice
import com.yunext.virtuals.ui.common.HDStateScreenModel
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.DeviceStatus
import com.yunext.virtuals.ui.data.DeviceType
import com.yunext.virtuals.ui.data.Effect
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
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
        loadDataJob = screenModelScope.launch {
            try {
                val device = state.value.device
                initMqtt(device)
                loadDataInternal(device)
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {

            }
        }
    }

    private fun initMqtt(device: DeviceAndStateViewData) {
        HDLogger.d(TAG, "::initMqtt")
        screenModelScope.launch {
            deviceManager.deviceStoreMapStateFlow.value
                .filterOrNull(device.communicationId)
                ?.deviceStateHolderFlow?.collect { holder ->
                    try {
                        mutableState.update { oldState ->
                            val changedDevice =
                                (holder.device as? TwinsDevice) ?: throw UnSupportDeviceException(
                                    holder.device::class
                                )
                            oldState.copy(
                                device = DeviceAndStateViewData(
                                    name = device.name,
                                    communicationId = changedDevice.generateId(),
                                    model = changedDevice.deviceType,
                                    status = when (changedDevice.communicationType) {
                                        DeviceType.WIFI -> {
                                            if (holder.connect) {
                                                DeviceStatus.WiFiOnLine
                                            } else DeviceStatus.WiFiOffLine
                                        }

                                        DeviceType.GPRS -> if (holder.connect) {
                                            DeviceStatus.GPRSOnLine
                                        } else DeviceStatus.GPRSOffLine
                                    }
                                )
                            )
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
        }
        deviceManager.add(device.toDeviceDTO().toMqttDevice())
    }

    private suspend fun loadDataInternal(device: DeviceAndStateViewData) {
        HDLogger.d(TAG, "::loadDataInternal")
    }


    override fun onDispose() {
        super.onDispose()
        HDLogger.d(TAG, "=========================== 退出设备详情 ===========================")
    }

}
