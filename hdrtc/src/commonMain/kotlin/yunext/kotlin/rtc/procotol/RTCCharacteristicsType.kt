package yunext.kotlin.rtc.procotol

import yunext.kotlin.bluetooth.ble.core.XCharacteristicsPermission
import yunext.kotlin.bluetooth.ble.core.XCharacteristicsProperty

sealed interface RTCCharacteristicsType {
    val properties: Array<XCharacteristicsProperty>
    val permissions: Array<XCharacteristicsPermission>
}

val RTCCharacteristicsType.canWrite: Boolean
    get() = this.permissions.isNotEmpty() && this.permissions.any {
        it == XCharacteristicsPermission.WRITE
    }

val RTCCharacteristicsType.canRead: Boolean
    get() = this.permissions.isNotEmpty() && this.permissions.any {
        it == XCharacteristicsPermission.READ
    }

data object RTCReadCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.READ)
    override val permissions: Array<XCharacteristicsPermission>
        get() = arrayOf(XCharacteristicsPermission.READ)

}

data object RTCWriteCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.READ, XCharacteristicsProperty.WRITE)
    override val permissions: Array<XCharacteristicsPermission>
        get() = arrayOf(XCharacteristicsPermission.WRITE)

}

data object RTCIndicateCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.READ, XCharacteristicsProperty.INDICAte)
    override val permissions: Array<XCharacteristicsPermission>
        get() = arrayOf(XCharacteristicsPermission.NONE)

}

data object RTCWriteNoResponseCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(
            XCharacteristicsProperty.WriteWithoutResponse,
            XCharacteristicsProperty.READ,
            XCharacteristicsProperty.WRITE
        )
    override val permissions: Array<XCharacteristicsPermission>
        get() = arrayOf(XCharacteristicsPermission.WRITE)

}

data object RTCNotifyCharacteristicsType : RTCCharacteristicsType {
    override val properties: Array<XCharacteristicsProperty>
        get() = arrayOf(XCharacteristicsProperty.Notify, XCharacteristicsProperty.READ)
    override val permissions: Array<XCharacteristicsPermission>
        get() = arrayOf(XCharacteristicsPermission.READ)

}