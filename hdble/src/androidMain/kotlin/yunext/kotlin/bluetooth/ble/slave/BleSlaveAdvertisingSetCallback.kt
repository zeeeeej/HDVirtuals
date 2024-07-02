package yunext.kotlin.bluetooth.ble.slave

import android.bluetooth.le.AdvertisingSetCallback
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class BleSlaveAdvertisingSetCallback: AdvertisingSetCallback() {
}