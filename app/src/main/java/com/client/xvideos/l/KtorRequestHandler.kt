package com.client.xvideos.l

import com.client.xvideos.l.net.Luscious.Companion.LOGIN
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
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
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
    private val timeoutMillis: Long = 15000,
    private val maxRetries: Int = 5,
    private val retryStatusCodes: Set<Int> = setOf(413, 429, 500, 502, 503, 504),
    private val backoffFactor: Long = 1000,
    username: String? = null,
    password: String? = null
) {
    private val faker = Faker()
    private var username: String? = username
    private var password: String? = password

    val client = HttpClient(OkHttp) {

        install(ContentNegotiation) { gson() }

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

        defaultRequest { headers.append("User-Agent", faker.internet().userAgentAny()) }
        install(Logging) { level = LogLevel.ALL }
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

    private suspend fun post(url: String, formData: Map<String, String> = emptyMap()): String {
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


//    private val cacheTtlMillis = 24 * 60 * 60 * 1000L * 60 //60 сутки

//    suspend fun postJsonCached(url: String, data: String): String {
//        val cacheKey = data.hashCode().toString()
//
//        val res = dao.get(cacheKey)
//
//        if(res != null){
//            return res.content
//        }
//
//        // Делаем запрос
//        val response = postJson(url, data)
//
//        dao.insert(PostJsonEntity(
//            url = cacheKey,
//            content = response
//        ))
//
//        return response
//    }



    // --- Login ---
    var loggedIn: Boolean = false
        private set

    fun setCredentials(username: String?, password: String?) {
        val normalizedUsername = username?.trim().orEmpty()
        val normalizedPassword = password.orEmpty()
        if (this.username != normalizedUsername || this.password != normalizedPassword) {
            loggedIn = false
            this.username = normalizedUsername
            this.password = normalizedPassword
        }
    }

    fun close() {
        client.close()
    }

    suspend fun login(): Boolean
        //username: String? = null,
        //password: String? = null,
    {
        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            println("Username or password not provided")
            loggedIn = false
            return false
        }

        val formData = mapOf(
            "login" to username.orEmpty(),
            "password" to password.orEmpty(),
            "remember" to "on"
        )

        val response = try {
            post(LOGIN, formData)
        } catch (e: Exception) {
            println("Login request failed: ${e.message}")
            loggedIn = false
            return false
        }

        if ("The username and/or password you specified are not correct." in response) {
            println("!!! Login failed. Please check your credentials")
            loggedIn = false
        } else {
            loggedIn = true
            println("Login successful")
        }
        return loggedIn
    }
    // ! --- Login --- !

}



