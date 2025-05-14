package com.client.xvideos.net

import android.util.LruCache
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.client.xvideos.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

private val lruCacheHTML: LruCache<String, String> = LruCache<String, String>(1000)

//suspend fun readHtmlFromURL(url: String = "https://www.xvideos.com"): String {
//
//    Timber.i("!!!..readHtmlFromURL $url ")
//
//    val client = HttpClient(OkHttp)
//    {
//        install(HttpTimeout)
//        {
//            requestTimeoutMillis = Long.MAX_VALUE
//        }
//        install(HttpCookies){
//            storage = AcceptAllCookiesStorage() // Хранение всех куки
//        }
//        followRedirects = true // Обработка редиректов
//        defaultRequest {
//            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
//            header("Accept-Language", "en-US,en;q=0.9")
//        }
//    }
//
//    lateinit var response: HttpResponse
//    try {
//        response = client.get(url)
//        val body = response.bodyAsText()
//
//        //val storage = client.plugin(HttpCookies).get(Url(url))
//        val cookies = client.plugin(HttpCookies).get(Url("https://www.xv-ru.com"))
//        Timber.i("!!! Cookies: $cookies")
//
//        return body
//    } catch (e: Exception) {
//        Timber.e("!!! readHtmlFromURL: Ошибка " + e.message)
//    } finally {
//        client.close()
//    }
//
//    return ""
//
//}

suspend fun readHtmlFromURL(url: String = "https://www.xvideos.com"): String =

    suspendCancellableCoroutine { continuation ->

        CoroutineScope(Dispatchers.Main).launch {

            Timber.i("!!!..readHtmlFromURL $url ")

            val context = App.instance.applicationContext

            val webView = WebView(context)

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(webView, true)

            with(webView.settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            webView.webChromeClient = WebChromeClient()

            webView.webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, finishedUrl: String?) {
                    super.onPageFinished(view, finishedUrl)

                    if (!continuation.isActive) return

                    CookieManager.getInstance().flush()

                    webView.evaluateJavascript(
                        "(function() { return document.documentElement.outerHTML; })();"
                    ) { html ->

                        Timber.i("!!!..readHtmlFromURL end $url")

                        continuation
                            .resume(
                                html.trim('"')
                                    .replace("\\u003C", "<")
                                    .replace("\\n", "\n")
                                    .replace("\\\"", "\"")
                            )
                    }
                }
            }

            webView.loadUrl(url)

            continuation.invokeOnCancellation { cause ->
                webView.destroy()
            }


        }
    }


