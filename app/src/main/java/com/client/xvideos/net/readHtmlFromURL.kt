package com.client.xvideos.net

import android.util.LruCache
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import timber.log.Timber

private val lruCacheHTML: LruCache<String, String> = LruCache<String, String>(1000)

suspend fun readHtmlFromURL(url: String = "https://www.xvideos.com"): String {

    Timber.i("!!!..readHtmlFromURL $url ")

    val client = HttpClient(OkHttp)
    {
        install(HttpTimeout)
        {
            requestTimeoutMillis = Long.MAX_VALUE
        }
    }

    //Если есть в кеше возвращает содержимое
    if (lruCacheHTML.get(url) != null) {
        Timber.i("!!! readHtmlFromURL: В LRU кеше есть данные")
        return lruCacheHTML.get(url)
    }

    Timber.i("!!!  readHtmlFromURL: В LRU кеше нет данных")

    //Если нет в кеше запрашиваем данные
    lateinit var response: HttpResponse
    try {
        response = client.get(url)
        val body = response.bodyAsText()
        lruCacheHTML.put(url, body)
        return body
    } catch (e: Exception) {
        Timber.e("!!! readHtmlFromURL: Ошибка " + e.message)
    } finally {
        client.close()
    }


    return ""


}