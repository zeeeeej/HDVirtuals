package yunext.kotlin.bluetooth.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattServer
import com.clj.fastble.utils.BleLog
import java.lang.reflect.Method

@Synchronized
internal fun refreshDeviceCache(bluetoothGatt: BluetoothGatt?) {
    try {
        val refresh: Method? = BluetoothGatt::class.java.getMethod("refresh")
        if (refresh != null && bluetoothGatt != null) {
            val success = refresh.invoke(bluetoothGatt) as Boolean
            BleLog.i("refreshDeviceCache, is success:  $success")
        }
    } catch (e: Exception) {
        BleLog.i("exception occur while refreshing device: " + e.message)
        e.printStackTrace()
    }
}

@SuppressLint("MissingPermission")
internal fun BluetoothGattServer?.resetBluetoothGattServer() {
    if (this != null) {
        if (this.services.isNotEmpty()) {
            this.clearServices()
        }
        this.close()
    }
}

internal val BluetoothDevice?.display: String
    @SuppressLint("MissingPermission") get() = this?.run {
        "${this.address}(${this.name ?: "-"})"
    } ?: "device is null"


//public static void startFetch( BluetoothDevice device ) {  try { cl = Class.forName("android.bluetooth.BluetoothDevice"); } catch( ClassNotFoundException exc ) { Log.e(CTAG, "android.bluetooth.BluetoothDevice not found." ); } if (null != cl) { Class[] param = {}; Method method = null; try { method = cl.getMethod("fetchUuidsWithSdp", param); } catch( NoSuchMethodException exc ) { Log.e(CTAG, "fetchUuidsWithSdp not found." ); } if (null != method) { Object[] args = {}; try { method.invoke(device, args); } catch (Exception exc) { Log.e(CTAG, "Failed to invoke fetchUuidsWithSdp method." ); } } } }
