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

struct ContentView: View {
//     let calculator = com.yunext.virtuals.util.Calculator.Companion()
 private let clientViewModel = HDCocoaMQTT()
    let calculator = Calculator2.Companion()
    let calculator3 = Calculator3.Companion()
    let calculator4 = Calculator4.Companion()
    let calculator5 = Calculator5.Companion()

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


    var body: some View {
        VStack {
            Text(sum)
                HStack{
                  Button("连接"){
  clientViewModel.testMqtt()
                  }

                   Button("注册"){
clientViewModel.subscribe(topic: "/skeleton/tcuf6vn2ohw4mvhb/twins_test_001_cid/down")
                                }

                                 Button("发送"){
  clientViewModel.publish(topic: "/skeleton/tcuf6vn2ohw4mvhb/twins_test_001_cid/down", with: "hello swift mqtt!!!\(UUID().uuidString)")
                                                  }

                                                   Button("关闭连接"){
  clientViewModel.disconnect()
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
                              print(re2)
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
    }
}


class WuYin{
    func test(wuyin:String)->String{
        return "your wuyin is /(wuyin)"
    }
}




