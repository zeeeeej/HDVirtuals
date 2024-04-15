package com.yunext.virtuals.ui.screen.adddevice

import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.virtuals.module.usecase.DeviceUseCase
import com.yunext.virtuals.ui.common.HDStateScreenModel
import com.yunext.virtuals.ui.data.DeviceType
import com.yunext.virtuals.ui.Effect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class AddDeviceState(
    val addResult: Boolean,
    val effect: Effect,
)

internal val DefaultState by lazy {
    AddDeviceState(false, Effect.Idle)
}

class AddDeviceScreenModel(initialState: AddDeviceState = DefaultState) :
    HDStateScreenModel<AddDeviceState>(initialState) {

    private val deviceUseCase = DeviceUseCase.INSTANCE

    fun doAddDevice(
        deviceName: String,
        deviceType: DeviceType,
        deviceCommunicationId: String,
        deviceModel: String,
    ) {
        screenModelScope.launch {
            mutableState.value = state.value.copy(effect = Effect.Processing)
            try {
                val result = withContext(Dispatchers.IO) {
                    deviceUseCase.addDevice(
                        deviceName,
                        deviceType,
                        deviceCommunicationId,
                        deviceModel
                    )
                }
                delay(Random.nextLong(2000) + 1000) // mock
                mutableState.value = state.value.copy(
                    effect =
                    if (result) Effect.Success else Effect.Fail(
                        IllegalStateException("添加失败")
                    ), addResult = result
                )

            } catch (e: Throwable) {
                mutableState.value = state.value.copy(effect = Effect.Fail(e))
            } finally {
                mutableState.value = state.value.copy(effect = Effect.Idle)
            }
        }
    }
}