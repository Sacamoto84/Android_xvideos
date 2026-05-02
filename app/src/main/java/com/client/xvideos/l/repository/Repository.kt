package com.client.xvideos.l.repository

import android.content.Context
import com.client.xvideos.common.encrypting.Password
import com.client.xvideos.common.room.AppDatabase
import com.client.xvideos.common.room.entity.CacheUrlStringRamEntity
import com.client.xvideos.common.room.entity.CacheUrlStringRomEntity
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.common.util.toMD5
import com.client.xvideos.l.KtorRequestHandler
import com.client.xvideos.l.net.Luscious
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository(
    dbCache: AppDatabase,
    //private val luscious: Luscious,
    private val scope: CoroutineScope,
    //private val saved: SavedL
    username: String? = null,
    password: String? = null,
    context: Context
) {

    //Точка входа для GraphQL
    val apiUrl = Luscious.Companion.API

    init {
        clearRamDao()
        Password.loadPassword(context)
    }

    private val handler = KtorRequestHandler(
        timeoutMillis = 5000,
        maxRetries = 5,
        retryStatusCodes = setOf(413, 429, 500, 502, 503, 504),
        backoffFactor = 1000,
        username,
        password
    )

    private val cacheUrlStringRomDao = dbCache.cacheUrlStringRomDao()
    private val cacheUrlStringRamDao = dbCache.cacheUrlStringRamDao()


    suspend fun openURI(
        data: String,
        type: RepositoryUriType = RepositoryUriType.POST,
        config: RepositoryUriConfig = RepositoryUriConfig.DIRECT
    ): Result<String> {
        Timber.i("!!! openURI()")
        //Timber.i("!!! openURI() data:$data type:$type config:$config")

        try {
            if (!handler.loggedIn) {
                handler.login()
                while (!handler.loggedIn) { delay(1000) }
            }
        }
        catch (e: Exception){
            Timber.e(e, "!!! openURI() login error")
            return Result.failure(e)
        }

        if (type == RepositoryUriType.POST) {

            when (config) {

                //Запрос без кеширования
                RepositoryUriConfig.DIRECT -> {
                    try {
                        val res = handler.postJson(apiUrl, data)
                        return validateJsonResponse(res)
                    }catch (e: Exception){
                        return Result.failure(e)
                    }
                }

                //Сделать запись в ROOM если нет в базе, иначе прочитать из него
                RepositoryUriConfig.CACHE_ROM -> {
                    try {
                        val cacheKey = data.toMD5()
                        val res = cacheUrlStringRomDao.get(cacheKey)
                        if (res != null) {
                            val cached = validateJsonResponse(res.content)
                            if (cached.isSuccess) {
                                //Timber.i("!!! openURI() CACHE_ROM res != null response:${res.content}")
                                return cached
                            }
                            Timber.w("!!! openURI() CACHE_ROM malformed cache: ${cached.exceptionOrNull()?.message}")
                            cacheUrlStringRomDao.delete(cacheKey)
                        }
                        val response = handler.postJson(apiUrl, data)
                        val checkedResponse = validateJsonResponse(response)
                        if (checkedResponse.isFailure) return checkedResponse

                        if (checkedResponse.getOrThrow().contains("{\"errors\":")){
                            SnackBar.error(checkedResponse.getOrThrow())
                            return Result.failure(Exception(checkedResponse.getOrThrow()))
                        }

                        cacheUrlStringRomDao.insert( CacheUrlStringRomEntity( url = cacheKey, content = checkedResponse.getOrThrow() ) )

                        //Timber.i("!!! openURI() CACHE_ROM net response:$response")
                        return checkedResponse
                    }
                    catch (e: Exception){
                        Timber.e(e, "!!! openURI() CACHE_ROM error")
                        return Result.failure(e)
                    }
                }

                //Сделать запись в ROOM RAM если нет в базе, иначе прочитать из него
                RepositoryUriConfig.CACHE_RAM -> {
                    try {
                        val cacheKey = data.toMD5()
                        val res = cacheUrlStringRamDao.get(cacheKey)
                        if (res != null) {
                            val cached = validateJsonResponse(res.content)
                            if (cached.isSuccess) {
                                //Timber.i("!!! openURI() CACHE_RAM res != null response:${res.content}")
                                return cached
                            }
                            Timber.w("!!! openURI() CACHE_RAM malformed cache: ${cached.exceptionOrNull()?.message}")
                            cacheUrlStringRamDao.delete(cacheKey)
                        }
                        val response = handler.postJson(apiUrl, data)
                        val checkedResponse = validateJsonResponse(response)
                        if (checkedResponse.isFailure) {
                            val cachedRom = cacheUrlStringRomDao.get(cacheKey)?.content?.let { validateJsonResponse(it) }
                            if (cachedRom?.isSuccess == true) {
                                Timber.w("!!! openURI() CACHE_RAM network error, fallback CACHE_ROM")
                                val cachedContent = cachedRom.getOrThrow()
                                cacheUrlStringRamDao.insert(CacheUrlStringRamEntity(url = cacheKey, content = cachedContent))
                                return cachedRom
                            }
                            return checkedResponse
                        }

                        val checkedContent = checkedResponse.getOrThrow()
                        if (checkedContent.contains("{\"errors\":")){
                            SnackBar.error(checkedContent)
                            return Result.failure(Exception(checkedContent))
                        }

                        cacheUrlStringRamDao.insert( CacheUrlStringRamEntity(url = cacheKey, content = checkedContent) )
                        cacheUrlStringRomDao.insert( CacheUrlStringRomEntity(url = cacheKey, content = checkedContent) )

                        //Timber.i("!!! openURI() CACHE_RAM net response:$response")

                        if (checkedContent.contains("{\"errors\":")) { SnackBar.error(checkedContent) }
                        return checkedResponse
                    }
                    catch (e: Exception){
                        Timber.e(e, "!!! openURI() CACHE_RAM error")
                        SnackBar.error(e.message?: "openURI() CACHE_RAM error")
                        return Result.failure(e)
                    }
                }
            }

        }

        return Result.failure(Exception("Некорректный тип запроса"))
    }

    private fun validateJsonResponse(response: String): Result<String> {
        val normalized = response.trim()
        if (!normalized.startsWith("{")) {
            val message = if (normalized.startsWith("<!DOCTYPE", ignoreCase = true) || normalized.startsWith("<html", ignoreCase = true)) {
                "Server returned HTML instead of JSON: ${normalized.previewForLog()}"
            } else {
                "Server returned non-JSON response: ${normalized.previewForLog()}"
            }
            Timber.w("!!! openURI() $message")
            return Result.failure(IllegalStateException(message))
        }

        return runCatching {
            val json = JsonParser.parseString(normalized)
            if (!json.isJsonObject) {
                error("Response is not a JSON object: ${normalized.previewForLog()}")
            }
            if (json.asJsonObject.has("errors")) {
                error("GraphQL errors: ${normalized.previewForLog()}")
            }
            normalized
        }.onFailure {
            Timber.w("!!! openURI() malformed JSON response: ${normalized.previewForLog()} (${it.message})")
        }
    }

    suspend fun deleteCache(data: String, config: RepositoryUriConfig) {
        val cacheKey = data.toMD5()
        when (config) {
            RepositoryUriConfig.CACHE_RAM -> cacheUrlStringRamDao.delete(cacheKey)
            RepositoryUriConfig.CACHE_ROM -> cacheUrlStringRomDao.delete(cacheKey)
            RepositoryUriConfig.DIRECT -> Unit
        }
    }

    private fun String.previewForLog(): String {
        return replace(Regex("\\s+"), " ").take(200)
    }

    private fun clearRamDao(){
        scope.launch { withContext(Dispatchers.Main) { cacheUrlStringRamDao.deleteAll() } }
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


