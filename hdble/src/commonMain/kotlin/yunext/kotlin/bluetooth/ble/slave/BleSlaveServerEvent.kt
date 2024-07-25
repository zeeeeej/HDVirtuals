package yunext.kotlin.bluetooth.ble.slave

import yunext.kotlin.bluetooth.ble.core.XBleCharacteristics
import yunext.kotlin.bluetooth.ble.core.XBleDescriptor
import yunext.kotlin.bluetooth.ble.core.XBleDevice
import yunext.kotlin.bluetooth.ble.core.XBleEvent
import yunext.kotlin.bluetooth.ble.core.XBleService


sealed interface BleSlaveServerEvent : XBleEvent {
    val device: XBleDevice
}

@Deprecated("见 BleSlaveConfigurationConnectedDevice")
class BleSlaveOnConnectionStateChange(
    override val device: XBleDevice,
    val status: Int,
    val connected: Boolean,
) : BleSlaveServerEvent

class BleSlaveOnCharacteristicReadRequest(
    override val device: XBleDevice,
    val requestId: Int,
    val offset: Int,
    val characteristic: XBleCharacteristics,
) : BleSlaveServerEvent

class BleSlaveOnExecuteWrite(
    override val device: XBleDevice,
    requestId: Int,
    execute: Boolean,
) : BleSlaveServerEvent

class BleSlaveOnDescriptorReadRequest(
    override val device: XBleDevice,
    val requestId: Int,
    val offset: Int,
    val descriptor: XBleDescriptor?,
) : BleSlaveServerEvent

class BleSlaveOnDescriptorWriteRequest(
    override val device: XBleDevice,
    val requestId: Int,
//    descriptor: BluetoothGattDescriptor?,
    val preparedWrite: Boolean,
    val responseNeeded: Boolean,
    val offset: Int,
    val value: ByteArray?,
) : BleSlaveServerEvent

class BleSlaveOnCharacteristicWriteRequest(
    override val device: XBleDevice,
    val requestId: Int,
    val characteristic: XBleCharacteristics,
    val preparedWrite: Boolean,
    val responseNeeded: Boolean,
    val offset: Int,
    val value: ByteArray?,
) : BleSlaveServerEvent

class BleSlaveOnNotificationSent(
    override val device: XBleDevice,
    status: Int,
) : BleSlaveServerEvent

class BleSlaveOnMtuChanged(
    override val device: XBleDevice,
    mtu: Int,
) : BleSlaveServerEvent

class BleSlaveOnPhyRead(
    override val device: XBleDevice,
    txPhy: Int,
    rxPhy: Int,
    status: Int,
) : BleSlaveServerEvent

class BleSlaveOnPhyUpdate(
    override val device: XBleDevice,
    txPhy: Int,
    rxPhy: Int,
    status: Int,
) : BleSlaveServerEvent

class BleSlaveOnServiceAdded(
    override val device: XBleDevice,
    service: XBleService,
) : BleSlaveServerEvent
