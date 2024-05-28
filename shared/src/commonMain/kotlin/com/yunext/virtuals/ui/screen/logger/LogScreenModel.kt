package com.yunext.virtuals.ui.screen.logger

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.db.datasource.LogDatasource
import com.yunext.virtuals.data.Log
import com.yunext.virtuals.data.UpLog
import com.yunext.virtuals.data.device.MQTTDevice
import com.yunext.virtuals.data.device.TwinsDevice
import com.yunext.virtuals.module.devicemanager.deviceManager
import com.yunext.virtuals.module.devicemanager.filterOrNull
import com.yunext.virtuals.module.repository.LogRepository
import com.yunext.virtuals.ui.Effect
import com.yunext.virtuals.ui.common.HDStateScreenModel
import com.yunext.virtuals.ui.data.DeviceAndStateViewData
import com.yunext.virtuals.ui.screen.logger.data.UIType
import com.yunext.virtuals.ui.screen.logger.data.toSign
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


enum class PullState {
    Idle,
    Refreshing,
    LoadMoreIng,
    ;
}


@Stable
internal data class LogState(
    val device: DeviceAndStateViewData,
    val list: List<Log>,
    val effect: Effect,
    val alert: String? = null,
    @Deprecated("delete")
    val msg: String? = null,
    val searchCondition: SearchCondition = SearchCondition(),
    val pullState: PullState = PullState.Idle,

    ) {

}

@Stable
data class SearchCondition(
    val sign: LogDatasource.Sign = LogDatasource.Sign.ALL,
    val start: Long = 0L,
    val end: Long = 0L,
    val search: String = "",
    val pageNumber: Int = 1,
    val pageSize: Int = 100,
    val preFetchSize: Int = 5,
)

internal class LogScreenModel(initialState: LogState) : HDStateScreenModel<LogState>(initialState) {
    private val logRepository: LogRepository by lazy {
        LogRepository
    }
    private var doSearchJob: Job? = null
    private var doLoadMoreJob: Job? = null

    init {
        doSearch(initialState.searchCondition, "LogScreenModel::init")
    }

    private val _toast: MutableSharedFlow<String> = MutableSharedFlow()
    val toast = _toast.asSharedFlow()

    suspend fun toast(msg: String) {
        _toast.emit(msg)
    }


    fun doLoadMore() {
        HDLogger.d("LogScreenModel", "doLoadMore")
        doLoadMoreJob?.cancel()
        doLoadMoreJob = null
        doLoadMoreJob = screenModelScope.launch {
            try {
                val deviceState = state.value.device
                val device =
                    deviceManager.deviceStoreMapStateFlow.value.filterOrNull(deviceState.communicationId)?.device
                        ?: return@launch
                require(device is TwinsDevice) {
                    "暂只支持TwinsDevice"
                }
                val oldSearchCondition = state.value.searchCondition
                mutableState.value = state.value.copy(
                    pullState = PullState.LoadMoreIng
                )
                val searchCondition =
                    oldSearchCondition.copy(pageNumber = oldSearchCondition.pageNumber + 1)
                val oldList = state.value.list
                val list = withContext(Dispatchers.IO) {
                    //delay(2000)
                    logRepository.search(
                        deviceId = device.deviceId,
                        sign = searchCondition.sign,
                        start = searchCondition.start,
                        end = searchCondition.end,
                        search = searchCondition.search,
                        pageNumber = searchCondition.pageNumber,
                        pageSize = searchCondition.pageSize
                    )
                }
                mutableState.value = state.value.copy(
                    searchCondition = searchCondition,
                    list = oldList + list,
                    pullState = PullState.Idle
                )
            } catch (e: Exception) {
                toast(e.message ?: "doLoadMore error")
            } finally {
                mutableState.value = state.value.copy(pullState = PullState.Idle)
            }
        }

    }


    fun onSearchConditionChanged(search: String) {
        doSearch(
            this.state.value.searchCondition.copy(search = search),
            "onSearchConditionChanged search:$search"
        )
    }

    fun onSearchConditionChanged(start: Long, end: Long) {
        doSearch(
            this.state.value.searchCondition.copy(start = start, end = end),
            "onSearchConditionChanged start:$start ,end:$end"
        )
    }

    fun onSearchConditionChanged(uiType: UIType) {

        doSearch(
            this.state.value.searchCondition.copy(sign = uiType.toSign(), pageNumber = 1),
            "onSearchConditionChanged uiType:$uiType"
        )
    }

    private fun doSearch(searchCondition: SearchCondition, tag: String) {
        HDLogger.d("LogScreenModel", "=>doSearch $searchCondition @$tag")
        val deviceState = state.value.device
        val device =
            deviceManager.deviceStoreMapStateFlow.value.filterOrNull(deviceState.communicationId)?.device
                ?: return
        require(device is TwinsDevice) {
            "暂只支持TwinsDevice"
        }
        doSearchJob?.cancel()
        doSearchJob = null
        doSearchJob = screenModelScope.launch {
            try {
                mutableState.value = state.value.copy(pullState = PullState.Refreshing)
                mutableState.value = state.value.copy(
                    searchCondition = searchCondition
                )
                HDLogger.d("LogScreenModel", "doSearch 555555")
                val list = withContext(Dispatchers.IO) {
                    //delay(1000)
                    logRepository.search(
                        deviceId = device.deviceId,
                        sign = searchCondition.sign,
                        start = searchCondition.start,
                        end = searchCondition.end,
                        search = searchCondition.search,
                        pageNumber = searchCondition.pageNumber,
                        pageSize = searchCondition.pageSize
                    )
                }
                HDLogger.d("LogScreenModel", "doSearch 666666 ${list.size}")
                mutableState.value = state.value.copy(
//                    msg = "共${searchCondition.pageNumber} ${list.size}条数据",
                    list = list
                )
            } catch (e: Exception) {
//                e.printStackTrace()
                HDLogger.e("LogScreenModel", "error: ${e.message} e=${e} ,check e  = ${e is CancellationException}")
                //toast(e.message ?: "doSearch error")
                if (e is CancellationException) {
                    HDLogger.d("LogScreenModel", "doSearch 8888 ${e.message}")
                    throw e
                }
            } finally {
                mutableState.value = state.value.copy(pullState = PullState.Idle)
            }

        }

    }

}


/*(0..100).map {
                        val type = Random.nextInt(3)
                        when (type) {
                            0 -> {
                                UpLog(
                                    id = it.toLong(),
                                    timestamp = currentTime(),
                                    deviceId = "deviceId-$it",
                                    clientId = "clientId-$it",
                                    topic = "topic-$it",
                                    cmd = "cmd-$it",
                                    payload = "payload-$it",
                                    state = Random.nextBoolean()
                                )
                            }

                            1 -> {
                                DownLog(
                                    id = it.toLong(),
                                    timestamp = currentTime(),
                                    deviceId = "deviceId-$it",
                                    clientId = "clientId-$it",
                                    topic = "topic-$it",
                                    cmd = "cmd-$it",
                                    payload = "payload-$it",
                                )
                            }

                            else -> {
                                OnlineLog(
                                    id = it.toLong(),
                                    timestamp = currentTime(),
                                    deviceId = "deviceId-$it",
                                    clientId = "clientId-$it",
                                    onLine = Random.nextBoolean()

                                )
                            }
                        }

                    }*/