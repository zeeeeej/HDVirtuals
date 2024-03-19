package com.yunext.virtuals.ui.screen.devicedetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.core.HDMqttClient
import com.yunext.kmp.mqtt.createHdMqttClient
import com.yunext.virtuals.ui.common.HDStateScreenModel
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.Effect
import kotlinx.coroutines.Job
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
                loadDataInternal(device)
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {

            }
        }
    }

    private var hdMqttClient: HDMqttClient? = null
    private suspend fun loadDataInternal(device: DeviceAndStateViewData) {
        val client = createHdMqttClient()
        client.init()
        hdMqttClient = client
    }


    override fun onDispose() {
        super.onDispose()
        hdMqttClient?.clear()
        HDLogger.d(TAG, "=========================== 退出设备详情 ===========================")
    }

}
