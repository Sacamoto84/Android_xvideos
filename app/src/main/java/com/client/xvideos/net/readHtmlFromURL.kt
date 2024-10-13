package com.client.xvideos.net


import android.util.LruCache
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import timber.log.Timber

private val lruCacheHTML: LruCache<String, String> = LruCache<String, String>(1000)

private val client = HttpClient(OkHttp)//(OkHttp)
{
    install(Logging) {
        level = LogLevel.INFO
    }

    install(HttpTimeout)
    {
        requestTimeoutMillis = Long.MAX_VALUE
    }
}

suspend fun readHtmlFromURL(url: String = "https://www.xvideos.com"): String {

    Timber.i("!!!..readHtmlFromURL $url ")

    //Если есть в кеше возвращает содержимое
    if (lruCacheHTML.get(url) != null) {
        Timber.i("!!! В кеше есть данные")
        return lruCacheHTML.get(url)
    }

    Timber.i("!!! В кеше нет данных")
    lateinit var response: HttpResponse
    try {
        response = client.get(url)
        //println(response.toString())
        val body = response.bodyAsText()

        if (lruCacheHTML.get(url) == null) {
            lruCacheHTML.put(url, body)
        }

        return body
    } catch (e: Exception) {
        Timber.e("Ошибка " + e.message)
    }

    //Сохраним в кеш данный html
    //client.close()
    return ""


}