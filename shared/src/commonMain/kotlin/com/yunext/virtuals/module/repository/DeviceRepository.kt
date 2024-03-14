package com.yunext.virtuals.module.repository

import com.yunext.virtuals.module.datasource.MemoryDeviceDatasource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface DeviceRepository {
    suspend fun add(deviceDTO: DeviceDTO): Boolean
    suspend fun delete(deviceDTO: DeviceDTO): Boolean

    suspend fun edit(deviceDTO: DeviceDTO): Boolean

    suspend fun list(): List<DeviceDTO>

    suspend fun clear(): Boolean

    companion object : DeviceRepository by MemoryDeviceRepositoryImpl()
}

suspend fun DeviceRepository.delete(communicationId: String) =
    this.delete(communicationIdAsDeviceDTO(communicationId))

class MemoryDeviceRepositoryImpl : DeviceRepository {
    private val memoryDeviceDatasource: MemoryDeviceDatasource by lazy {
        MemoryDeviceDatasource()
    }

    override suspend fun add(deviceDTO: DeviceDTO): Boolean {
        return suspendCancellableCoroutine {
            try {
                val result = memoryDeviceDatasource.add(deviceDTO)
                it.resume(result)
            } catch (e: Throwable) {
                it.resumeWithException(e)
            }
        }
    }

    override suspend fun delete(deviceDTO: DeviceDTO): Boolean {
        return suspendCancellableCoroutine {
            try {
                val result = memoryDeviceDatasource.delete(deviceDTO)
                it.resume(result)
            } catch (e: Throwable) {
                it.resumeWithException(e)
            }
        }
    }

    override suspend fun edit(deviceDTO: DeviceDTO): Boolean {
        return suspendCancellableCoroutine {
            try {
                val result = memoryDeviceDatasource.edit(deviceDTO)
                it.resume(result)
            } catch (e: Throwable) {
                it.resumeWithException(e)
            }
        }
    }

    override suspend fun list(): List<DeviceDTO> {
        return suspendCancellableCoroutine {
            try {
                val result = memoryDeviceDatasource.list()
                it.resume(result)
            } catch (e: Throwable) {
                it.resumeWithException(e)
            }
        }
    }

    override suspend fun clear(): Boolean {
        return suspendCancellableCoroutine {
            try {
                val result = memoryDeviceDatasource.clear()
                it.resume(result)
            } catch (e: Throwable) {
                it.resumeWithException(e)
            }
        }
    }

}

class DBDeviceRepositoryImpl : DeviceRepository {
    override suspend fun add(deviceDTO: DeviceDTO): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(deviceDTO: DeviceDTO): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun edit(deviceDTO: DeviceDTO): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun list(): List<DeviceDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun clear(): Boolean {
        TODO("Not yet implemented")
    }

}