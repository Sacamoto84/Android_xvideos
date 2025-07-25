package com.client.common.urlVideImage

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.composeunstyled.Text
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import okhttp3.OkHttpClient
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private fun createUnsafeImageLoader(context: Context): ImageLoader {
    val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAllCerts, SecureRandom())

    val okHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .build()

    return ImageLoader.Builder(context)
        .okHttpClient(okHttpClient)
        .build()
}

/**
 * url: строка с адресом изображения
 */
@Composable
fun UrlImage(url: String, modifier: Modifier = Modifier,  contentScale : ContentScale = ContentScale.FillWidth, loadIndicator : Boolean = true, isGrayscale: Boolean = false) {

    val context = LocalContext.current

    val colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.9f) })

    val imageRequest = ImageRequest.Builder(context)
        .data(url)
        .crossfade(true)
        .memoryCacheKey(url)
        .diskCacheKey(url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {

        val imageLoader = remember { createUnsafeImageLoader(context) }

        CoilImage(
            imageLoader = { imageLoader },
            imageRequest = { imageRequest },
            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center,
                colorFilter = if (isGrayscale) colorFilter else null
            ),
            modifier = Modifier.then(modifier),
            loading = {

                //Box(modifier = Modifier.fillMaxSize().background(Color.Magenta))

                if (loadIndicator) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)//.align(Alignment.Center)
                            , color = Color.Gray
                        )
                    }
                }
            },
            failure = {
                Timber.e(">>>>>>>>>>>>"+it.reason)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка загрузки", color = Color.Gray)
                }
            }

        )

    }else{
        CoilImage(
            imageRequest = { imageRequest },
            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center,
                colorFilter = if (isGrayscale) colorFilter else null
            ),
            modifier = Modifier.then(modifier),
            loading = {
                //Box(modifier = Modifier.fillMaxSize().background(Color.Magenta))
                if (loadIndicator) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)//.align(Alignment.Center)
                            , color = Color.Gray
                        )
                    }
                }
            },
            failure = {
                Timber.e(">>>>>>>>>>>>"+it.reason)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка загрузки", color = Color.Gray)
                }
            }
        )
    }


}