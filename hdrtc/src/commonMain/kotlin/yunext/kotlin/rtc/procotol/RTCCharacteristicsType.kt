package yunext.kotlin.rtc.procotol

import yunext.kotlin.bluetooth.ble.core.XCharacteristicsPermission
import yunext.kotlin.bluetooth.ble.core.XCharacteristicsProperty

sealed interface RTCCharacteristicsType {
    val properties: Array<XCharacteristicsProperty>
    val permission: XCharacteristicsPermission
}

data object RTCReadCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.READ)
    override val permission: XCharacteristicsPermission
        get() = XCharacteristicsPermission.READ

}

data object RTCWriteCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.READ, XCharacteristicsProperty.WRITE)
    override val permission: XCharacteristicsPermission
        get() = XCharacteristicsPermission.WRITE

}

data object RTCIndicateCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.READ, XCharacteristicsProperty.INDICAte)
    override val permission: XCharacteristicsPermission
        get() = XCharacteristicsPermission.NONE

}

data object RTCWriteNoResponseCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.WriteWithoutResponse)
    override val permission: XCharacteristicsPermission
        get() = XCharacteristicsPermission.WRITE

}

data object RTCNotifyCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.Notify)
    override val permission: XCharacteristicsPermission
        get() = XCharacteristicsPermission.NONE

}