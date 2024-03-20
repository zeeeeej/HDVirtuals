package com.yunext.kmp.mqtt.virtuals.protocol

import kotlin.jvm.JvmStatic

sealed interface ProtocolMQTTCmd {
    val cmd: String

    companion object {

        @JvmStatic
        fun from(text: String): ProtocolMQTTCmd {
            return when (text) {
                ReportCmd.cmd -> ReportCmd
                SetCmd.cmd -> SetCmd
                OnlineCmd.cmd -> OnlineCmd
                InfoUpCmd.cmd -> InfoUpCmd
                DataCmd.cmd -> DataCmd
                // else -> throw TslCmdException("非默认支持的命令，一般常见于service。 cmd = $text")
                else -> ServiceCmd(cmd = text) // 对于设备端来说，其他的都认为是服务器调用服务，并且cmd值为服务的identifier
            }
        }
    }
}

// cmds
data object ReportCmd : ProtocolMQTTCmd {
    override val cmd: String
        get() = "report"
}

data object SetCmd : ProtocolMQTTCmd {
    override val cmd: String
        get() = "set"
}

data object OnlineCmd : ProtocolMQTTCmd {
    override val cmd: String
        get() = "online"
}

data object InfoUpCmd : ProtocolMQTTCmd {
    override val cmd: String
        get() = "info"
}

data object DataCmd : ProtocolMQTTCmd {
    override val cmd: String
        get() = "data"
}

data object TimestampCmd : ProtocolMQTTCmd {
    override val cmd: String
        get() = "timestamp"
}

data class ServiceCmd(override val cmd: String) : ProtocolMQTTCmd
