package com.yunext.kmp.mqtt.virtuals.test

import com.yunext.kmp.common.logger.HDLogger
import com.yunext.kmp.mqtt.HDMqttClient
import com.yunext.kmp.mqtt.core.OnHDMqttActionListener
import com.yunext.kmp.mqtt.createHdMqttClient
import com.yunext.kmp.mqtt.hdMqttConnect
import com.yunext.kmp.mqtt.hdMqttDisconnect
import com.yunext.kmp.mqtt.hdMqttInit
import com.yunext.kmp.mqtt.hdMqttPublish
import com.yunext.kmp.mqtt.hdMqttState
import com.yunext.kmp.mqtt.hdMqttSubscribeTopic

class MQTTVirtualsDemo {
    private var mqttClient: HDMqttClient? = null
    fun disconnect() {
        mqttClient?.hdMqttDisconnect()
    }

    @OptIn(ExperimentalStdlibApi::class)

    fun init() {

        val content = "hello kmp !".encodeToByteArray()
        val topic = TestResource.TOPIC_UP
        mqttClient = createHdMqttClient().also { client ->
            client.hdMqttInit()
            client.hdMqttConnect(TestResource.mqttXParam, object : OnHDMqttActionListener {
                override fun onSuccess(token: Any?) {
                    HDLogger.d("HDMQTTDemo", "connect:onSuccess")

                    client.hdMqttSubscribeTopic(topic = TestResource.TOPIC_UP,
                        object : OnHDMqttActionListener {
                            override fun onSuccess(token: Any?) {
                                HDLogger.d("HDMQTTDemo", "subscribeTopic success!")
                                client.hdMqttPublish(
                                    topic,
                                    content,
                                    1,
                                    false,
                                    object : OnHDMqttActionListener {
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
                    )
                }

                override fun onFailure(token: Any?, exception: Throwable?) {
                    HDLogger.e("HDMQTTDemo", "connect:onFailure :$exception")
                }
            }, onHDMqttStateChangedListener = { _, state ->
                HDLogger.e("HDMQTTDemo", "$$$$ connect:state :$state $$$$")
            }, onHDMqttMessageChangedListener = {
                _,topic,message->
                HDLogger.e("HDMQTTDemo", "topic:$topic message:$message")
            })

        }


    }

    fun publish() {
        val topic = TestResource.TOPIC_DOWN
        val content = "hello kmp ! from client:${mqttClient?.hdMqttState}".encodeToByteArray()
        mqttClient?.hdMqttPublish(topic, content, 1, false, object : OnHDMqttActionListener {
            override fun onSuccess(token: Any?) {
                HDLogger.e("HDMQTTDemo", "publish:onSuccess")
            }

            override fun onFailure(token: Any?, exception: Throwable?) {
                HDLogger.e("HDMQTTDemo", "publish:onFailure :$exception")
            }

        })
    }
}