package com.client.xvideos.net

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebView

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient

@SuppressLint("StaticFieldLeak")
object web {

    lateinit var webView: WebView

    @SuppressLint("StaticFieldLeak")
    fun createweb(context: Context) {

        webView = WebView(context).apply {

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true

           //settings.setAppCacheEnabled(true)

            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, true)

            webViewClient = object : WebViewClient() {
                @SuppressLint("StaticFieldLeak")
                override fun onPageFinished(view: WebView, url: String) {
                    println("Страница загружена: $url")

                    // Чтение Cookies
                    val cookies = cookieManager.getCookie(url)
                    println("Cookies: $cookies")

                    // Работа с localStorage
                    view.evaluateJavascript("localStorage.getItem('exampleKey');") { value ->
                        println("localStorage значение: $value")
                    }
                }
            }

            // Загрузка страницы
            loadUrl("https://example.com")
        }
    }



}