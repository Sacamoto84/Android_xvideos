package com.client.xvideos.l

import com.client.common.AppPath
import com.client.xvideos.l.db.PostJsonDao
import com.client.xvideos.l.db.PostJsonEntity
import com.client.xvideos.l.db.PostJsonRamDao
import com.client.xvideos.l.db.PostJsonRamEntity
import com.github.javafaker.Faker
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.delay
import java.io.IOException

class KtorRequestHandler(
    private val timeoutMillis: Long = 5000,
    private val maxRetries: Int = 5,
    private val retryStatusCodes: Set<Int> = setOf(413, 429, 500, 502, 503, 504),
    private val backoffFactor: Long = 1000,
    private val dao : PostJsonDao,
    private val daoRam : PostJsonRamDao
) {
    private val faker = Faker()

    val client = HttpClient(OkHttp) {

        install(ContentNegotiation) {
            gson()
        }

        // Подключаем поддержку куков (сохраняет cookies между запросами)
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }

        // Ретрай на уровне клиента (аналог retry_strategy)
        install(HttpRequestRetry) {
            maxRetries = 5
            retryIf { request, response ->
                response.status.value in listOf(413, 429, 500, 502, 503, 504)
            }
            delayMillis { retry -> retry * 1000L }  // backoff factor = 1 секунда * номер попытки
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        defaultRequest {
            headers.append("User-Agent", faker.internet().userAgentAny())
        }
    }

    suspend fun get(url: String, params: Map<String, String> = emptyMap()): String {
        return retry {
            client.get {
                url(url)
                params.forEach { (k, v) -> parameter(k, v) }
            }.body()
        }
    }

    suspend fun postJson(url: String, data: String): String {
        return retry {
            client.post {
                url(url)
                contentType(ContentType.Application.Json)
                setBody(TextContent(data, ContentType.Application.Json))
            }.body()
        }
    }

    suspend fun post(url: String, formData: Map<String, String> = emptyMap()): String {
        return retry {
            client.post {
                url(url)
                setBody(FormDataContent(Parameters.build {
                    formData.forEach { (k, v) -> append(k, v) }
                }))
            }.body()
        }
    }

    private suspend fun <T> retry(block: suspend () -> T): T {
        var attempt = 0
        var lastError: Throwable? = null

        while (attempt < maxRetries) {
            try {
                return block()
            } catch (e: ResponseException) {
                val status = e.response.status.value
                if (status !in retryStatusCodes) throw e
                lastError = e
            } catch (e: IOException) {
                lastError = e
            }

            attempt++
            delay(backoffFactor * attempt)
        }

        throw lastError ?: IllegalStateException("Unknown error during retry")
    }


    private val cacheTtlMillis = 24 * 60 * 60 * 1000L * 60 //60 сутки

    suspend fun postJsonCached(url: String, data: String): String {
        val cacheKey = data.hashCode().toString()

        val res = dao.get(cacheKey)

        if(res != null){
            return res.content
        }

        // Делаем запрос
        val response = postJson(url, data)

        dao.insert(PostJsonEntity(
            url = cacheKey,
            content = response
        ))

        return response
    }

    suspend fun postJsonCachedRam(url: String, data: String): String {
        val cacheKey = data.hashCode().toString()

        val res = dao.get(cacheKey)

        if(res != null){
            return res.content
        }

        // Делаем запрос
        val response = postJson(url, data)

        daoRam.insert(
            PostJsonRamEntity(
                url = cacheKey,
                content = response
            )
        )

        return response
    }

}



