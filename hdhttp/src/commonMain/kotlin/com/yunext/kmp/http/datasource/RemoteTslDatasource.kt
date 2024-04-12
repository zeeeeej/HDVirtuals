package com.yunext.kmp.http.datasource

import com.yunext.kmp.http.core.HDResult
import com.yunext.kmp.resp.tsl.TslResp

interface RemoteTslDatasource :TslDatasource{
    /**
     * @param clientId 设备接入id
     * @param projectKey 暂时无用
     */
    suspend fun getTsl(clientId: String, projectKey: String): HDResult<TslResp>

}