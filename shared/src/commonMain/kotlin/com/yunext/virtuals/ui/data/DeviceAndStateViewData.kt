package com.yunext.virtuals.ui.data

import androidx.compose.runtime.Stable
import com.yunext.kmp.resource.HDRes
import com.yunext.virtuals.ui.common.DrawableResourceFactory
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import kotlin.jvm.JvmStatic
import kotlin.random.Random

@Stable
@Serializable
data class DeviceAndStateViewData(
    val name: String,
    val communicationId: String,
    val model: String,
    val status: DeviceStatus,
    val propertyList: List<PropertyData>,
    val eventList: List<EventData>,
    val serviceList: List<ServiceData>,
) : PlatformSerializable {

    companion object {

        @JvmStatic
        @PublishedApi
        internal fun randomList(): List<DeviceAndStateViewData> =
            List(Random.nextInt(10)) {
                DeviceAndStateViewData(
                    name = "虚拟设备 -> $it",
                    communicationId = "通信id -> $it",
                    model = "设备型号 -> $it",
                    status = DeviceStatus.random(), emptyList(), emptyList(), emptyList()
                )
            }

        @PublishedApi
        internal val DEBUG_LIST: List<DeviceAndStateViewData> by lazy {
            List(19) {
                DeviceAndStateViewData(
                    name = "虚拟设备 - $it",
                    communicationId = "通信id - $it",
                    model = "设备型号 - $it",
                    status = DeviceStatus.random(), emptyList(), emptyList(), emptyList()
                )
            }
        }

        @PublishedApi
        @JvmStatic
        internal val DEBUG_ITEM by lazy {
            DeviceAndStateViewData(
                "设备测试01设备测试01设备测试01设备测试01设备测试01设备测试01设备测试01设备测试01",
                "通信id",
                "设备型号",
                DeviceStatus.random(), emptyList(), emptyList(), emptyList()
            )
        }


    }
}

//object DeviceStatusSerializer: JsonContentPolymorphicSerializer<DeviceStatus>(DeviceStatus::class){
//    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<DeviceStatus> {
//        when(element){
//            element.jsonObject->DeviceStatus.serializer()
//        }
//    }
//
//}
//@Serializable()

@Serializable
@Stable
sealed class DeviceStatus(val type: DeviceType, val state: DeviceState) : PlatformSerializable {
    @Stable
    @Serializable
    data object WiFiOnLine : DeviceStatus(DeviceType.WIFI, DeviceState.ONLINE)

    @Stable
    @Serializable
    data object WiFiOffLine : DeviceStatus(DeviceType.WIFI, DeviceState.OFFLINE)

    @Stable
    @Serializable
    data object GPRSOnLine : DeviceStatus(DeviceType.GPRS, DeviceState.ONLINE)

    @Stable
    @Serializable
    data object GPRSOffLine : DeviceStatus(DeviceType.GPRS, DeviceState.OFFLINE)
    companion object {
        internal fun random(): DeviceStatus {
            return listOf(WiFiOnLine, WiFiOffLine, GPRSOnLine, GPRSOffLine).random()
        }
    }
}

val DeviceStatus.str: String
    get() = when (this) {
        DeviceStatus.GPRSOffLine -> "4G离线"
        DeviceStatus.GPRSOnLine -> "4G在线"
        DeviceStatus.WiFiOffLine -> "WiFi离线"
        DeviceStatus.WiFiOnLine -> "WiFi在线"
    }

@OptIn(ExperimentalResourceApi::class)
val DeviceStatus.iconRes: DrawableResourceFactory
    get() = when (this) {
        DeviceStatus.GPRSOffLine -> {
            {
                HDRes.drawable.icon_twins_offline
            }

        }

        DeviceStatus.GPRSOnLine -> {
            {
                HDRes.drawable.icon_twins_4g
            }
        }

        DeviceStatus.WiFiOffLine -> {
            {
                HDRes.drawable.icon_twins_offline
            }
        }

        DeviceStatus.WiFiOnLine -> {
            {
                HDRes.drawable.icon_twins_wifi
            }
        }
    }


@Serializable
enum class DeviceState {
    ONLINE, OFFLINE;
}

@Serializable
enum class DeviceType {
    WIFI, GPRS;
}

