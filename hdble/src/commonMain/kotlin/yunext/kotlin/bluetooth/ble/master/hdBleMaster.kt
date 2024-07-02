package yunext.kotlin.bluetooth.ble.master

import com.yunext.kmp.context.HDContext

expect class HDBleMaster internal constructor(hdContext: HDContext) : BleMaster

expect fun createBleMaster(): HDBleMaster






