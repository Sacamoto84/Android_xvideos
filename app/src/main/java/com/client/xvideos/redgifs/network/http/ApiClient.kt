package com.client.xvideos.redgifs.network.http

import android.annotation.SuppressLint
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import timber.log.Timber

object ApiClient {

    val USER_AGENT: String =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 YaBrowser/25.6.0.0 Safari/537.36"

    @SuppressLint("CheckResult")
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson {
                this.registerTypeAdapter(UInt::class.java, UIntAdapter()).create()
                this.registerTypeAdapter(ULong::class.java, ULongAdapter()).create()
                this.registerTypeAdapter(Long::class.java, LongAdapter()).create()
            }
        }
        defaultRequest {
            headers.append("Referer", "https://www.redgifs.com/")
            headers.append("Origin", "https://www.redgifs.com")
            headers.append(HttpHeaders.UserAgent, USER_AGENT)
            headers.append(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            headers.append(HttpHeaders.AcceptEncoding, "identity")
            headers.append(HttpHeaders.AcceptLanguage, "ru,en;q=0.9")
            headers.append(HttpHeaders.Range, "bytes=0-500000")
        }
        expectSuccess = true
    }

    var bearerToken: String? = null

    data class TokenResponse(@SerializedName("token") val token: String)

    suspend fun login(): Result<Boolean> {
        return try {
            Timber.i("!!! Red ApiClient login()")
            val tokenResponse = client.get("https://api.redgifs.com/v2/auth/temporary").body<TokenResponse>()
            bearerToken = tokenResponse.token
            Timber.i("!!! Red ApiClient login() SUCCESS - token received")
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "!!! Red ApiClient login() FAILED: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    suspend inline fun <reified T> request(
        url: String,
        params: Map<String, String> = emptyMap(),
    ): Result<T> {
        Timber.i("!!! Red ApiClient request() $url")
        if (bearerToken == null) {
            val loginResult = login()
            if (loginResult.isFailure) return Result.failure(loginResult.exceptionOrNull()!!)
        }
        return try {
            val response: T = client.get(url) {
                bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
                params.forEach { (key, value) -> parameter(key, value) }
            }.body()
            Result.success(response)
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Timber.w("!!! Red ApiClient request() 401 Unauthorized, retrying login...")
                bearerToken = null
                val loginResult = login()
                if (loginResult.isSuccess) {
                    return try {
                        val response: T = client.get(url) {
                            bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
                            params.forEach { (key, value) -> parameter(key, value) }
                        }.body()
                        Result.success(response)
                    } catch (e2: Exception) {
                        Timber.e(e2, "!!! Red ApiClient request() FAILED after retry: $url")
                        Result.failure(e2)
                    }
                }
            }
            Timber.e(e, "!!! Red ApiClient request() FAILED: $url")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "!!! Red ApiClient request() FAILED: $url")
            Result.failure(e)
        }
    }

    suspend inline fun <reified T> request(
        route: Route,
        vararg params: Pair<String, Any> = emptyArray(),
    ): Result<T> {
        Timber.i("!!! Red ApiClient request() ${route.url}")
        if (bearerToken == null) {
            val loginResult = login()
            if (loginResult.isFailure) return Result.failure(loginResult.exceptionOrNull()!!)
        }
        return try {
            val response: T = client.get(route.url) {
                bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
                params.forEach { (key, value) -> parameter(key, value) }
            }.body()
            Result.success(response)
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Timber.w("!!! Red ApiClient request() 401 Unauthorized, retrying login...")
                bearerToken = null
                val loginResult = login()
                if (loginResult.isSuccess) {
                    return try {
                        val response: T = client.get(route.url) {
                            bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
                            params.forEach { (key, value) -> parameter(key, value) }
                        }.body()
                        Result.success(response)
                    } catch (e2: Exception) {
                        Timber.e(e2, "!!! Red ApiClient request() FAILED after retry: ${route.url}")
                        Result.failure(e2)
                    }
                }
            }
            Timber.e(e, "!!! Red ApiClient request() FAILED: ${route.url}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "!!! Red ApiClient request() FAILED: ${route.url}")
            Result.failure(e)
        }
    }

    suspend inline fun requestText(
        route: Route,
        vararg params: Pair<String, Any> = emptyArray(),
    ): Result<String> {
        Timber.i("!!! Red ApiClient requestText() ${route.url}")
        if (bearerToken == null) {
            val loginResult = login()
            if (loginResult.isFailure) return Result.failure(loginResult.exceptionOrNull()!!)
        }
        return try {
            val response: String = client.get(route.url) {
                bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
                params.forEach { (key, value) -> parameter(key, value) }
            }.bodyAsText()
            Result.success(response)
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Timber.w("!!! Red ApiClient requestText() 401 Unauthorized, retrying login...")
                bearerToken = null
                val loginResult = login()
                if (loginResult.isSuccess) {
                    return try {
                        val response: String = client.get(route.url) {
                            bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
                            params.forEach { (key, value) -> parameter(key, value) }
                        }.bodyAsText()
                        Result.success(response)
                    } catch (e2: Exception) {
                        Timber.e(e2, "!!! Red ApiClient requestText() FAILED after retry: ${route.url}")
                        Result.failure(e2)
                    }
                }
            }
            Timber.e(e, "!!! Red ApiClient requestText() FAILED: ${route.url}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "!!! Red ApiClient requestText() FAILED: ${route.url}")
            Result.failure(e)
        }
    }

    suspend inline fun requestText(
        url: String,
        vararg params: Pair<String, Any> = emptyArray(),
    ): Result<String> {
        Timber.i("!!! Red ApiClient requestText() $url")
        if (bearerToken == null) {
            val loginResult = login()
            if (loginResult.isFailure) return Result.failure(loginResult.exceptionOrNull()!!)
        }
        return try {
            val response: String = client.get(url) {
                bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
                params.forEach { (key, value) -> parameter(key, value) }
            }.bodyAsText()
            Result.success(response)
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Timber.w("!!! Red ApiClient requestText() 401 Unauthorized, retrying login...")
                bearerToken = null
                val loginResult = login()
                if (loginResult.isSuccess) {
                    return try {
                        val response: String = client.get(url) {
                            bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
                            params.forEach { (key, value) -> parameter(key, value) }
                        }.bodyAsText()
                        Result.success(response)
                    } catch (e2: Exception) {
                        Timber.e(e2, "!!! Red ApiClient requestText() FAILED after retry: $url")
                        Result.failure(e2)
                    }
                }
            }
            Timber.e(e, "!!! Red ApiClient requestText() FAILED: $url")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "!!! Red ApiClient requestText() FAILED: $url")
            Result.failure(e)
        }
    }
}

class UIntAdapter : TypeAdapter<UInt>() {
    override fun write(out: JsonWriter?, value: UInt?) {
        if (value == null) out?.nullValue() else out?.value(value.toLong())
    }
    override fun read(input: JsonReader?): UInt? {
        if (input?.peek() == JsonToken.NULL) { input.nextNull(); return null }
        return input?.nextLong()?.toUInt()
    }
}

class ULongAdapter : TypeAdapter<ULong>() {
    override fun write(out: JsonWriter?, value: ULong?) {
        if (value == null) out?.nullValue() else out?.value(value.toLong())
    }
    override fun read(input: JsonReader?): ULong? {
        if (input?.peek() == JsonToken.NULL) { input.nextNull(); return null }
        return input?.nextLong()?.toULong()
    }
}

class LongAdapter : TypeAdapter<Long>() {
    override fun write(out: JsonWriter?, value: Long?) {
        if (value == null) out?.nullValue() else out?.value(value)
    }
    override fun read(input: JsonReader?): Long? {
        if (input?.peek() == JsonToken.NULL) { input.nextNull(); return null }
        return input?.nextLong()
    }
}
