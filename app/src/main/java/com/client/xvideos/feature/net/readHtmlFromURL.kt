package com.client.xvideos.feature.net

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


