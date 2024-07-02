//package yunext.kotlin.bluetooth.ble.master
//
//import android.Manifest
//import android.bluetooth.BluetoothManager
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.bluetooth.BluetoothDevice
//import androidx.bluetooth.BluetoothLe
//import androidx.core.app.ActivityCompat
//import com.yunext.kmp.context.HDContext
//import com.yunext.kmp.context.application
//import kotlinx.coroutines.CoroutineName
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import yunext.kotlin.bluetooth.ble.d
//import yunext.kotlin.bluetooth.ble.i
//import yunext.kotlin.bluetooth.ble.w
//import java.util.UUID
//
//class HDBleJetpackImpl internal constructor(hdContext: HDContext) : BleMaster {
//    private val context = hdContext.application
//    private val bluetoothLe = BluetoothLe(context)
//    private val bluetoothManager =
//        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//    private val bluetoothAdapter = bluetoothManager.adapter
//    private val bleScope =
//        CoroutineScope(Dispatchers.Main + SupervisorJob() + CoroutineName("HDBleMaster-Android"))
//
//    private var startScanJob: Job? = null
//    private var connectJob: Job? = null
//
//    private val scanCache: MutableMap<String, androidx.bluetooth.ScanResult> = mutableMapOf()
//
//    override fun startScan(onResult: (XBleScanResult) -> Unit) {
//        d("[startScan]>>>")
//        startScanJob?.cancel()
//        startScanJob = null
//        startScanInternal(onResult)
//    }
//
//    private fun startScanInternal(onResult: (XBleScanResult) -> Unit) {
//        startScanJob = bleScope.launch {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                    w("[startScan]  java.lang.SecurityException: Need android.permission.BLUETOOTH_SCAN permission for android.content")
//                    return@launch
//                }
//            }
//
//            val scanResultFlow =
//
//                bluetoothLe.scan(
//                    emptyList()
//                    //                    listOf(
//                    //                        ScanFilter(serviceDataUuid = null, deviceName = ""),
//                    //                    )
//                )
//            scanResultFlow.collect {
//                val address = it.deviceAddress.address
//                val deviceName = it.device.name ?: return@collect
//                val rssi = it.rssi
//                // i("[startScan] <${address}>(${deviceName})~${rssi}")
//
//                scanCache[address] = it
//                onResult.invoke(
//                    XBleScanResult(
//                        name = deviceName ?: "",
//                        mac = address,
//                        rssi = rssi
//                    )
//                )
//            }
//            i("[startScan]<<<<<<<<<")
//        }.let { curJob ->
//            curJob.invokeOnCompletion {
//                d("[startScan] invokeOnCompletion ${it?.message}")
//            }
//            curJob
//        }
//    }
//
//    override fun stopScan() {
//        d("[stopScan]>>>")
//        startScanJob?.cancel()
//        startScanJob = null
//    }
//
//    override fun connect(device: XBleDevice, onUp: (XBleUp) -> Unit) {
//        i("[connect]>>> $device")
//        stopScan()
//        connectJob?.cancel()
//        connectJob = null
//        val address = device.mac
//        val bluetoothDevice = scanCache[address]?.device ?: return
//        connectInternal(bluetoothDevice)
//    }
//
//    private fun connectInternal(bluetoothDevice: BluetoothDevice) {
//        connectJob = bleScope.launch {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                Manifest.permission.BLUETOOTH_CONNECT
//
//                if (ActivityCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.BLUETOOTH_CONNECT
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    w("[connect]  java.lang.SecurityException: Need android.permission.BLUETOOTH_Connect permission for android.content")
//                    return@launch
//                }
//            }
//            bluetoothLe.connectGatt(bluetoothDevice) {
//                //                launch {
//                //                    this@connectGatt.servicesFlow.collect {
//                //
//                //                            services ->
//                //                        i("services (${services.size}个)")
//                //                        services.forEach { service ->
//                //                            d(" service(${service.uuid.toString()} (${service.characteristics.size})个characteristics")
//                //                            service.characteristics.forEach { characteristics ->
//                //                                d("     characteristics(${characteristics.uuid.toString()}")
//                //                            }
//                //                        }
//                //                    }
//                //                }
//
//
//                launch read@{
//                    delay(1000)
//
//                    w("读取设备 mac 地址")
//                    val service = getService(UUID.fromString(uuid("A001")))
//                    w("读取设备 mac 地址 service:$service")
//                    val characteristic =
//                        service?.getCharacteristic(UUID.fromString(uuid("B002"))) ?: return@read
//                    w("读取设备 mac 地址 characteristic:$characteristic")
////                    launch {
////                        subscribeToCharacteristic(characteristic).collect {
////                            w("subscribeToCharacteristic 设备 mac 地址 ${it.decodeToString()}")
////                        }
////                    }
//
//
//                    run {
//                        delay(1000)
//                        //                    withTimeout(5000) {
//                        w("设备 mac 地址 >>>>>>>>>>>>")
//                        val readCharacteristic = readCharacteristic(characteristic)
//                        w("设备 mac 地址 ${readCharacteristic.getOrNull()?.decodeToString()}")
//                        w("设备 mac 地址 <<<<<<<<<<<<<")
//                    }/*.onSuccess {
//                            w("设备 mac 地址 ${it.decodeToString()}")
//                        }.onFailure {
//                            e("设备 mac 地址 e = ${it.message}")
//                        }*/
//
//
//                }
//            }
//        }.also {
//            it.invokeOnCompletion { e ->
//                i("[connect]invokeOnCompletion ${e?.message}")
//            }
//        }
//    }
//
//    override fun notify(service: XBleService, characteristic: XBleCharacteristic) {
//        TODO("Not yet implemented")
//    }
//
//    override fun read(service: XBleService, characteristic: XBleCharacteristic) {
//        TODO("Not yet implemented")
//    }
//
//    override fun write(service: XBleService, characteristic: XBleCharacteristic, down: XBleDown) {
//        TODO("Not yet implemented")
//    }
//
//    override fun disconnect() {
//        connectJob?.cancel()
//        connectJob = null
//    }
//
//    override fun clear() {
//        d("[close]")
//
//        startScanJob?.cancel()
//        connectJob?.cancel()
//        startScanJob = null
//        connectJob = null
//        scanCache.clear()
//    }
//
//
//}