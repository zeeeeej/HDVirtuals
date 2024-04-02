package com.yunext.virtuals.module.datasource

import com.yunext.virtuals.module.repository.DeviceDTO
import com.yunext.virtuals.ui.data.DeviceType
import io.ktor.util.collections.ConcurrentMap

class MemoryDeviceDatasource {
    private val map: ConcurrentMap<String, DeviceDTO> = ConcurrentMap()

    init {
        add(DeviceDTO("twins_test_001", DeviceType.WIFI, "twins_test_001_cid", "tcuf6vn2ohw4mvhb"))
        add(DeviceDTO("twins_test_002", DeviceType.WIFI, "twins_test_002_cid", "tcuf6vn2ohw4mvhb"))
        add(DeviceDTO("twins_test_003", DeviceType.WIFI, "twins_test_003_cid", "tcuf6vn2ohw4mvhb"))
        add(DeviceDTO("twins_test_004", DeviceType.WIFI, "twins_test_004_cid", "tcuf6vn2ohw4mvhb"))
//        add(DeviceDTO("1", DeviceType.WIFI, "ff91482fdfeb", "QR-12TRWQ4"))
//        add(DeviceDTO("2", DeviceType.GPRS, "fe495a3be9c7", "QR-12TRWQ4"))
    }

    fun add(deviceDTO: DeviceDTO): Boolean {

        val key = deviceDTO.communicationId
        if (map.containsKey(key)) {
            return false
        }
        map[key] = deviceDTO
        return true
    }

    fun delete(deviceDTO: DeviceDTO): Boolean {
        val key = deviceDTO.communicationId
        if (map.containsKey(key)) {
            map.remove(key)
            return true
        }
        return true
    }

    fun edit(deviceDTO: DeviceDTO): Boolean {
        val key = deviceDTO.communicationId
        if (map.containsKey(key)) {
            map[key] = deviceDTO
            return true
        }
        return false
    }

    fun list(): List<DeviceDTO> {
        return map.values.toList()
    }

    fun clear(): Boolean {
        map.clear()
        return true
    }

}