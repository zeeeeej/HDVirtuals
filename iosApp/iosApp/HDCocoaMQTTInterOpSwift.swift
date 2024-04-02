//
//  HDCocoaMQTTInterOpSwift.swift
//  iosApp
//
//  Created by 项鹏乐 on 2024/3/29.
//  Copyright © 2024 orgName. All rights reserved.
//

import ComposeApp
// import shared
import CocoaMQTT

class HDCocoaMQTTInterOpSwift : MqttEeventDelegate{
    private var debug:Bool = false
    private let TAG = "HDCocoaMQTTx"
    // mqtt管理
    private var hDCocoaMQTTMap  = [String:HDCocoaMQTT]()
    // kotlin interop out 回调设置类 kotlin call swift
    private let hDCocoaMQTTInterOpOut = HDCocoaMQTTInterOpOut()
    // kotlin interop in 回调设置类 swift callback
    private let hDCocoaMQTTInterOpIn = HDCocoaMQTTInterOpIn()
    
    private func tryGetClient(reference:String)->HDCocoaMQTT?{
        return hDCocoaMQTTMap[reference]
    }
    
    private func add(ref:String,client:HDCocoaMQTT){
        let exsits = tryGetClient(reference: ref)
        if(exsits != nil){
            exsits?.disconnect()
            exsits?.delegate = nil
        }
        hDCocoaMQTTMap[ref] = client
    }
    
    private func remove(ref:String){
//        let exsits = tryGetClient(reference: ref)
//        if(exsits != nil){
//            exsits?.disconnect()
//            exsits?.delegate = nil
//        }
        hDCocoaMQTTMap.removeValue(forKey: ref)
    }
   
    // ########### delegate a ###########
    func onConnectionStatus(isConnected: Bool,client: HDCocoaMQTT) {
       
        
        
        hDCocoaMQTTInterOpIn.onConnectStateChangedCallInSwift(reference: client.reference(), connect:isConnected)
    }
    
    func onSubscibe(topics: NSDictionary,client: HDCocoaMQTT) {
        
    }
    
    func onUnsubscribe(topics: [String],client: HDCocoaMQTT) {
        
    }
    
    func onMessageArrived(message: CocoaMQTTMessage,client: HDCocoaMQTT) {
        let ref = client.reference()
        let topic = message.topic
        let payload:[UInt8] = message.payload
        let qos = message.qos.rawValue
        let retained = message.retained
        let dup = message.duplicated
        self.TRACE("======收到消息 a=====")
        self.TRACE("topic            :   \(message.topic)")
        self.TRACE("payload          :   \(message.payload.count)")
        self.TRACE("description      :   \(String(describing: message.string?.description))")
        self.TRACE("payload.str      :   \(bytesToIntString(payload))")
        let py = KotlinByteArray.from(payload)
        hDCocoaMQTTInterOpIn.onMessageArrivedCallInSwift(reference: ref, topic: topic, qos: Int32(qos), payload: py, retained: retained, dup: dup)
        self.TRACE("======收到消息 z=====")
    }
    // ########### delegate z ###########
    
   
    
    // 初始化kotlin callback
    func initializeInterOp(){
        
        // 初始化
        hDCocoaMQTTInterOpOut.initializeMQTTInSwift{
            (host,port,clientId,username,password,reference) -> String in
            self.displayClients()
            self.TRACE("""
                  来自Kotlin的方法initializeMQTTInSwift参数
                  host          :       \(host)
                  port          :       \(port)
                  clientId      :       \(clientId)
                  username      :       \(username)
                  password      :       \(password)
                  reference     :       \(reference)
            """)
            let ref = clientId + "@" + username
            if(self.tryGetClient(reference: ref) != nil){
                self.TRACE("已经存在：\(ref)")
                return ""
            }
            self.TRACE("创建client")
            let client = HDCocoaMQTT()
            client.initializeMQTT(host,UInt16(truncating: port),clientId,username,password,ref)
            client.delegate = self
            client.connect()
            self.hDCocoaMQTTMap[ref] = client
            self.displayClients()
            return ref
        }
        
        // 链接
        hDCocoaMQTTInterOpOut.connectInSwift{
            reference in
            self.checkClient(reference: reference){
                client in
                self.TRACE("""
                  来自Kotlin的方法connectInSwift参数
                  reference     :       \(reference)
            """)
                client.connect()
            }
        }
        
        // 注册
        hDCocoaMQTTInterOpOut.subscribeInSwift{
            topic,reference in
            self.checkClient(reference: reference){
                client in
                self.TRACE("""
                  来自Kotlin的方法subscribeInSwift参数
                  topic         :       \(topic)
                  reference     :       \(reference)
            """)
                client.subscribe(topic:topic)
            }
        }
        
        // 发布
        hDCocoaMQTTInterOpOut.publishInSwift{
            topic,message,reference in
            self.checkClient(reference: reference){
                client in
                self.TRACE("""
                  来自Kotlin的方法publishInSwift参数
                  topic         :       \(topic)
                  message       :       \(message)
                  reference     :       \(reference)
            """)
                client.publish(topic:topic,with : message)
            }
        }
        
        // 反注册
        hDCocoaMQTTInterOpOut.unSubscribeInSwift{
            topic,reference in
            self.checkClient(reference: reference){
                client in
                let msg = """
                  来自Kotlin的方法unSubscribeInSwift参数
                  topic         :       \(topic)
                  reference     :       \(reference)
            """
                self.TRACE(msg)
                client.unSubscribe(topic:topic )
            }
        }
        
        // 断开链接
        hDCocoaMQTTInterOpOut.disconnectInSwift{
            reference in
            self.checkClient(reference: reference){
                client in
                self.TRACE("""
                      来自Kotlin的方法disconnectInSwift参数
                      reference     :       \(reference)
                """)
                self.remove(ref: client.reference())
                client.disconnect()
                client.delegate = nil
            }
        }
    }
    
    private func checkClient(reference:String,block:(HDCocoaMQTT)->Void){
        if let client = tryGetClient(reference: reference) {
            block(client)
        }else{
            TRACE("不存在client：\(reference) ")
        }
    }
}

extension HDCocoaMQTTInterOpSwift{
    func TRACE(_ msg:String){
        if(!debug){
            return
        }
        HDLogX.shared().d(tag: TAG, message: msg)
    }
    
    func displayClients(){
        TRACE("###当前MQTT客户端###")
        for (ref,client) in self.hDCocoaMQTTMap{
            TRACE("-->\(ref)")
        }
        TRACE("***当前MQTT客户端***")
    }
}


