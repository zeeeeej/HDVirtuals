package yunext.kotlin.bluetooth.ble.slave

import com.yunext.kmp.context.HDContext

expect class HDBleSlave internal constructor(hdContext: HDContext, configuration: SlaveConfiguration) : BleSlave

expect fun createBleSlave(configuration: SlaveConfiguration): HDBleSlave






