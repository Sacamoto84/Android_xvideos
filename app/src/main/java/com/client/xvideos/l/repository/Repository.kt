package com.client.xvideos.l.repository

import com.client.xvideos.common.util.toMD5
import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.db.AppLDatabase
import com.client.xvideos.l.db.RepositoryCacheFullEntity
import com.client.xvideos.l.db.RepositoryCacheTempEntity
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository(
    db: AppLDatabase,
    //private val luscious: Luscious,
    private val snackBarEvent: SnackBarEvent,
    private val scope: CoroutineScope,
    //private val saved: SavedL
    username: String? = null,
    password: String? = null,
) {

    init { clearTemp() }

    private val handler = KtorRequestHandler(
        timeoutMillis = 5000,
        maxRetries = 5,
        retryStatusCodes = setOf(413, 429, 500, 502, 503, 504),
        backoffFactor = 1000,
        username,
        password
    )

    private val repositoryCacheFullDao = db.repositoryCacheFullDao()
    private val repositoryCacheTempDao = db.repositoryCacheTempDao()

    suspend fun openURI(
        url: String,
        data: String,
        type: RepositoryUriType = RepositoryUriType.POST,
        config: RepositoryUriConfig = RepositoryUriConfig.DIRECT
    ): Result<String> {

        Timber.i("!!! openURI() data:$data type:$type config:$config")

        try {
            if (!handler.loggedIn) {
                handler.login()
                while (!handler.loggedIn) {
                    delay(1000)
                }
            }
        }
        catch (e: Exception){
            Timber.e(e, "!!! openURI() login error")
            return Result.failure(e)
        }

        if (type == RepositoryUriType.POST) {

            when (config) {

                RepositoryUriConfig.DIRECT -> {
                    try {
                       val res = handler.postJson(url, data)
                       return Result.success(res)
                    }catch (e: Exception){
                       return Result.failure(e)
                    }
                }

                RepositoryUriConfig.CACHE_ROM -> {
                    try {
                        val cacheKey = data.toMD5()
                        val res = repositoryCacheFullDao.get(cacheKey)
                        if (res != null) {
                            //Timber.i("!!! openURI() CACHE_ROM res != null response:${res.content}")
                            return Result.success(res.content)
                        }
                        val response = handler.postJson(url, data)

                        if (response.contains("{\"errors\":")){
                            snackBarEvent.error(response)
                            return Result.failure(Exception(response))
                        }

                        repositoryCacheFullDao.insert(
                            RepositoryCacheFullEntity(
                                url = cacheKey,
                                content = response
                            )
                        )
                        //Timber.i("!!! openURI() CACHE_ROM net response:$response")
                        return Result.success(response)
                    }
                    catch (e: Exception){
                        Timber.e(e, "!!! openURI() CACHE_ROM error")
                        return Result.failure(e)
                    }
                }

                RepositoryUriConfig.CACHE_RAM -> {
                    try {
                        val cacheKey = data.toMD5()
                        val res = repositoryCacheTempDao.get(cacheKey)
                        if (res != null) {
                            //Timber.i("!!! openURI() CACHE_RAM res != null response:${res.content}")
                            return Result.success(res.content)
                        }
                        val response = handler.postJson(url, data)

                        if (response.contains("{\"errors\":")){
                            snackBarEvent.error(response)
                            return Result.failure(Exception(response))
                        }

                        repositoryCacheTempDao.insert(
                            RepositoryCacheTempEntity(
                                url = cacheKey,
                                content = response
                            )
                        )
                        //Timber.i("!!! openURI() CACHE_RAM net response:$response")

                        if (response.contains("{\"errors\":"))
                        {
                            snackBarEvent.error(response)
                        }

                        return Result.success(response)
                    }
                    catch (e: Exception){
                        Timber.e(e, "!!! openURI() CACHE_RAM error")
                        return Result.failure(e)
                    }
                }
            }

        }

        return Result.failure(Exception("Некорректный тип запроса"))
    }


    fun clearTemp(){
        scope.launch {
            withContext(Dispatchers.Main) {
                repositoryCacheTempDao.deleteAll()
            }
        }
    }



}


enum class RepositoryUriType {
    GET,
    POST,
}

enum class RepositoryUriConfig {
    DIRECT,    //Прямой запрос в сеть
    CACHE_RAM, //Временный кеш в DB, после перезагрузки удаляется
    CACHE_ROM  //Постоянный кеш в DB
}


