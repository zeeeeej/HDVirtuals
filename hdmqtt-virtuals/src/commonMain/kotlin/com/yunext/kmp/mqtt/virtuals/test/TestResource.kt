package com.yunext.kmp.mqtt.virtuals.test

import com.yunext.kmp.common.util.currentTime
import com.yunext.kmp.common.util.hdUUID
import com.yunext.kmp.mqtt.data.HDMqttParam

internal object TestResource {
    private val KEY_TLS_FOR_MQTTx = """
    -----BEGIN CERTIFICATE-----
    MIIDrzCCApegAwIBAgIQCDvgVpBCRrGhdWrJWZHHSjANBgkqhkiG9w0BAQUFADBh
    MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3
    d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBD
    QTAeFw0wNjExMTAwMDAwMDBaFw0zMTExMTAwMDAwMDBaMGExCzAJBgNVBAYTAlVT
    MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5j
    b20xIDAeBgNVBAMTF0RpZ2lDZXJ0IEdsb2JhbCBSb290IENBMIIBIjANBgkqhkiG
    9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4jvhEXLeqKTTo1eqUKKPC3eQyaKl7hLOllsB
    CSDMAZOnTjC3U/dDxGkAV53ijSLdhwZAAIEJzs4bg7/fzTtxRuLWZscFs3YnFo97
    nh6Vfe63SKMI2tavegw5BmV/Sl0fvBf4q77uKNd0f3p4mVmFaG5cIzJLv07A6Fpt
    43C/dxC//AH2hdmoRBBYMql1GNXRor5H4idq9Joz+EkIYIvUX7Q6hL+hqkpMfT7P
    T19sdl6gSzeRntwi5m3OFBqOasv+zbMUZBfHWymeMr/y7vrTC0LUq7dBMtoM1O/4
    gdW7jVg/tRvoSSiicNoxBN33shbyTApOB6jtSj1etX+jkMOvJwIDAQABo2MwYTAO
    BgNVHQ8BAf8EBAMCAYYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUA95QNVbR
    TLtm8KPiGxvDl7I90VUwHwYDVR0jBBgwFoAUA95QNVbRTLtm8KPiGxvDl7I90VUw
    DQYJKoZIhvcNAQEFBQADggEBAMucN6pIExIK+t1EnE9SsPTfrgT1eXkIoyQY/Esr
    hMAtudXH/vTBH1jLuG2cenTnmCmrEbXjcKChzUyImZOMkXDiqw8cvpOp/2PV5Adg
    06O/nVsJ8dWO41P0jmP6P6fbtGbfYmbW0W5BjfIttep3Sp+dWOIrWcBAI+0tKIJF
    PnlUkiaY4IBIqDfv8NZ5YBberOgOzW6sRBc4L0na4UU+Krk2U886UAb3LujEV0ls
    YSEY1QSteDwsOoBrp+uvFRTp2InBuThs4pFsiv9kuXclVzDAGySj4dzp30d8tbQk
    CAUw7C29C79Fv1C5qfPrmAESrciIxpg0X40KPMbp1ZWVbd4=
    -----END CERTIFICATE-----
    """.trimIndent()

    internal val testParam2 = HDMqttParam(
        username = "laputa",
        password = "963210xx",
        clientId = "mqttx_0eb2fd3d_kmp_${currentTime()}",
        url = "ssl://127.0.0.1:8883",
        ssl = false, port = "8883", shortUrl = "127.0.0.1", scheme = "ssl", tls = null
    )

    internal val testParam = HDMqttParam(
        username = "fe495a3be9c7",
        password = "1a430ea4f7edfb28200de2130efdb739",
        clientId = "DEV:QR-12TRWQ4_fe495a3be9c7_${currentTime()}",
        url = "ssl://emqtt-test.yunext.com:8881",
        ssl = false, port = "8881", shortUrl = "emqtt-test.yunext.com", scheme = "ssl", tls = null
    )

    internal val mqttXParam = HDMqttParam(
        username = "zeej",
        password = "963210xx",
        clientId = "kmp_jvm_${currentTime()}",
        url = "ssl://o5dae913.cn-hangzhou.emqx.cloud:15925",
        ssl = false,
        port = "15925",
        shortUrl = "o5dae913.cn-hangzhou.emqx.cloud",
        scheme = "ssl",
        tls = KEY_TLS_FOR_MQTTx
    )

    internal val debugParam: HDMqttParam
        get() = twins_test_001

    private val twins_test_001 = HDMqttParam(
        username = "twins_test_001_cid",
        password = "42e74d901c11371d9e8362501736c2d0",
        //clientId = "DEV:tcuf6vn2ohw4mvhb_twins_test_001_cid_${currentTime().toString().take(4)}",
        clientId = "DEV:tcuf6vn2ohw4mvhb_twins_test_001_cid_1133",
        url = "ssl://emqtt-test.yunext.com:8904",
        ssl = true, port = "8904", shortUrl = "emqtt-test.yunext.com", scheme = "ssl", tls = null
    )

    // [username]twins_test_001_cid ## [password]3415ee6f6d02c7b112eae4ae472bcaba ## [clientId]DEV:tcuf6vn2ohw4mvhb_twins_test_001_cid_aTpJ ## [url]ssl://emqtt-test.yunext.com:8904 ## [ssl]true
    // [username]twins_test_001_cid ## [password]39eb3141c562be8e7b52fea13d0bdd1d ## [clientId]DEV:tcuf6vn2ohw4mvhb_twins_test_001_cid_5987 ## [url]ssl://emqtt-test.yunext.com:8904 ## [ssl]true
    // [username]twins_test_001_cid ## [password]ebb0c43c1703c74bf692b1b37803a191 ## [clientId]DEV:tcuf6vn2ohw4mvhb_twins_test_001_cid_4015 ## [url]ssl://emqtt-test.yunext.com:8904 ## [ssl]true
    // [username]twins_test_001_cid ## [password]39eb3141c562be8e7b52fea13d0bdd1d ## [clientId]tcuf6vn2ohw4mvhb_twins_test_001_cid_5987 ## [url]ssl://emqtt-test.yunext.com:8904 ## [ssl]true
    internal const val TOPIC_UP = "/skeleton/QR-12TRWQ4/fe495a3be9c7/up"
    internal const val TOPIC_DOWN = "/skeleton/QR-12TRWQ4/fe495a3be9c7/down"
}