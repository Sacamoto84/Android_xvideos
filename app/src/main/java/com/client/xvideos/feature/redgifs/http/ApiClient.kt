package com.client.xvideos.feature.redgifs.http

import android.annotation.SuppressLint
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import io.ktor.client.plugins.cache.HttpCache

class ApiClient {

    val USER_AGENT: String =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3346.8 Safari/537.36 Redgifs/"

//    // Set up the user's device information
//    val deviceInfo = "
//        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.3',
//        'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
//        'accept-language': 'en-US,en;q=0.8',
//        'accept-encoding': 'gzip, deflate, br',
//        'connection': 'keep-alive',
//        'upgrade-insecure-requests': '1'"
//    };

    @SuppressLint("CheckResult")
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson {
                this.registerTypeAdapter(UInt::class.java, UIntAdapter()).create()
                this.registerTypeAdapter(ULong::class.java, ULongAdapter()).create()
                this.registerTypeAdapter(Long::class.java, LongAdapter()).create()
            }
        }
        expectSuccess = true
    }

    var bearerToken: String? = null

    data class TokenResponse(  @SerializedName("token") val token: String)

    suspend fun login() {
        // Получить временный токен
        val tokenResponse = client.get("https://api.redgifs.com/v2/auth/temporary").body<TokenResponse>()
        bearerToken = tokenResponse.token
    }

    suspend inline fun <reified T> request(
        url: String,
        params: Map<String, String> = emptyMap(),
    ): T {
        if (bearerToken == null) login()
        return client.get(url) {
            bearerToken?.let { headers { append(HttpHeaders.Authorization, "Bearer $it") } }
            params.forEach { (key, value) -> parameter(key, value) }
        }.body()
    }


    suspend inline fun <reified T> request(
        route: Route, vararg params: Pair<String, Any> = emptyArray(),
    ): T {

        if (bearerToken == null) login()

        return client.get(route.url) {
            bearerToken?.let {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $it")
                }
            }
            params.forEach { (key, value) ->
                parameter(key, value)
            }
        }.body()
    }


    suspend inline fun requestText(
        route: Route,
        vararg params: Pair<String, Any> = emptyArray(),
    ): String {

        if (bearerToken == null) login()

        return client.get(route.url) {
            bearerToken?.let {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $it")
                }
            }
            params.forEach { (key, value) ->
                parameter(key, value)
            }
        }.bodyAsText()
    }

    suspend inline fun requestText(
        route: String,
        vararg params: Pair<String, Any> = emptyArray(),
    ): String {

        return client.get(route) {
            bearerToken?.let {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $it")
                }
            }
            params.forEach { (key, value) ->
                parameter(key, value)
            }
        }.bodyAsText()
    }


}

class UIntAdapter : TypeAdapter<UInt>() {

    override fun write(out: JsonWriter?, value: UInt?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out?.value(value.toLong()) // Сохраняем как Long в JSON
        }
    }

    override fun read(input: JsonReader?): UInt? {
        if (input?.peek() == JsonToken.NULL) {
            input.nextNull()
            return null
        }
        return input?.nextLong()?.toUInt()
    }
}

class ULongAdapter : TypeAdapter<ULong>() {

    override fun write(out: JsonWriter?, value: ULong?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out?.value(value.toLong()) // Сохраняем как Long в JSON
        }
    }

    override fun read(input: JsonReader?): ULong? {
        if (input?.peek() == JsonToken.NULL) {
            input.nextNull()
            return null
        }
        return input?.nextLong()?.toULong()
    }
}

class LongAdapter : TypeAdapter<Long>() {

    override fun write(out: JsonWriter?, value: Long?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out?.value(value) // Сохраняем как Long в JSON
        }
    }

    override fun read(input: JsonReader?): Long? {
        if (input?.peek() == JsonToken.NULL) {
            input.nextNull()
            return null
        }
        return input?.nextLong()
    }
}
