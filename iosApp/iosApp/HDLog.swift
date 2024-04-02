//
//  HDLog.swift
//  iosApp
//
//  Created by 项鹏乐 on 2024/4/2.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ComposeApp

protocol HDLog: AnyObject {
    func d(tag:String,message:String)
    func i(tag:String,message:String)
    func w(tag:String,message:String)
    func e(tag:String,message:String)
}



class HDLogX :HDLog{
    private let log = HDLoggerCompanion()
    private var debug = true
    
    // MARK: Shared Instance

    private static let _shared = HDLogX()

    // MARK: - Accessors

    class func shared() -> HDLog {
        return _shared
    }
    
    func d(tag: String, message: String) {
        if(!debug){
            return
        }
        log.d(tag: tag, msg: message)
    }
    
    func i(tag: String, message: String) {
        if(!debug){
            return
        }
        log.i(tag: tag, msg: message)
    }
    
    func w(tag: String, message: String) {
        if(!debug){
            return
        }
        log.w(tag: tag, msg: message)
    }
    
    func e(tag: String, message: String) {
        if(!debug){
            return
        }
        log.e(tag: tag, msg: message)
    }
    
    
}
