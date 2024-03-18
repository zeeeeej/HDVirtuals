import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
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



