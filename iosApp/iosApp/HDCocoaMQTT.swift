//
//  HDCocoaMQTT.swift
//  iosApp
//
//  Created by 项鹏乐 on 2024/3/28.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import CocoaMQTT

class HDCocoaMQTT{
    private var mqttxClient :CocoaMQTT?
    
    func testMqtt(){
        if(mqttxClient != nil){
            mqttxClient = nil
        }
//        let clientID = "CocoaMQTT_\(UUID().uuidString)"
//        let username = "zeej"
//        let password = "963210xx"
//        let url = "o5dae913.cn-hangzhou.emqx.cloud"
//        let port:UInt16 = 15925
        
        let clientID = "DEV:tcuf6vn2ohw4mvhb_twins_test_001_cid_0860"
        let username = "twins_test_001_cid"
        let password = "2f75802341bd4fa4dc9e3e8b814cd633"
        let url = "emqtt-test.yunext.com"
        let port:UInt16 = 8904
        
        print("开始链接")
        print("url      :   \(url)")
        print("port     :   \(port)")
        print("clientID :   \(clientID)")
        print("username :   \(username)")
        print("password :   \(password)")
        ///MQTT 3.1.1
        mqttxClient = CocoaMQTT(clientID: clientID, host: url, port: port)
        mqttxClient?.username = username
        mqttxClient?.password = password
        //        mqtt.willMessage = CocoaMQTTMessage(topic: "/will", string: "dieout")
        //
        mqttxClient?.enableSSL = true
        mqttxClient?.logLevel = .debug
        mqttxClient?.keepAlive = 60
        mqttxClient?.delegate = self
        
        //             mqtt.didConnectAck = {
        //                 _,ack in
        //                 print("=>didConnectAck")
        //             }
        //             mqtt.didDisconnect = {
        //                 _,acl in
        //                 print("=>didDisconnect")
        //             }
        //             mqtt.didReceiveMessage = { _, _, _ in
        //                 print("=>didReceiveMessage")
        //             }
        
        //             mqtt.autoReconnect = true
        //             mqtt.cleanSession = true
        
        //mqtt.allowUntrustCACertificate = true
        let r = mqttxClient?.connect()
        if let r{
            print("链接sucess")
        }else{
            print("链接fail")
        }
    }
    
    func connect(){
        mqttxClient?.connect()
    }
    
    func subscribe(topic:String){
        mqttxClient?.subscribe(topic, qos: .qos1)
    }
    
    func publish(topic:String,with message: String) {
        mqttxClient?.publish(topic, withString: message, qos: .qos1)
    }
    
    func disconnect() {
        mqttxClient?.disconnect()
    }
}

extension HDCocoaMQTT:CocoaMQTTDelegate{
    func mqtt(_ mqtt: CocoaMQTT, didConnectAck ack: CocoaMQTTConnAck) {
        print("mqtt CocoaMQTTConnAck")
        if ack == .accept {
            print("mqtt CocoaMQTTConnAck ok")
            mqttxClient = mqtt
        }
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishMessage message: CocoaMQTTMessage, id: UInt16) {
        print("mqtt CocoaMQTTMessage CocoaMQTTMessage")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishAck id: UInt16) {
        print("mqtt didPublishAck")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didReceiveMessage message: CocoaMQTTMessage, id: UInt16) {
        print("mqtt didReceiveMessage CocoaMQTTMessage")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopics success: NSDictionary, failed: [String]) {
        print("mqtt didSubscribeTopics")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didUnsubscribeTopics topics: [String]) {
        print("mqtt didUnsubscribeTopics")
    }
    
    func mqttDidPing(_ mqtt: CocoaMQTT) {
        print("mqttDidPing")
    }
    
    func mqttDidReceivePong(_ mqtt: CocoaMQTT) {
        print("mqttDidReceivePong")
    }
    
    func mqttDidDisconnect(_ mqtt: CocoaMQTT, withError err: Error?) {
        print("mqttDidDisconnect")
    }
    
    
}
