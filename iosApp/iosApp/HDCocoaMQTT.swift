//
//  HDCocoaMQTT.swift
//  iosApp
//
//  Created by 项鹏乐 on 2024/3/28.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import CocoaMQTT


protocol MqttEeventDelegate: AnyObject {
    func onConnectionStatus(isConnected: Bool,client:HDCocoaMQTT)
    func onSubscibe(topics: NSDictionary,client:HDCocoaMQTT)
    func onUnsubscribe(topics: [String],client:HDCocoaMQTT)
    func onMessageArrived(message: CocoaMQTTMessage,client:HDCocoaMQTT)
}

class HDCocoaMQTT{
    private let TAG = "HDCocoaMQTT"
    private let debug:Bool = false
    private var mqttxClient :CocoaMQTT?
    private var host: String!
    private var topic: String!
    private var username: String!
    private var password: String!
    private var ref:String!
    
    var isConnected: Bool = false
    weak var delegate : MqttEeventDelegate? = nil

    // MARK: Shared Instance

    private static let _shared = HDCocoaMQTT()

    // MARK: - Accessors

    class func shared() -> HDCocoaMQTT {
        return _shared
    }
    
    func initializeMQTT(_ host:String = "emqtt-test.yunext.com",
    _ port:UInt16=8904,
    _ clientID:String="DEV:tcuf6vn2ohw4mvhb_twins_test_001_cid_0860",
    _ username:String="twins_test_001_cid",
    _ password:String="2f75802341bd4fa4dc9e3e8b814cd633",
                        _ ref :String
    ){
        if(mqttxClient != nil){
            TRACE("清理CocoaMQTT")
            mqttxClient = nil
        }
        TRACE("初始化initializeMQTT...")
        TRACE("url      :   \(host)")
        TRACE("port     :   \(port)")
        TRACE("clientID :   \(clientID)")
        TRACE("username :   \(username)")
        TRACE("password :   \(password)")
        self.host = host
        self.username = username
        self.password = password
        self.ref = ref
        
        ///MQTT 3.1.1
        mqttxClient = CocoaMQTT(clientID: clientID, host: host, port: port)
        mqttxClient?.username = username
        mqttxClient?.password = password
        // mqtt.willMessage = CocoaMQTTMessage(topic: "/will", string: "dieout")
        mqttxClient?.enableSSL = true
        //mqttxClient?.logLevel = .debug
        mqttxClient?.keepAlive = 60
        mqttxClient?.delegate = self
        TRACE("初始化initializeMQTT完毕!")
    }
    
    func connect(){
        TRACE("开始链接...")
        let r = mqttxClient?.connect()
        if r != nil{
            TRACE("链接sucess!")
        }else{
            TRACE("链接fail!")
        }
    }
    
    func subscribe(topic:String){
        TRACE("开始注册topic:\(topic)")
        mqttxClient?.subscribe(topic, qos: .qos1)
    }
    
    func unSubscribe(topic:String){
        TRACE("开始反注册topic:\(topic)")
        mqttxClient?.unsubscribe(topic)
    }
    
    func publish(topic:String,with message: String) {
        TRACE("发送数据topic:\(topic) \(message)")
        mqttxClient?.publish(topic, withString: message, qos: .qos1)
    }
    
    func disconnect() {
        TRACE("断开连接")
        mqttxClient?.disconnect()
        delegate = nil
    }
    
    func currentHost() -> String? {
        return host
    }
    
    func reference()->String {
        return ref
    }
}

extension HDCocoaMQTT:CocoaMQTTDelegate{
    func mqtt(_ mqtt: CocoaMQTT, didConnectAck ack: CocoaMQTTConnAck) {
        TRACE("mqtt:CocoaMQTTConnAck ack:\(ack)")
        if ack == .accept {
            mqttxClient = mqtt
            isConnected = true
            delegate?.onConnectionStatus(isConnected: isConnected,client: self)
        }
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishMessage message: CocoaMQTTMessage, id: UInt16) {
        TRACE("mqtt:CocoaMQTTMessage message:\(String(describing: message.string?.description)),id:\(id)")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishAck id: UInt16) {
        TRACE("mqtt didPublishAck id:\(id)")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didReceiveMessage message: CocoaMQTTMessage, id: UInt16) {
        TRACE("mqtt didReceiveMessage message:\(String(describing: message.string?.description)),id:\(id)")
        
            delegate?.onMessageArrived(message: message,client: self)
        
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopics success: NSDictionary, failed: [String]) {
        TRACE("mqtt didSubscribeTopics success:\(success),failed:\(failed)")
        delegate?.onSubscibe(topics: success,client: self)
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didUnsubscribeTopics topics: [String]) {
        TRACE("mqtt didUnsubscribeTopics topics:\(topics)")
        delegate?.onUnsubscribe(topics: topics,client: self)
    }
    
    func mqttDidPing(_ mqtt: CocoaMQTT) {
        TRACE("mqttDidPing >>>")
    }
    
    func mqttDidReceivePong(_ mqtt: CocoaMQTT) {
        TRACE("mqttDidReceivePong <<<")
    }
    
    func mqttDidDisconnect(_ mqtt: CocoaMQTT, withError err: Error?) {
        TRACE("mqttDidDisconnect withError:\(String(describing: err?.localizedDescription))")
        isConnected = false
        delegate?.onConnectionStatus(isConnected: isConnected,client: self)
    }
}

extension HDCocoaMQTT{
    
    func TRACE(_ message:String,fun:String = #function){
        if(!debug){
            return
        }
        let names = fun.components(separatedBy: ":")
        var prettyName:String
        if names.count == 1 {
            prettyName = names[0]
        }else {
            prettyName = names[1]
        }
        if fun == "mqttDidDisconnect(_:withError:)" {
            prettyName = "didDisconect"
        }
        let tag = TAG
        let msessage = ("[TRACE][\(prettyName)]:\(message)")
        HDLogX.shared().d(tag: tag, message: msessage)
    }
}
