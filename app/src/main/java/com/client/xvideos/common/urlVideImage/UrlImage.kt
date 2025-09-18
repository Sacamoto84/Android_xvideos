package com.client.xvideos.common.urlVideImage

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.composeunstyled.Text
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.fresco.FrescoImage
import okhttp3.OkHttpClient
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

//private fun createUnsafeImageLoader(context: Context): ImageLoader {
//    val trustAllCerts = arrayOf<TrustManager>(
//
//        @SuppressLint("CustomX509TrustManager")
//    object : X509TrustManager {
//
//        @SuppressLint("TrustAllX509TrustManager")
//        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
//
//        @SuppressLint("TrustAllX509TrustManager")
//        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
//
//        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
//    })
//
//    val sslContext = SSLContext.getInstance("TLS")
//    sslContext.init(null, trustAllCerts, SecureRandom())
//
//    val okHttpClient = OkHttpClient.Builder()
//        .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
//        .hostnameVerifier { _, _ -> true }
//        .build()
//
//    return ImageLoader.Builder(context)
//        .okHttpClient(okHttpClient)
//        .build()
//}

/**
 * url: строка с адресом изображения
 */
@Composable
fun UrlImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    loadIndicator: Boolean = true
) {
    //Timber.d("!!! UrlImage: $url")
    Column(modifier = modifier) {
        FrescoImage(
            imageUrl = url,
            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            loading = {
                if (loadIndicator) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(
                                32.dp
                            ), color = Color.Gray
                        )
                    }
                }
            },
            failure = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Ошибка загрузки", color = Color.Gray) }
            },
        )
    }
}


@Composable
fun UrlImageCoil(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    loadIndicator: Boolean = true
) {

    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context)
        .data(url)
        .crossfade(true)
        .memoryCacheKey(url)
        .diskCacheKey(url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {

        //val imageLoader = remember { createUnsafeImageLoader(context) }

        CoilImage(
            //imageLoader = { imageLoader },
            imageRequest = { imageRequest },
            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center,
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
                Timber.e(">>>>>>>>>>>>" + it.reason)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка загрузки", color = Color.Gray)
                }
            }

        )

    } else {


        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        CoilImage(
            imageLoader = { imageLoader },
            imageRequest = { imageRequest },
            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center,
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
                Timber.e(">>>>>>>>>>>>" + it.reason)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка загрузки", color = Color.Gray)
                }
            }
        )
    }

}