package com.yunext.kmp.mqtt.test

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.core.HDMqttActionListener
import com.yunext.kmp.mqtt.core.HDMqttClient
import com.yunext.kmp.mqtt.createHdMqttClient
import com.yunext.kmp.mqtt.hdMqttClear
import com.yunext.kmp.mqtt.hdMqttConnect
import com.yunext.kmp.mqtt.hdMqttDisconnect
import com.yunext.kmp.mqtt.hdMqttInit
import com.yunext.kmp.mqtt.hdMqttPublish
import com.yunext.kmp.mqtt.hdMqttSubscribeTopic

class HDMQTTDemo {
    private var mqttClient: HDMqttClient? = null
    fun disconnect() {
        mqttClient?.disconnect()
        mqttClient?.clear()
    }

    @OptIn(ExperimentalStdlibApi::class)

    fun init() {

        val content = "hello kmp !".encodeToByteArray()
        val topic = TestResource.TOPIC_UP
        mqttClient = createHdMqttClient().also { client ->
            client.init()
            client.connect(TestResource.mqttXParam, object : HDMqttActionListener {
                override fun onSuccess(token: Any?) {
                    HDLogger.d("HDMQTTDemo", "connect:onSuccess")

                    client.subscribeTopic(topic = TestResource.TOPIC_UP,
                        object : HDMqttActionListener {
                            override fun onSuccess(token: Any?) {
                                HDLogger.d("HDMQTTDemo", "subscribeTopic success!")
                                client.publish(
                                    topic,
                                    content,
                                    1,
                                    false,
                                    object : HDMqttActionListener {
                                        override fun onSuccess(token: Any?) {
                                            HDLogger.d("HDMQTTDemo", "publish success!")
                                        }

                                        override fun onFailure(token: Any?, exception: Throwable?) {
                                            HDLogger.e("HDMQTTDemo", "publish fail$exception!")
                                        }

                                    })
                            }

                            override fun onFailure(token: Any?, exception: Throwable?) {
                                HDLogger.e("HDMQTTDemo", "subscribeTopic fail$exception!")
                            }

                        }
                    ) { mqttClient, topic, message ->
                        HDLogger.d(
                            "HDMQTTDemo",
                            "onChanged $topic ${message.payload.toHexString(HexFormat.Default)}}!"
                        )
                    }
                }

                override fun onFailure(token: Any?, exception: Throwable?) {
                    HDLogger.e("HDMQTTDemo", "connect:onFailure :$exception")
                }
            })

        }


    }

    fun publish() {
        val topic = TestResource.TOPIC_DOWN
        val content = "hello kmp ! from client:${mqttClient?.clientId}".encodeToByteArray()
        mqttClient?.publish(topic, content, 1, false, object : HDMqttActionListener {
            override fun onSuccess(token: Any?) {
                HDLogger.e("HDMQTTDemo", "publish:onSuccess")
            }

            override fun onFailure(token: Any?, exception: Throwable?) {
                HDLogger.e("HDMQTTDemo", "publish:onFailure :$exception")
            }

        })
    }
}

class HDMQTT2Demo {
    private var mqttClient: HDMqttClient? = null
    fun disconnect() {
        mqttClient?.hdMqttDisconnect()
        mqttClient?.hdMqttClear()
    }

    @OptIn(ExperimentalStdlibApi::class)

    fun init() {

        val content = "hello kmp !".encodeToByteArray()
        val topic = TestResource.TOPIC_UP
        mqttClient = createHdMqttClient().also { client ->
            client.hdMqttInit()
            client.hdMqttConnect(TestResource.mqttXParam, object : HDMqttActionListener {
                override fun onSuccess(token: Any?) {
                    HDLogger.d("HDMQTTDemo", "connect:onSuccess")

                    client.hdMqttSubscribeTopic(topic = TestResource.TOPIC_UP,
                        object : HDMqttActionListener {
                            override fun onSuccess(token: Any?) {
                                HDLogger.d("HDMQTTDemo", "subscribeTopic success!")
                                client.hdMqttPublish(
                                    topic,
                                    content,
                                    1,
                                    false,
                                    object : HDMqttActionListener {
                                        override fun onSuccess(token: Any?) {
                                            HDLogger.d("HDMQTTDemo", "publish success!")
                                        }

                                        override fun onFailure(token: Any?, exception: Throwable?) {
                                            HDLogger.e("HDMQTTDemo", "publish fail$exception!")
                                        }

                                    })
                            }

                            override fun onFailure(token: Any?, exception: Throwable?) {
                                HDLogger.e("HDMQTTDemo", "subscribeTopic fail$exception!")
                            }

                        }
                    ) { mqttClient, topic, message ->
                        HDLogger.d(
                            "HDMQTTDemo",
                            "onChanged $topic ${message.payload.toHexString(HexFormat.Default)}}!"
                        )
                    }
                }

                override fun onFailure(token: Any?, exception: Throwable?) {
                    HDLogger.e("HDMQTTDemo", "connect:onFailure :$exception")
                }
            })

        }


    }

    fun publish() {
        val topic = TestResource.TOPIC_DOWN
        val content = "hello kmp ! from client:${mqttClient?.state}".encodeToByteArray()
        mqttClient?.hdMqttPublish(topic, content, 1, false, object : HDMqttActionListener {
            override fun onSuccess(token: Any?) {
                HDLogger.e("HDMQTTDemo", "publish:onSuccess")
            }

            override fun onFailure(token: Any?, exception: Throwable?) {
                HDLogger.e("HDMQTTDemo", "publish:onFailure :$exception")
            }

        })
    }
}