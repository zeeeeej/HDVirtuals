package yunext.kotlin.bluetooth.ble.master

import com.yunext.kmp.context.HDContext
import com.yunext.kmp.context.hdContext

actual class HDBleMaster internal actual constructor(hdContext: HDContext) :
    BleMaster by HDBleFastBleImpl(hdContext)
//    BleMaster by HDBleJetpackImpl(hdContext)

actual fun createBleMaster(): HDBleMaster {
    return HDBleMaster(hdContext = hdContext)
}