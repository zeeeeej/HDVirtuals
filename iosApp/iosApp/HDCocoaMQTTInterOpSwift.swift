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
    private var curReference :String? = nil
    
    func onConnectionStatus(isConnected: Bool) {
        let ref = curReference!
        hDCocoaMQTTInterOpIn.onConnectStateChangedCallInSwift(reference: ref, connect:isConnected)
    }
    
    func onSubscibe(topics: NSDictionary) {
        
    }
    
    func onUnsubscribe(topics: [String]) {
        
    }
    
    func onMessageArrived(message: CocoaMQTTMessage) {
        let ref = curReference!
        let topic = message.topic
        let payload:[UInt8] = message.payload
        let qos = message.qos.rawValue
        let retained = message.retained
        let dup = message.duplicated
//        let payLoadKt:[Int8]
        // process(buffer.map{ x in UInt8(x) }) // OK
//        let newData :[Int] = payload.map{x in Int(x)}
//        var json  = ""
//        if  let m = message.string?.description  {
//            json = m
//        }
//        let json2 = bytesToIntString(payload)
//        guard let json2 = String(data: Data(payload), encoding: .utf8) else { return; default "" }
//        let count = payload.length / sizeof(UInt8)
//        var array = [UInt8](count: count, repeatedValue: 0)
//        payload.getBytes(&array, length:count * sizeof(UInt8))
//        String.stringWithBytes(payload, encoding: NSUTF8StringEncoding)
//        
        print("======收到消息 a=====")
        print("topic            :   \(message.topic)")
        print("payload          :   \(message.payload.count)")
        print("description      :   \(String(describing: message.string?.description))")
        print("payload.str      :   \(bytesToIntString(payload))")
//        print("json             :   \(json)")
//        print("newData          :   \(newData.count)")
        //let payload:[UInt8] = stringToBytes(json)
     
        let py = KotlinByteArray.from(payload)
        hDCocoaMQTTInterOpIn.onMessageArrivedCallInSwift(reference: ref, topic: topic, qos: Int32(qos), payload: py, retained: retained, dup: dup)
        print("======收到消息 z=====")
    }
    
    // mqtt管理 单利
    private let hdCocoaMQTT = HDCocoaMQTT.shared()
    // kotlin interop out 回调设置类 kotlin call swift
    private let hDCocoaMQTTInterOpOut = HDCocoaMQTTInterOpOut()
    // kotlin interop in 回调设置类 swift callback
    private let hDCocoaMQTTInterOpIn = HDCocoaMQTTInterOpIn()
    
   
    //private let vm = VM()
    
    // 初始化kotlin callback
    func initializeInterOp(){
        hdCocoaMQTT.delegate = self
        
        // 初始化
        hDCocoaMQTTInterOpOut.initializeMQTTInSwift{
            (host,port,clientId,username,password,reference) -> String in
            print("""
                  来自Kotlin的方法initializeMQTTInSwift参数
                  host          :       \(host)
                  port          :       \(port)
                  clientId      :       \(clientId)
                  username      :       \(username)
                  password      :       \(password)
                  reference     :       \(reference)
            """)
            self.hdCocoaMQTT.initializeMQTT(host,UInt16(truncating: port),clientId,username,password)
            self.hdCocoaMQTT.connect()
            let ref = clientId+username
            self.curReference = ref
            return ref//"来自initializeMQTTInSwift swift返回"
        }
        // 链接
        hDCocoaMQTTInterOpOut.connectInSwift{
            reference in
            print("""
                  来自Kotlin的方法connectInSwift参数
                  reference     :       \(reference)
            """)
            self.hdCocoaMQTT.connect()
        }
        
        // 注册
        hDCocoaMQTTInterOpOut.subscribeInSwift{
            topic,reference in
            print("""
                  来自Kotlin的方法subscribeInSwift参数
                  topic         :       \(topic)
                  reference     :       \(reference)
            """)
            self.hdCocoaMQTT.subscribe(topic:topic)
        }
        
        // 发布
        hDCocoaMQTTInterOpOut.publishInSwift{
            topic,message,reference in
            print("""
                  来自Kotlin的方法publishInSwift参数
                  topic         :       \(topic)
                  message       :       \(message)
                  reference     :       \(reference)
            """)
            self.hdCocoaMQTT.publish(topic:topic,with : message)
        }
        
        // 反注册
        hDCocoaMQTTInterOpOut.unSubscribeInSwift{
            topic,reference in
            print("""
                  来自Kotlin的方法unSubscribeInSwift参数
                  topic         :       \(topic)
                  reference     :       \(reference)
            """)
            self.hdCocoaMQTT.unSubscribe(topic:topic )
        }
        
        // 断开链接
        hDCocoaMQTTInterOpOut.disconnectInSwift{
            reference in
            print("""
                  来自Kotlin的方法disconnectInSwift参数
                  reference     :       \(reference)
            """)
            self.hdCocoaMQTT.disconnect()
        }
    }
    
}


