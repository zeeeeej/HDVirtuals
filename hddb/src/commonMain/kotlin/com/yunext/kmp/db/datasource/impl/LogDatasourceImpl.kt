package com.yunext.kmp.db.datasource.impl

import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.database.Hd_log
import com.yunext.kmp.db.DBLog
import com.yunext.kmp.db.database.DemoDatabaseOwner
import com.yunext.kmp.db.datasource.LogDatasource
import com.yunext.kmp.db.entity.LogEntity
import com.yunext.kmp.db.entity.toEntity

class LogDatasourceImpl: LogDatasource {
    private val owner: DemoDatabaseOwner = DemoDatabaseOwner
    /**
     * UI展示的最近日志Id，防止查询时间内，有新的数据添加，加载更多会导致数据重复。
     * todo 线程同步？
     */
    private val mLatestLogIdMap: MutableMap<LogDatasource.Sign, Long?> = mutableMapOf()

    private fun getLatestLogId(sign: LogDatasource.Sign, refresh: Boolean) =
        if (refresh) Long.MAX_VALUE else (mLatestLogIdMap[sign] ?: Long.MAX_VALUE)

    private fun updateLatestLogId(sign: LogDatasource.Sign, id: Long) {
        mLatestLogIdMap[sign] = id
    }

    override fun findAll(): List<LogEntity> {
        val hdLogs: List<Hd_log> = owner.database.demoDatabaseQueries.selectAllLog().executeAsList()
        DBLog.d("LogDatasourceImpl::findAll size = $hdLogs")
        return hdLogs.map(Hd_log::toEntity)
    }

    override fun searchAll(
        deviceId: String,
        sign: LogDatasource.Sign,
        search: String?,
        start: Long,
        end: Long,
        pageNumber: Int,
        pageSize: Int,
    ): List<LogEntity> {
        val realStart = if (start <= 0) 0 else start
        val realEnd = if (end <= 0) currentTime() else end
        require(realStart >= 0) {
            "start=$realStart must >=0"
        }
        require(realEnd >= 0) {
            "end=$realEnd must >=0"
        }
        require(realEnd > realStart) {
            "start=$realStart end=$realEnd !end must > start!"
        }
        require(pageNumber >= 1) {
            "pageNumber=$pageNumber must >=1"
        }

        require(pageSize >= 1) {
            "pageSize=$pageSize must >=1"
        }
        val lastId = getLatestLogId(sign, pageNumber == 1)
        check(lastId >= 0) {
            "lastId错误"
        }


        fun applyLatest(toList: List<LogEntity>) {
            if (pageNumber == 1 && toList.isNotEmpty()) {
                updateLatestLogId(sign, toList[0].lid)
            }
        }
        DBLog.d("LogDatasourceImpl::searchAll 1111 $sign")
        val list = when (sign) {
            LogDatasource.Sign.ALL -> {
                owner.queries.searchAllLog(
                    lastId = lastId,
                    deviceId = deviceId,
                    start = realStart,
                    end = realEnd,
                    search = "%${search ?: ""}%",
                    pageSize = pageSize.toLong(),
                    offset = pageSize * (pageNumber - 1).toLong()

                ).executeAsList()
            }

            LogDatasource.Sign.FAIL -> {
                owner.queries.searchAllLogByState(
                    state = false,
                    lastId = lastId,
                    deviceId = deviceId,
                    start = realStart,
                    end = realEnd,
                    search = search ?: "",
                    pageSize = pageSize.toLong(),
                    offset = pageSize * (pageNumber - 1).toLong()
                ).executeAsList()
            }

            LogDatasource.Sign.SUCCESS -> owner.queries.searchAllLogByState(
                state = true,
                lastId = lastId,
                deviceId = deviceId,
                start = realStart,
                end = realEnd,
                search = search ?: "",
                pageSize = pageSize.toLong(),
                offset = pageSize * (pageNumber - 1).toLong()
            ).executeAsList()

            LogDatasource.Sign.ONLINE -> owner.queries.searchAllLogByType(
                type = LogEntity.Type.Online.type,
                lastId = lastId,
                deviceId = deviceId,
                start = realStart,
                end = realEnd,
                search = search ?: "",
                pageSize = pageSize.toLong(),
                offset = pageSize * (pageNumber - 1).toLong()
            ).executeAsList()

            LogDatasource.Sign.UP -> owner.queries.searchAllLogByType(
                type = LogEntity.Type.Up.type,
                lastId = lastId,
                deviceId = deviceId,
                start = realStart,
                end = realEnd,
                search = search ?: "",
                pageSize = pageSize.toLong(),
                offset = pageSize * (pageNumber - 1).toLong()
            ).executeAsList()

            LogDatasource.Sign.DOWN -> owner.queries.searchAllLogByType(
                type = LogEntity.Type.Down.type,
                lastId = lastId,
                deviceId = deviceId,
                start = realStart,
                end = realEnd,
                search = search ?: "",
                pageSize = pageSize.toLong(),
                offset = pageSize * (pageNumber - 1).toLong()
            ).executeAsList()
        }.map(Hd_log::toEntity)
        DBLog.d("LogDatasourceImpl::searchAll 2222")
        applyLatest(list)
        DBLog.d("LogDatasourceImpl::searchAll size = $list")
        return list

    }

    override fun add(logEntity: LogEntity) {
        DBLog.d("LogDatasourceImpl::add")
        val (_, timestamp, deviceId, clientId, type, topic, cmd, payload, state, onLine) = logEntity
        owner.queries.insertLog(
            timestamp = timestamp,
            device_id = deviceId,
            client_id = clientId,
            type = type.type,
            topic = topic,
            cmd = cmd,
            payload = payload,
            payload_raw = payload.encodeToByteArray(),
            online = onLine,
            state = state
        )
    }

    override fun clearById(vararg logId: Long) {
        require(logId.isNotEmpty()) {
            "必须输入一个log id"
        }
        logId.forEach {
            owner.queries.deleteLogById(it)
        }

    }

    override fun clear() {
        owner.queries.deleteAllLog()
    }

    override fun clearByDevice(deviceId: String) {
        owner.queries.deleteLogByDeviceId(deviceId)
    }
}