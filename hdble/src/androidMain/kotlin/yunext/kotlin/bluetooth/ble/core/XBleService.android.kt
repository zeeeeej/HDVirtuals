package yunext.kotlin.bluetooth.ble.core

import android.bluetooth.BluetoothGattService


class AndroidBleService internal constructor(
    override val uuid: String,
    override val characteristics: List<XBleCharacteristics>,
) : XBleService

actual fun generateBleService(
    uuid: String,
    characteristics: List<XBleCharacteristics>,
): XBleService {
    return AndroidBleService(uuid, characteristics)
}




internal fun BluetoothGattService.asService() =
    generateBleService(
        uuid = this.uuid.toString(),
        characteristics = this.characteristics.map { it.asCharacteristics() }
    )