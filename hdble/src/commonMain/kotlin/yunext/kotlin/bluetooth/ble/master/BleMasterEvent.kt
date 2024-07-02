package yunext.kotlin.bluetooth.ble.master

import yunext.kotlin.bluetooth.ble.core.XBleDevice
import yunext.kotlin.bluetooth.ble.core.XBleEvent
import yunext.kotlin.bluetooth.ble.core.XBleException
import yunext.kotlin.bluetooth.ble.core.XBleUpPayload


sealed interface BleMasterEvent : XBleEvent {
    val device: XBleDevice
}

data class BleMasterConnectFail(override val device: XBleDevice, val e: XBleException) :
    BleMasterEvent

data class BleMasterConnectSuccess(override val device: XBleDevice) : BleMasterEvent

sealed interface BleMasterNotifyEvent : XBleEvent {
    val device: XBleDevice
}

data class BleMasterNotifyFail(override val device: XBleDevice, val e: XBleException) :
    BleMasterNotifyEvent

data class BleMasterNotifySuccess(override val device: XBleDevice) : BleMasterNotifyEvent
data class BleMasterNotifyDataChanged(override val device: XBleDevice, val data: XBleUpPayload) :
    BleMasterNotifyEvent

sealed interface BleMasterReadEvent : XBleEvent {
    val device: XBleDevice
}

data class BleMasterReadFail(override val device: XBleDevice, val e: XBleException) :
    BleMasterReadEvent

data class BleMasterReadSuccess(override val device: XBleDevice, val data: XBleUpPayload) :
    BleMasterReadEvent

sealed interface BleMasterWriteEvent : XBleEvent {
    val device: XBleDevice
}

data class BleMasterWriteFail(override val device: XBleDevice, val e: XBleException) :
    BleMasterWriteEvent

data class BleMasterWriteSuccess(override val device: XBleDevice) : BleMasterWriteEvent