package com.yunext.kmp.http.datasource

import com.yunext.kmp.http.core.HDResult
import com.yunext.kmp.resp.tsl.TslResp

interface LocalTslDatasource:TslDatasource {
    /**
     * @param fileName files/tsl_demo.json
     */
    suspend fun getTsl(fileName:String = "files/tsl_demo.json"): HDResult<TslResp>
    suspend fun getTsl2(fileName:String = "files/tsl_demo.json"): HDResult<TslResp>

}