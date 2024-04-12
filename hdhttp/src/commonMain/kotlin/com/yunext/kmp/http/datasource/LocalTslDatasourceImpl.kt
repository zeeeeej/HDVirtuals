package com.yunext.kmp.http.datasource

import com.yunext.kmp.http.core.HDResult
import com.yunext.kmp.http.tsl.HDResponseContainer
import com.yunext.kmp.resp.tsl.TslResp
import com.yunext.kmp.resource.hdResFiles
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

class LocalTslDatasourceImpl() : LocalTslDatasource {
    override suspend fun getTsl(fileName: String): HDResult<TslResp> {
        return try {
            //Napier.v("TslDatasourceImpl::loadLocal start...")
            val json = hdResFiles(fileName).decodeToString()
            //Napier.v("TslDatasourceImpl::loadLocal end json :$json")
            //Napier.v("LocalTslDatasourceImpl::loadLocal 1")
            val data: HDResponseContainer<TslResp> = hdJson.decodeFromString(json)
            //Napier.v("LocalTslDatasourceImpl::loadLocal 2")
            val r = HDResponseContainer.parse<TslResp>(data)
            //Napier.v("LocalTslDatasourceImpl::loadLocal end json :$json r:$r")
            r
        } catch (e: Exception) {
            Napier.e("LocalTslDatasourceImpl::getTsl end error :$e")
            HDResult.Fail(e)
        }
    }

    override suspend fun getTsl2(fileName: String): HDResult<TslResp> {
        return try {
            Napier.v("LocalTslDatasourceImpl::loadLocal start...")
            val json = hdResFiles(fileName).decodeToString()
            //Napier.v("LocalTslDatasourceImpl::loadLocal 1")
            val data: HDResponseContainer<TslResp> = hdJson.decodeFromString(json)
            //Napier.v("LocalTslDatasourceImpl::loadLocal 2")
            val r = HDResponseContainer.parse<TslResp>(data)
            //Napier.v("LocalTslDatasourceImpl::loadLocal end json :$json r:$r")
            r
        } catch (e: Exception) {
            Napier.e("LocalTslDatasourceImpl::getTsl end error :$e")
            HDResult.Fail(e)
        }
    }
}

val hdJson : Json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys  =true

}


