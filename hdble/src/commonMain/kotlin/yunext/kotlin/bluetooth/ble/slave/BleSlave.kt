package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.core.XBleDevice
import yunext.kotlin.bluetooth.ble.core.XBleService
import yunext.kotlin.bluetooth.ble.logger.BleRecordCallback

interface BleSlave {

    val configuration: SlaveConfiguration

    val broadcastAddress: String

    val deviceName: String

    val broadcasting: BroadcastStatus

    val connectStatus: ConnectStatus

    fun init(statusCallback: BleSlaveStatusCallback, serverCallback: BleSlaveCallback, recordCallback: BleRecordCallback)

    fun enable(enable:Boolean)

    fun startBroadcast()

    fun stopBroadcast()

    fun disconnect()

    fun clear()

    fun sendResponse(response: BleSlaveResponse): Boolean
    fun notifyChanged(request: BleSlaveRequest): Boolean

}


data class SlaveConfiguration(
    val broadcastAddress: String,
    val deviceName: String,
    val services: List<XBleService>,
)


//<editor-fold desc="ConnectStatus">
sealed interface ConnectStatus {
    data object Disconnected : ConnectStatus

    data class Connected(val device: XBleDevice) : ConnectStatus
}



internal fun ConnectStatus.asEvent(list:List<XBleService>) = BleSlaveConfigurationConnectedDevice(this, services =list )

 val ConnectStatus.text: String
    get() = when (this) {
        is ConnectStatus.Connected -> "已连接[${this.device.address}(${this.device.deviceName})]"
        ConnectStatus.Disconnected -> "已断开连接"
    }

val ConnectStatus.connected: Boolean
    get() = this is ConnectStatus.Connected

//</editor-fold>


//<editor-fold desc="BroadcastStatus">
sealed interface BroadcastStatus {
    data object BroadcastStopped : BroadcastStatus

    data class Init(val configuration: SlaveConfiguration) : BroadcastStatus
    data class Started(val configuration: SlaveConfiguration) : BroadcastStatus

    data class Broadcasting(val configuration: SlaveConfiguration) : BroadcastStatus
}

val BroadcastStatus.doing: Boolean
    get() = this is BroadcastStatus.Broadcasting

val BroadcastStatus.text:String
    get() = when(this){
        BroadcastStatus.BroadcastStopped -> "已停止广播"
        is BroadcastStatus.Broadcasting -> "广播中..."
        is BroadcastStatus.Init -> "初始化中..."
        is BroadcastStatus.Started -> "添加服务中..."
    }

internal fun BroadcastStatus.asEvent(list:List<XBleService>) = BleSlaveConfigurationBroadcasting(this,list)
//</editor-fold>

