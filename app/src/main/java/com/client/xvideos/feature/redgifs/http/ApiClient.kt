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

class ApiClient {

    val USER_AGENT: String =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3346.8 Safari/537.36 Redgifs/"

    @SuppressLint("CheckResult")
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson {
                //this.registerTypeAdapter(MediaItem::class.java, MediaItemTypeAdapter()).create()
                this.registerTypeAdapter(UInt::class.java, UIntAdapter()).create()
                this.registerTypeAdapter(ULong::class.java, ULongAdapter()).create()

            }
        }
    }

    var bearerToken: String? = null

    data class TokenResponse(  @SerializedName("token") val token: String)

    suspend fun login() {

        // Получить временный токен
        val tokenResponse =
            client.get("https://api.redgifs.com/v2/auth/temporary").body<TokenResponse>()
        bearerToken = tokenResponse.token
    }

    suspend inline fun <reified T> request(
        url: String,
        params: Map<String, String> = emptyMap(),
    ): T {

        if (bearerToken == null) login()

        return client.get(url) {
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

    //val res : CreatorResponse  = request(route)
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


//class MediaItemTypeAdapter : TypeAdapter<MediaItem>() {
//
//    private val gson = Gson()
//
//    override fun write(out: JsonWriter, value: MediaItem?) { }
//
//    override fun read(reader: JsonReader): MediaItem {
//
//        val jsonElement = JsonParser.parseReader(reader).asJsonObject
//        val type = jsonElement.get("type").asInt
//
//        return when (type) {
//            1 -> gson.fromJson(jsonElement, ImageInfo::class.java).let { ImageInfoItem(it) }
//            2 -> gson.fromJson(jsonElement, GifInfo::class.java).let { GifInfoItem(it) }
//            else -> throw IllegalArgumentException("Unknown type: $type")
//        }
//    }
//
//}


