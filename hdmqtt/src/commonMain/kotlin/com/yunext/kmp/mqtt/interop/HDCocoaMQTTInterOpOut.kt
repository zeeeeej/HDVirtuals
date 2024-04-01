package com.yunext.kmp.mqtt.interop

import com.yunext.kmp.common.logger.HDLogger

/**
 * Swift
 * class    HDCocoaMQTT.swift
 * func     initializeMQTT
 * -param    host       emqtt-test.yunext.com
 * -param    port       8904
 * -param    clientId   DEV:tcuf6vn2ohw4mvhb_twins_test_001_cid_0860
 * -param    username   twins_test_001_cid
 * -param    password   2f75802341bd4fa4dc9e3e8b814cd633
 * -param    reference  mqtt链接凭证 clientId+username
 */
typealias InterOp_HDCocoaMQTT_initializeMQTT = (
    host: String, /* host */
    port: UInt, /* port */
    clientId: String, /* clientId */
    username: String, /* username */
    password: String, /* password */
    reference: String,
) -> String

/**
 * Swift
 * class    HDCocoaMQTT.swift
 * func     connect
 *  -param    reference  mqtt链接凭证 clientId+username
 *
 */
typealias InterOp_HDCocoaMQTT_connect = (reference: String) -> Unit

/**
 * Swift
 * class    HDCocoaMQTT.swift
 * func     subscribe
 * -param   topic   /skeleton/tcuf6vn2ohw4mvhb/twins_test_001_cid/down
 * -param    reference  mqtt链接凭证 clientId+username
 */
typealias InterOp_HDCocoaMQTT_subscribe = (topic: String, reference: String  /* topic */) -> Unit

/**
 * Swift
 * class    HDCocoaMQTT.swift
 * func     unSubscribe
 * -param   topic   /skeleton/tcuf6vn2ohw4mvhb/twins_test_001_cid/down
 */
typealias InterOp_HDCocoaMQTT_unSubscribe = (topic: String, reference: String  /* topic */) -> Unit

/**
 * Swift
 * class    HDCocoaMQTT.swift
 * func     publish
 * -param   topic       /skeleton/tcuf6vn2ohw4mvhb/twins_test_001_cid/down
 * -param   message     hello swift
 * -param    reference  mqtt链接凭证 clientId+username
 */
typealias InterOp_HDCocoaMQTT_publish = (
    topic: String, /* topic */
    message: String, /* message */
    reference: String,
) -> Unit

/**
 * Swift
 * class    HDCocoaMQTT.swift
 * func     disconnect
 * -param    reference  mqtt链接凭证 clientId+username
 */
typealias InterOp_HDCocoaMQTT_disconnect = (reference: String) -> Unit


/**
 * kotlin调用swift
 */
object HDCocoaMQTTInterOpOut {
    var initializeMQTT: InterOp_HDCocoaMQTT_initializeMQTT? = null
        private set
    var connect: InterOp_HDCocoaMQTT_connect? = null
        private set
    var subscribe: InterOp_HDCocoaMQTT_subscribe? = null
        private set
    var unSubscribe: InterOp_HDCocoaMQTT_unSubscribe? = null
        private set
    var publish: InterOp_HDCocoaMQTT_publish? = null
        private set
    var disconnect: InterOp_HDCocoaMQTT_disconnect? = null
        private set

    //<editor-fold desc="register callback int swift ! callback作为中转将kotlin数据传递到swift">
    // NOTICE : DO NOT CALL IN KOTLIN !!!
    fun initializeMQTTInSwift(initializeMQTT: InterOp_HDCocoaMQTT_initializeMQTT) {
        HDLogger.d("mqtt", "HDCocoaMQTTInterOpOut::initializeMQTTInSwift $initializeMQTT")
        this.initializeMQTT = initializeMQTT
        HDLogger.d("mqtt", "HDCocoaMQTTInterOpOut::initializeMQTTInSwift ${this.initializeMQTT}")
    }

    fun connectInSwift(connect: InterOp_HDCocoaMQTT_connect) {
        this.connect = connect
    }

    fun subscribeInSwift(subscribe: InterOp_HDCocoaMQTT_subscribe) {
        this.subscribe = subscribe
    }

    fun unSubscribeInSwift(unSubscribe: InterOp_HDCocoaMQTT_unSubscribe) {
        this.unSubscribe = unSubscribe
    }

    fun publishInSwift(publish: InterOp_HDCocoaMQTT_publish) {
        this.publish = publish
    }

    fun disconnectInSwift(disconnect: InterOp_HDCocoaMQTT_disconnect) {
        this.disconnect = disconnect
    }

    fun clearInSwift() {
        initializeMQTT = null
        connect = null
        subscribe = null
        unSubscribe = null
        publish = null
        disconnect = null
    }

    // NOTICE : DO NOT CALL IN KOTLIN !!!
    //</editor-fold>
}