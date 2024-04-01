import UIKit
import SwiftUI
import ComposeApp
// import shared
import CocoaMQTT

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

//extension ContentView:MqttEeventDelegate{
//    
//}

class VM{
   
    @Published private(set) var log:String
    
    init() {
        self.log  = "等待..."
    }
    
    func append(msg:String){
        log.append("\n" + msg)
    }
    
    func clear(){
        self.log  = "等待..."
    }
}
extension VM:MqttEeventDelegate{
    func onConnectionStatus(isConnected: Bool) {
        append(msg: "连接状态：\(isConnected)")
    }
    
    func onSubscibe(topics: NSDictionary) {
        append(msg: "注册topic成功：\(topics)")
    }
    
    func onUnsubscribe(topics: [String]) {
        append(msg: "反注册topic成功：\(topics)")
    }
    
    func onMessageArrived(message: CocoaMQTTMessage) {
        let data = message.payload
        append(msg: "收到消息：\(message.topic)-\(bytesToIntString(data))")
    }
    
    private func bytesToIntString(_ bytes: [UInt8]) -> String{
        var str = ""
        for byte in bytes {
            str += String(byte)
        }
        print(str)
        return str
    }

    
    
}

struct ContentView: View {
//     let calculator = com.yunext.virtuals.util.Calculator.Companion()
   
    let calculator = Calculator2.Companion()
    let calculator3 = Calculator3.Companion()
    let calculator4 = Calculator4.Companion()
    let calculator5 = Calculator5.Companion()
    
    //let hDCocoaMQTTInterOpOut = HDCocoaMQTTInterOpOut()
    //private let clientViewModel = HDCocoaMQTT.shared()
    let hDCocoaMQTTInterOpSwift = HDCocoaMQTTInterOpSwift()
    //private let vm = VM()
    
    init(){
    //clientViewModel.initializeMQTT()
    //    clientViewModel.delegate = vm

        // register for HDCocoaMQTTInterOpOut
        hDCocoaMQTTInterOpSwift.initializeInterOp()
    }
    
    

    private var sum :String{
//        let  wuyin  = DatetimesKt.debugWuYin()
       let  wuyin  = "XXX"//DatetimesKt.debugWuYin()
        let color = Colors.red
        let currentTime = DatetimesKt.currentTime()
        let div = calculator.mulx(a:10,b:5)
        let r4 = calculator4.sum(a:111,b:222)
        let r5 = calculator5.sum(a:111,b:222)
//         let div = calculator.mul(a:10,b:5)
        return "wuyin:\(wuyin) color:\(color) currentTime=\(currentTime) \(r5) - \(r4)-"+String(calculator3.sum(a:1,b:2))+"-"+String(div) + String(calculator.sum(a:20,b:30))
    }

    private let debugInterOp = DebugInterOp()
    private let debugSendByteArrayOpt = DebugSendByteArrayOpt()
    
