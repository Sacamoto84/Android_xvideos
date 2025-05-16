package com.client.xvideos.feature.redgifs

import com.client.xvideos.feature.redgifs.model.TagInfo
import com.client.xvideos.feature.redgifs.model.TagsResponse
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

object ApiClient {

    val USER_AGENT: String =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3346.8 Safari/537.36 Redgifs/"

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson()
        }
    }

    var bearerToken: String? = null

    suspend fun login() {
        data class TokenResponse(val token: String)
        // Получить временный токен
        val tokenResponse =
            client.get("https://api.redgifs.com/v2/auth/temporary").body<TokenResponse>()
        bearerToken = tokenResponse.token
    }

    suspend fun getSomething(): String {
        return client.get("https://example.com/v2/data") {
            bearerToken?.let {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $it")
                }
            }
        }.bodyAsText()
    }


    suspend inline fun <reified T> request(
        url: String,
        params: Map<String, String> = emptyMap(),
    ): T {
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

    suspend inline fun <reified T> request(
        route: Route
    ): T {

        return client.get(route.url) {
            bearerToken?.let {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $it")
                }
            }
//            params.forEach { (key, value) ->
//                parameter(key, value)
//            }
        }.body()
    }


    suspend inline fun requestText(
        route: Route
    ): String {

        return client.get(route.url) {
            bearerToken?.let {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $it")
                }
            }
//            params.forEach { (key, value) ->
//                parameter(key, value)
//            }
        }.bodyAsText()
    }





}




