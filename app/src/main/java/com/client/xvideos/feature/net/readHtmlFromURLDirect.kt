package com.client.xvideos.feature.net

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import timber.log.Timber

suspend fun readHtmlFromURLDirect(url: String = "https://www.xvideos.com"): String {

    Timber.i("!!!..readHtmlFromURLDirect $url ")

    val client = HttpClient(OkHttp)
    {
        install(HttpTimeout)
        {
            requestTimeoutMillis = Long.MAX_VALUE
        }
//        install(HttpCookies){
//            storage = AcceptAllCookiesStorage() // Хранение всех куки
//        }
        followRedirects = true // Обработка редиректов
        defaultRequest {
            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
            header("Accept-Language", "en-US,en;q=0.9")
        }
    }

    lateinit var response: HttpResponse
    try {
        response = client.get(url)
        val body = response.bodyAsText()

        //val storage = client.plugin(HttpCookies).get(Url(url))
        //val cookies = client.plugin(HttpCookies).get(Url("https://www.xv-ru.com"))
        //Timber.i("!!! Cookies: $cookies")

        return body
    } catch (e: Exception) {
        Timber.e("!!! readHtmlFromURLDirect: Ошибка %s", e.message)
    } finally {
        client.close()
    }

    return ""

}