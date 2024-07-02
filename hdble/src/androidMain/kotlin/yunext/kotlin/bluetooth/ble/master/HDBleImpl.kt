//package yunext.kotlin.bluetooth.ble.master
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothManager
//import android.bluetooth.le.ScanCallback
//import android.bluetooth.le.ScanResult
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.bluetooth.BluetoothDevice
//import androidx.bluetooth.BluetoothLe
//import androidx.core.app.ActivityCompat
//import com.clj.fastble.BleManager
//import com.clj.fastble.callback.BleScanCallback
//import com.clj.fastble.scan.BleScanRuleConfig
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
//internal class HDBleImpl internal  constructor(hdContext: HDContext) : BleMaster {
//    private val context = hdContext.application
//    private val bluetoothLe = BluetoothLe(context)
//    private val scope =
//        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("HDBleMaster-Android"))
//
//    private var startScanJob: Job? = null
//    private var connectJob: Job? = null
//
//    private val scanCache: MutableMap<String, androidx.bluetooth.ScanResult> = mutableMapOf()
//    private val scanCacheFastBle: MutableMap<String, com.clj.fastble.data.BleDevice> = mutableMapOf()
//
//    private val bluetoothManager =
//        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//    private val bluetoothAdapter = bluetoothManager.adapter
//    private val scanCallback = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult?) {
//            super.onScanResult(callbackType, result)
//            d("[onScanResult] type:$callbackType result:$result")
//        }
//
//        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
//            super.onBatchScanResults(results)
//            d("[onBatchScanResults] results:$results")
//        }
//
//        override fun onScanFailed(errorCode: Int) {
//            super.onScanFailed(errorCode)
//            d("[onScanFailed] errorCode:$errorCode")
//        }
//    }
//
//    private val leScanCallback =
//        BluetoothAdapter.LeScanCallback { device, rssi, scanRecord -> d("[onLeScan] device:$device ,rssi$rssi ,scanRecord:$scanRecord") }
//
//
//    private val bleManager: BleManager = BleManager.getInstance().apply {
//        initScanRule(
//            BleScanRuleConfig.Builder()
//                //.setServiceUuids(arrayOf(HBle.adUUID()))
//                .build()
//        )
//        //connectOverTime = DEFAULT_CONNECT_TIMEOUT * 10
//        splitWriteNum = 240
//
//        init(this@HDBleMaster.context)
//
//    }
//
//    private val bleScanCallback = object : BleScanCallback() {
//        override fun onScanStarted(success: Boolean) {
//            d("[onScanStarted] success:$success")
//        }
//
//        override fun onScanning(bleDevice: com.clj.fastble.data.BleDevice?) {
//            d("[onScanning] bleDevice:$bleDevice")
//
//
//        }
//
//        override fun onScanFinished(scanResultList: MutableList<com.clj.fastble.data.BleDevice>?) {
//            d("[onScanFinished] scanResultList:$scanResultList")
//        }
//
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun scanInternal(enable: Boolean) {
////        if (enable) {
////            val filters: List<android.bluetooth.le.ScanFilter> = emptyList()
////            val settings: ScanSettings =
////                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
////                    .build()
////            bluetoothAdapter.bluetoothLeScanner.startScan(filters, settings, scanCallback)
////        } else {
////            bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
////        }
//
////        if (enable){
////            bluetoothAdapter.startLeScan(leScanCallback)
////        }else{
////            bluetoothAdapter.stopLeScan(leScanCallback)
////        }
//
//        if (enable) {
//            d("[scanInternal] ${Build.VERSION.SDK_INT}")
//            d(
//                """
//                isSupportBle    :   ${bleManager.isSupportBle}
//                isBlueEnable    :   ${bleManager.isBlueEnable}
//            """.trimIndent()
//            )
//            bleManager.initScanRule(
//                BleScanRuleConfig.Builder()
//                    .setScanTimeOut(15000)
//                    .build()
//            )
//            bleManager.scan(bleScanCallback)
//        } else {
//            bleManager.cancelScan()
//        }
//
//
//    }
//
//
//    override fun startScan(onResult: (BleScanResult) -> Unit) {
//        d("[startScan]>>>")
//        startScanJob?.cancel()
//        startScanJob = null
//        scanInternal(true)
//        //startScanInternal(onResult)
//
//    }
//
//    private fun startScanInternal(onResult: (BleScanResult) -> Unit) {
//        startScanJob = scope.launch {
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
//                    BleScanResult(
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
//        try {
//            scanInternal(false)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        startScanJob?.cancel()
//        startScanJob = null
//    }
//
//    override fun connect(device: BleDevice, onUp: (BleUp) -> Unit) {
//        i("[connect]>>> $device")
//        stopScan()
//        connectJob?.cancel()
//        connectJob = null
//        val address = device.mac
//        val bluetoothDevice = scanCache[address]?.device ?: return
//        connectInternal(bluetoothDevice)
//
//    }
//
//    private fun connectInternalByFastBle(bluetoothDevice: BluetoothDevice) {
//        BleManager.getInstance().connect()
//    }
//
//    private fun connectInternal(bluetoothDevice: BluetoothDevice) {
//        connectJob = scope.launch {
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
//                    delay(3000)
//
//                    w("读取设备 mac 地址")
//                    val service = getService(UUID.fromString(uuid("A001")))
//                    w("读取设备 mac 地址 service:$service")
//                    val characteristic =
//                        service?.getCharacteristic(UUID.fromString(uuid("B001"))) ?: return@read
//                    w("读取设备 mac 地址 characteristic:$characteristic")
//                    launch {
//
//                        subscribeToCharacteristic(characteristic).collect {
//                            w("subscribeToCharacteristic 设备 mac 地址 ${it.decodeToString()}")
//                        }
//                    }
//
//
//                    run {
//                        //                    withTimeout(5000) {
//                        val readCharacteristic = readCharacteristic(characteristic)
//                        w("设备 mac 地址 ${readCharacteristic.getOrNull()?.decodeToString()}")
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
//    override fun enableNotify(characteristic: BleCharacteristic) {
//        TODO("Not yet implemented")
//    }
//
//    override fun read(service: BleService, characteristic: BleCharacteristic) {
//        TODO("Not yet implemented")
//    }
//
//    override fun write(service: BleService, characteristic: BleCharacteristic, down: BleDown) {
//        TODO("Not yet implemented")
//    }
//
//    override fun disconnect() {
//        connectJob?.cancel()
//        connectJob = null
//    }
//
//    override fun close() {
//        d("[close]")
//        startScanJob?.cancel()
//        connectJob?.cancel()
//        startScanJob = null
//        connectJob = null
//        scanCacheFastBle.clear()
//        scanCache.clear()
//    }
//
//
//}