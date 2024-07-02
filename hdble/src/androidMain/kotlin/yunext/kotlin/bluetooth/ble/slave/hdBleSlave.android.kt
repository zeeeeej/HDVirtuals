package yunext.kotlin.bluetooth.ble.slave

import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.hdContext

actual class HDBleSlave internal actual constructor(hdContext: HDContext, configuration: SlaveConfiguration) :
    BleSlave by AndroidBleSlaveImpl(hdContext, configuration)

actual fun createBleSlave(configuration: SlaveConfiguration): HDBleSlave {
    return HDBleSlave(hdContext = hdContext, configuration = configuration)
}