    var body: some View {
        VStack {
            Text(sum)
                HStack{
                  Button("连接"){
                      //clientViewModel.connect()
                      print("开始")
                      let swiftData :[UInt8] = [1,2,3,4,56,4]
                      print("\(swiftData)")
                      debugInterOp.interOp_Debug_ByteArray2(data:swiftData)
                      
//                      let intArray : [Int8] = swiftData
//                          .map { Int8(bitPattern: $0) }
//                      let kotlinByteArray: KotlinByteArray = KotlinByteArray.init(size: Int32(data.count))
//                      for (index, element) in intArray.enumerated() {
//                          kotlinByteArray.set(index: Int32(index), value: element)
//                      }
                      
//                      let intArray:[Int8] = data.map{Int8(bitPattern: $0)}
//                      let kotlinByteArray:KotlinByteArray = KotlinByteArray.init(size:Int32(data.count))
//                      for(index,element) in intArray.enumerated(){
//                          kotlinByteArray.set(index:Int32(index),value:element)
//                      }
                      debugInterOp.interOp_Debug_ByteArray(data: KotlinByteArray.from( swiftData))
                      
                      print("结束")
                  }

                   Button("注册"){
                       //clientViewModel.subscribe(topic: "/skeleton/tcuf6vn2ohw4mvhb/twins_test_001_cid/down")
                                }

                                 Button("发送"){
                                     // clientViewModel.publish(topic: "/skeleton/tcuf6vn2ohw4mvhb/twins_test_001_cid/down", with: "hello swift mqtt!!!\(UUID().uuidString)")
                                                  }

                                                   Button("关闭连接"){
                                                       //clientViewModel.disconnect()
                                                                    }
                }
              Button("确认"){
                    print(" 测试开始")
                                // 测试函数作为参数
                               DemoOptKt.hdAccept(some:"i am from swift param for accept!"){
                                    (fromKotlinString) -> (KotlinInt?) in
                                    1234
                               }
                               // 测试函数作为返回

                              let re:String? =  DemoOptKt.hdSupply(some:"i am from swift param for supply!")("5678")
                              print(re ?? "error hdSupply 1")
                              let re2:String? =  DemoOptKt.hdSupply(some:"i am from swift param for supply!")("5678")
                  print(re2 ?? "-")
                              if let re2 {
                                print(re2)
                              } else{
                                print("error hdSupply")
                              }
                              print(" 测试结束")
                            }

            ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
                .detectOrientation()
                .onAppear(){
                    //
                    debugInterOp.interOp_Out_ByteArray{
                        KotlinByteArray in
                        print("收到来自kotlin的ByteArray参数:\(KotlinByteArray)")
                        // how KotlinByteArray->[Int8]
//
//                        func test123(p:[Int8]){
//                            print("p:\(p)")
//                        }
//                        
//                        test123(p:KotlinByteArray)
                    }
                    
                    // 注册回调
                    debugSendByteArrayOpt.interOp_Out_ByteArray{
                        data in
                        print("收到来自kotlin的NSData参数:\(data)")
                        let swiftByteArray = [UInt8](data)
                        print("转换来自kotlin的NSData参数:\(swiftByteArray)")
                        
                    }
                    
                    // 注册来自kotlin的桥接方法
                    Bridges_iosKt.changeScreenOrientation { KotlinInt in
                                if (KotlinInt == 0) {
                                    changeOrientation(to: UIInterfaceOrientation.portrait)
                                }
                                else {
                                    changeOrientation(to: UIInterfaceOrientation.landscapeLeft)
                                }
                            }

                     // 注册来自kotlin的桥接方法
                     Bridges_iosKt.tryGetScreenOrientation (
                        callBack:tryGetOrientationType

                     )
                    
                    //
//                    hDCocoaMQTTInterOpOut.initializeMQTTInSwift{
//                        (host,port,clientId,username,password,reference) -> String in
//                        print("""
//                              来自Kotlin的方法initializeMQTTInSwift参数
//                              host          :       \(host)
//                              port          :       \(port)
//                              clientId      :       \(clientId)
//                              username      :       \(username)
//                              password      :       \(password)
//                              reference     :       \(reference)
//                        """)
//                        clientViewModel.initializeMQTT(host,UInt16(port),clientId,username,password)
//                        clientViewModel.connect()
//                        return "来自initializeMQTTInSwift swift返回"
//                    
//                    }
                    





                }
        }
    }
}

struct DetectOrientation: ViewModifier {
    func body(content: Content) -> some View {
        content
            .onReceive(NotificationCenter.default.publisher(for: UIDevice.orientationDidChangeNotification)) { _ in
                // 触发屏幕方向改变事件
                if (UIDevice.current.orientation.isLandscape) {
                                    // 调用kotlin的桥接方法
                                    Bridges_iosKt.onScreenChange(orientation: 1)
                } else {
                                    Bridges_iosKt.onScreenChange(orientation: 0)
                }
            }
    }
}

// extension View {
//     func detectOrientation() -> some View {
//         modifier(DetectOrientation())
//     }
// }

extension View {
    func detectOrientation(
        //_ orientation: Binding<UIDeviceOrientation>
    ) -> some View {
        modifier(DetectOrientation(
            //orientation: orientation
        ))
    }
}

// 切换横竖屏
func changeOrientation(to orientation: UIInterfaceOrientation) {
    if #available(iOS 16.0, *) {

        let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene

        if (orientation.isPortrait) {
            windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: .portrait))
        }
        else {
            windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: .landscape))
        }
    }
    else {
        UIDevice.current.setValue(orientation.rawValue, forKey: "orientation")
    }
}

// func tryGetOrientationType(a:(KotlinInt) -> KotlinUnit){
//     a(0)
// }


func tryGetOrientationType(a:(KotlinInt) -> KotlinUnit){
    let orientation = UIApplication.shared.statusBarOrientation
    switch orientation {
        case .portrait, .portraitUpsideDown, .unknown: /// 竖屏
            a(0)
        case .landscapeLeft, .landscapeRight: /// 横屏
            a(1)
        default:
            print("tryGetOrientationType 错误的orientation:\(orientation) ")
    }
}


class WuYin{
    func test(wuyin:String)->String{
        return "your wuyin is /(wuyin)"
    }
}





