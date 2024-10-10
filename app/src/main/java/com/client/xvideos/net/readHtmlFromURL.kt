package com.client.xvideos.net


import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import timber.log.Timber

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

suspend fun readHtmlFromURL(url : String = "https://www.xvideos.com"): String {

    Timber.i("..readHtmlFromURL $url ")

//    if (cacheCheck(url))
//    {
//        //Если в кеше есть страница то ее и читаем
//        val html = cacheHTMLRead(url)
//        return html
//    }
//    else
//    {
        Timber.i("В кеше нет данных")
        lateinit var response: HttpResponse
        try {
            //Downloader.cache.HtmlChannel.send(url)
            response = client.get(url)
            //println(response.toString())
            return response.bodyAsText()
        } catch (e: Exception) {
            Timber.e("Ошибка " + e.message)
        }

        //Сохраним в кеш данный html
        //client.close()
        return ""


}