package com.yunext.virtuals.ui.screen.devicelist

import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.virtuals.module.usecase.DeviceUseCase
import com.yunext.virtuals.ui.common.HDStateScreenModel
import com.yunext.virtuals.ui.data.DeviceAndState
import com.yunext.virtuals.ui.data.DeviceType
import com.yunext.virtuals.ui.data.Effect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DeviceListState(
    val list: List<DeviceAndState>,
    val effect: Effect,
)

internal val DeviceListStateDefault by lazy {
    DeviceListState(list = emptyList(), Effect.Idle)
}

class DeviceListScreenModel(initialState: DeviceListState = DeviceListStateDefault) :
    HDStateScreenModel<DeviceListState>(initialState) {

    private val deviceUseCase = DeviceUseCase.INSTANCE

    fun doGetAllDevice() {
        screenModelScope.launch {
            mutableState.value = state.value.copy(effect = Effect.Processing)
            try {
                val list = withContext(Dispatchers.IO) { deviceUseCase.list() }
                mutableState.value = state.value.copy(list = list)
            } catch (e: Throwable) {
                mutableState.value = state.value.copy(effect = Effect.Fail(e))
            } finally {
                mutableState.value = state.value.copy(effect = Effect.Idle)
            }
        }
    }

    fun doDeleteDevice(deviceAndState: DeviceAndState) {
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