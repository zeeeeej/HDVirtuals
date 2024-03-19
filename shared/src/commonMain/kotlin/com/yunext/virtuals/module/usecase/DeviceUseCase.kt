package com.yunext.virtuals.module.usecase

import com.yunext.virtuals.module.repository.DeviceDTO
import com.yunext.virtuals.module.repository.DeviceRepository
import com.yunext.virtuals.module.repository.delete
import com.yunext.virtuals.module.toDeviceAndState
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.data.DeviceType

internal class DeviceUseCase {
    private val deviceRepository by lazy { DeviceRepository }
    suspend fun addDevice(
        deviceName: String,
        deviceType: DeviceType,
        deviceCommunicationId: String,
        deviceModel: String,
    ): Boolean {
        return deviceRepository.add(
            DeviceDTO(
                deviceName,
                deviceType,
                deviceCommunicationId,
                deviceModel
            )
        )
    }

    suspend fun delete(deviceCommunicationId: String): Boolean {
        return deviceRepository.delete(communicationId = deviceCommunicationId)
    }

    suspend fun edit(deviceDTO: DeviceDTO): Boolean {
        return deviceRepository.edit(deviceDTO)
    }

    suspend fun list(): List<DeviceAndStateViewData> {
        return deviceRepository.list().map(DeviceDTO::toDeviceAndState)
    }

    suspend fun clear(): Boolean {
        return deviceRepository.clear()
    }

    companion object{
        val INSTANCE : DeviceUseCase by lazy {
            DeviceUseCase()
        }
    }
}