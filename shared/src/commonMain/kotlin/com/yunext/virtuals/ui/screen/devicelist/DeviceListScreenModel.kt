package com.yunext.virtuals.ui.screen.devicelist

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.module.devicemanager.DeviceStoreWrapper
import com.yunext.virtuals.module.devicemanager.deviceManager
import com.yunext.virtuals.module.usecase.DeviceUseCase
import com.yunext.virtuals.ui.Effect
import com.yunext.virtuals.ui.common.HDStateScreenModel
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.DeviceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class DeviceListState(
    val list: List<DeviceAndStateViewData>,
    val effect: Effect,
)

internal val DeviceListStateDefault by lazy {
    DeviceListState(list = emptyList(), Effect.Idle)
}

class DeviceListScreenModel(initialState: DeviceListState = DeviceListStateDefault) :
    HDStateScreenModel<DeviceListState>(initialState) {

    private val deviceUseCase = DeviceUseCase.INSTANCE

    // todo mqtt整合信息
    init {
        deviceManager.deviceStoreMapStateFlow
            .onEach { map ->
                HDLogger.d("DeviceListScreenModel", "设备状态更新${map.size}")
                val oldValue = state.value
                merge(oldValue.list,map)
            }.onCompletion {

            }.launchIn(screenModelScope)
    }

    private fun merge(list: List<DeviceAndStateViewData>,map:Map<String,DeviceStoreWrapper>) {

        //HDLogger.d("DeviceListScreenModel","开始：${oldValue.list.joinToString ("\n"){ it.toString() }}")
        val newList = list.map { device ->
            val temp = map.filter { (k, v) ->
                device.communicationId == k
            }.values.singleOrNull()
            //HDLogger.d("DeviceListScreenModel","temp:${temp?.deviceStore?.isConnect()} ,device：${temp?.deviceStore?.device}")
            if (temp != null) {
                device.copy(status = if (temp.deviceStore.isConnected()) DeviceStatus.WiFiOnLine else DeviceStatus.WiFiOffLine)
            } else device

        }
        //HDLogger.d("DeviceListScreenModel","结果：${newList.joinToString ("\n"){ it.toString() }}")

        mutableState.update {
            state.value.copy(list = newList)
        }
    }

    fun doGetAllDevice() {
        HDLogger.d("DeviceListScreenModel","::doGetAllDevice")
        screenModelScope.launch {
            // mutableState.value = state.value.copy(effect = Effect.Processing) // TODO 会导致页面闪一下
            try {
                val list = withContext(Dispatchers.IO) { deviceUseCase.list() }
                 merge(list, deviceManager.deviceStoreMapStateFlow.value)
                // 整合deviceManager
            } catch (e: Throwable) {
                mutableState.value = state.value.copy(effect = Effect.Fail(e))
            } finally {
                mutableState.value = state.value.copy(effect = Effect.Idle)
            }
        }
    }

    fun doDisconnectDevice(deviceAndState: DeviceAndStateViewData) {
        screenModelScope.launch {
           deviceManager.find(deviceAndState.communicationId)?.disconnect()
        }
    }
    fun doDeleteDevice(deviceAndState: DeviceAndStateViewData) {
        screenModelScope.launch {
            mutableState.value = state.value.copy(effect = Effect.Processing)
            try {
                val finalList = withContext(Dispatchers.IO) {
                    val result = deviceUseCase.delete(deviceAndState.communicationId)
                    HDLogger.d("DeviceListScreenModel", "doDeleteDevice:$result")
                    if (result) {
                        deviceUseCase.list()
                    } else state.value.list
                }
                HDLogger.d("DeviceListScreenModel", "doDeleteDevice:$finalList")
                mutableState.value = state.value.copy(list = finalList)

            } catch (e: Throwable) {
                HDLogger.d("DeviceListScreenModel", "doDeleteDevice:$e")
                mutableState.value = state.value.copy(effect = Effect.Fail(e))
            } finally {
                mutableState.value = state.value.copy(effect = Effect.Idle)
            }
        }
    }
}