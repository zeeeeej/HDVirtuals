import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import yunext.kotlin.bluetooth.ble.core.XCharacteristicsProperty
import yunext.kotlin.bluetooth.ble.core.generateBleService
import yunext.kotlin.bluetooth.ble.core.generateXBleCharacteristics
import yunext.kotlin.bluetooth.ble.slave.SlaveConfiguration
import yunext.kotlin.bluetooth.ble.util.uuidFromShort
import yunext.kotlin.rtc.procotol.ParameterData
import yunext.kotlin.rtc.procotol.ParameterKey
import yunext.kotlin.rtc.procotol.ParameterPacket
import yunext.kotlin.rtc.procotol.RTCScopeImpl
import yunext.kotlin.rtc.procotol.RTC_ACCESS_KEY
import yunext.kotlin.rtc.procotol.assci
import yunext.kotlin.rtc.procotol.rtc
import yunext.kotlin.rtc.procotol.rtcCmdDataList
import yunext.kotlin.rtc.procotol.toByteArray
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class RTCScopeTest {
    private val scope = RTCScopeImpl()

    // 5aa5b1002039383731303437343331326361343862363139383838323333376534336463357f
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `测试rtcDataForAuthenticationWrite`() {
        val accessKey = RTC_ACCESS_KEY
        val mac = "88:23:1F:74:B5:0E"
        val rtcData = scope.rtcDataForAuthenticationWrite(accessKey, mac)
        println("加密后的数据：${rtcData.toByteArray().toHexString()}")
        val result = scope.rtcDataForAuthenticationNotify(
            authed = rtcData.payload,
            accessKey = accessKey,
            mac = mac
        )
        println("检查：${result}")
        assertTrue(result)

        // 密钥 : 12xs8gmkwgrgxtye
        // mac : 88231f74b50e
        // md5 : 3f4d2063a02b37e4cd861d5e7ee1269a
        // 数据 : 5aa5 b1 00 20 3366346432303633613032623337653463643836316435653765653132363961 8c
        // 5aa5 b1 00 10 3f4d2063a02b37e4cd861d5e7ee1269a 8c
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `parseParameterPacketListFromPayload`() {
        val parameterList = ParameterKey.entries.map { pk ->
            val parameterData = ParameterData(key = pk.assci, Random.Default.nextBytes(4))
            ParameterPacket(parameterData)
        }
        println("前(${parameterList.size})：$parameterList")
        val rtcData = scope.rtcDataForParameterWrite(parameterList)
        val payload = rtcData.payload
        val packets = scope.parseParameterPacketListFromPayload(payload)
        println("后（${packets.size}）：$packets")
        assertContentEquals(parameterList, packets)

    }

    private var job: Job? = null

    @Test
    fun `测试timeoutJob`() {
        runBlocking {

            job?.cancel()
            job = null
            job = launch {
                val timeoutJob = launch {
                    launch {
                        while (isActive) {
                            delay(1000)
                            println("timeout job running ...")
                        }
                    }
                    delay(50000)
                    println("timeout!")
                }.also {
                    it.invokeOnCompletion {
                        println("timeout invokeOnCompletion")
                    }
                }

                launch {
                    delay(10000)
                    println("do my work")
                    timeoutJob.cancel()
                }
            }

            println("end")

        }
    }

    @Test
    fun `tryGetService`() {
        val notify = arrayOf(XCharacteristicsProperty.Notify, XCharacteristicsProperty.READ)
        val r1 = arrayOf(XCharacteristicsProperty.READ, XCharacteristicsProperty.Notify)
        val r2 = arrayOf(XCharacteristicsProperty.Notify, XCharacteristicsProperty.READ)

        assertFalse {
            r1.contentEquals(notify)
        }

        assertTrue {
            r2.contentEquals(notify)
        }

        assertTrue {
            r1.sortedArray().contentEquals(notify.sortedArray())
        }

        val sc = rtc{
            tryGetService(generateConfiguration("123").services)
        }
        println("sc:$sc")
        assertTrue {
            sc!=null
        }

    }

    private fun generateConfiguration(targetAddress: String) =
        SlaveConfiguration(
            broadcastAddress = targetAddress,
            deviceName = targetAddress,
            services = rtcCmdDataList.map {
                generateBleService(
                    uuidFromShort(it.serviceShortUUID),
                    listOf(
                        generateXBleCharacteristics(
                            uuid = uuidFromShort(it.characteristicsShortUUID),
                            serviceUUID = uuidFromShort(it.serviceShortUUID),
                            properties = it.cmd.characteristicsType.properties.toList(),
                            permissions = (it.cmd.characteristicsType.permissions).toList(),
                            value = byteArrayOf(),
                            descriptors = emptyList()
                        )
                    )
                )
            }
        )
}