//
//  Utils.swift
//  iosApp
//
//  Created by 项鹏乐 on 2024/4/1.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ComposeApp

func stringToBytes(_ str:String) -> [UInt8] {
     let data = str.data(using: .utf8)!
     return [UInt8](data)
 }

 func bytesToIntString(_ bytes: [UInt8]) -> String{
    var str = ""
    for byte in bytes {
        str += String(byte)
    }
    print(str)
    return str
}


extension KotlinByteArray {
    static func from(_ data: [UInt8]) -> KotlinByteArray {
        let swiftByteArray = data
        return swiftByteArray
            .map(Int8.init(bitPattern:))
            .enumerated()
            .reduce(into: KotlinByteArray(size: Int32(swiftByteArray.count))) { result, row in
                result.set(index: Int32(row.offset), value: row.element)
            }
    }
}


//                      let intArray:[Int8] = data.map{Int8(bitPattern: $0)}
//                      let kotlinByteArray:KotlinByteArray = KotlinByteArray.init(size:Int32(data.count))
//                      for(index,element) in intArray.enumerated(){
//                          kotlinByteArray.set(index:Int32(index),value:element)
//                      }
extension KotlinByteArray {
    static func toInt8 ()-> [Int8] {
        
        return []
    }
}

