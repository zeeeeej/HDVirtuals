package yunext.kotlin.bluetooth.ble.slave

import com.yunext.kmp.context.HDContext
import yunext.kotlin.bluetooth.ble.logger.BleRecordCallback

actual class HDBleSlave internal actual constructor(hdContext: HDContext,configuration:SlaveConfiguration) :
    BleSlave {
    override val configuration: SlaveConfiguration
        get() = TODO("Not yet implemented")
    override val address: String
        get() = TODO("Not yet implemented")
    override val deviceName: String
        get() = TODO("Not yet implemented")
    override val broadcasting: BroadcastStatus
        get() = TODO("Not yet implemented")
    override val connectStatus: ConnectStatus
        get() = TODO("Not yet implemented")

    override fun init(
        statusCallback: BleSlaveStatusCallback,
        serverCallback: BleSlaveCallback,
        recordCallback: BleRecordCallback,
    ) {
        TODO("Not yet implemented")
    }

    override fun enable(enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun startBroadcast() {
        TODO("Not yet implemented")
    }

    override fun stopBroadcast() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun sendResponse(response: BleSlaveResponse): Boolean {
        TODO("Not yet implemented")
    }

    override fun notifyChanged(request: BleSlaveRequest): Boolean {
        TODO("Not yet implemented")
    }

}

actual fun createBleSlave(configuration: SlaveConfiguration): HDBleSlave {
    TODO("Not yet implemented")
}