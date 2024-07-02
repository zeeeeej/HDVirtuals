package yunext.kotlin.rtc

import yunext.kotlin.bluetooth.ble.master.BleMaster
import yunext.kotlin.bluetooth.ble.master.createBleMaster
import yunext.kotlin.rtc.procotol.rtc

class RTKMaster {

    private val bleMaster: BleMaster = createBleMaster()


    fun startScan(){
        bleMaster.startScan {

        }
    }

    fun stopScan(){
        bleMaster.stopScan()
    }

    fun broadcastCase(broadcast: ByteArray) {
        val mac = rtc {
            parseMacFromBroadcast(broadcast)
        }
        println(mac)
    }
}

// 广播case
// 连接case
// 鉴权case
// 设置case
// 时间戳case