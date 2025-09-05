package com.client.xvideos.l.ui.urlImage

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import com.client.xvideos.common.AppPath
import com.composeunstyled.Text
import com.skydoves.landscapist.ImageOptions
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.io.File


data class StaticImageWrapper(
    val source: Any,
    val isStatic: Boolean = true
) {
    // Glide будет использовать source для загрузки
    override fun toString(): String = source.toString()
}


//@Composable
//fun UrlImageLusciousGifsGlide(
//    url: String,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.FillWidth,
//    loadIndicator: Boolean = true,
//    onLoading: (Boolean) -> Unit = {},
//    onSuccess: (Boolean) -> Unit = {},
//    albumName: String,
//    isAnimated: Boolean = false
//) {
//    // Определяем имя файла и проверяем наличие
//    val fileName = url.substringAfterLast('/').substringBefore('?')
//    val file = when (albumName) {
//        "likes", "crypto" -> File(url)
//        else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
//    }
//
//    val dataSource: Any = if (file.exists()) {
//        when (albumName) {
//            "likes" -> file
//            "crypto" -> EncryptedFileModel(file)
//            else -> file
//        }
//    } else {
//        url
//    }
//
//    val context = LocalContext.current
//    var isPlaying by remember { mutableStateOf(true) } // Анимация по умолчанию включена
//    var isLoading by remember { mutableStateOf(true) }
//
//    val animatedFile = isAnimated ||
//            fileName.endsWith(".gif", true) ||
//            fileName.endsWith(".webp", true)
//
//    // Оборачиваем в Column, чтобы кнопка отображалась
//    Column(
//        modifier = modifier
//    ) {
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth()
//        ) {
//            AndroidView(
//                factory = { context ->
//                    val view = ImageView(context).apply {
//                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
//                        adjustViewBounds = true
//                        scaleType = when (contentScale) {
//                            ContentScale.FillWidth -> ImageView.ScaleType.CENTER_CROP
//                            ContentScale.Fit -> ImageView.ScaleType.FIT_CENTER
//                            ContentScale.Crop -> ImageView.ScaleType.CENTER_CROP
//                            else -> ImageView.ScaleType.CENTER_CROP
//                        }
//                    }
//
//                    Glide.with(view)
//                        .asDrawable()
//                        .load(dataSource)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .addListener(object : RequestListener<Drawable> {
//                            override fun onLoadFailed(
//                                e: GlideException?,
//                                model: Any?,
//                                target: Target<Drawable>,
//                                isFirstResource: Boolean
//                            ): Boolean {
//                                isLoading = false
//                                onLoading(false)
//                                return false
//                            }
//
//                            override fun onResourceReady(
//                                resource: Drawable,
//                                model: Any,
//                                target: Target<Drawable>?,
//                                dataSource: DataSource,
//                                isFirstResource: Boolean
//                            ): Boolean {
//                                isLoading = false
//                                onLoading(false)
//                                onSuccess(true)
//
//                                if (resource is GifDrawable) {
//                                    if (isPlaying) {
//                                        resource.start()
//                                    } else {
//                                        resource.stop()
//                                    }
//                                }
//                                return false // Позволяем Glide установить drawable
//                            }
//                        })
//                        .into(view)
//
//                    view
//                },
//                update = { view ->
//                    // Обновляем состояние анимации при изменении isPlaying
//                    (view.drawable as? GifDrawable)?.let { gifDrawable ->
//                        if (isPlaying) {
//                            gifDrawable.start()
//                        } else {
//                            gifDrawable.stop()
//                        }
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Gray)
//            )
//
//            // Индикатор загрузки
//            if (isLoading && loadIndicator) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(32.dp),
//                        color = Color.White
//                    )
//                }
//            }
//        }
//
//        // Кнопка Play/Pause для GIF и WebP
//        if (animatedFile) {
//            Button(
//                onClick = {
//                    isPlaying = !isPlaying
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                )
//            ) {
//                Text(
//                    text = if (isPlaying) "⏸ Пауза" else "▶ Старт",
//                    color = Color.White
//                )
//            }
//        }
//    }
//}













//@Composable
//fun UrlImageLusciousGifsGlide(
//    url: String,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.FillWidth,
//    loadIndicator: Boolean = true,
//    onLoading: (Boolean) -> Unit = {},
//    onSuccess: (Boolean) -> Unit = {},
//    albumName: String,
//    isAnimated: Boolean = false
//)
//{
//    // Определяем имя файла и проверяем наличие
//    val fileName = url.substringAfterLast('/').substringBefore('?')
//    val file = when (albumName) {
//        "likes", "crypto" -> File(url)
//        else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
//    }
//
//    val dataSource: Any = if (file.exists()) {
//        when (albumName) {
//            "likes" -> file
//            "crypto" -> EncryptedFileModel(file)
//            else -> file
//        }
//    } else {
//        url
//    }
//
//    val context = LocalContext.current
//
//    var isPlaying by remember { mutableStateOf(false) } // Анимация по умолчанию включена
//
//    val animatedFile =
//        isAnimated || fileName.endsWith(".gif", true) || fileName.endsWith(".webp", true)
//
//
////    // Создаем разные модели для анимированного и статичного контента
////    val imageModel = remember(dataSource, isPlaying, animatedFile) {
////        if (isPlaying && animatedFile) {
////            // Для анимации - возвращаем исходный dataSource
////            dataSource
////        } else {
////            // Для статичного изображения - можем обернуть в специальный wrapper
////            // или просто использовать dataSource с флагом
////            StaticImageWrapper(dataSource, isStatic = true).toString()
////        }
////    }
//
//
////    val gGif = Glide
////        .with(context)
////        .asDrawable()
////        .load(dataSource)
////        .diskCacheStrategy(DiskCacheStrategy.ALL)
////        .centerCrop()
////
////    val gBitmap = Glide
////        .with(context)
////        .asDrawable()
////        .load(dataSource)
////        .diskCacheStrategy(DiskCacheStrategy.ALL)
////        .centerCrop()
//
//
////    GlideImage(
////        imageModel = { imageModel }, // локальный файл или URL
////        modifier = modifier.background(Color.Black),
////        imageOptions = ImageOptions(
////            contentScale = contentScale,
////            alignment = Alignment.Center,
////        ),
////        requestBuilder = {
////
////            if (isPlaying && animatedFile) {
////                gGif
////            }else{
////                gBitmap
////            }
////
////        },
////
////        loading = {
////            onLoading(true)
////            if (loadIndicator) {
////                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
////                    CircularProgressIndicator(
////                        modifier = Modifier.size(32.dp),
////                        color = Color.Gray
////                    )
////                }
////            }
////        },
////        success = { _, drawable ->
////            Image(
////                painter = drawable,
////                modifier = modifier,
////                contentDescription = "Image"
////            )
////        },
////        failure = {
////            onLoading(false)
////            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
////                Text("Ошибка загрузки", color = Color.Gray)
////            }
////        }
////    )
//
//
//    AndroidView(
//        factory = { context ->
//            val view = ImageView(context).apply {
//                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
//                adjustViewBounds = true
//            }
//
//            Glide.with(view).asDrawable().load(dataSource)
//                .addListener(object : RequestListener<Drawable> {
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        model: Any,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        if (resource is GifDrawable) {
//                            if (isPlaying) {
//                                resource.start()
//                                //resource.startFromFirstFrame()
//                            } else {
//                                resource.stop()
//                            }
//                        }
//                        view.setImageDrawable(resource)
//                        return true
//                    }
//                }).into(view)
//
//            view
//        },
//        update = { view ->
//            (view.drawable as? GifDrawable)?.let { gifDrawable ->
//
//                if (isPlaying) {
//                    gifDrawable.start()
//                } else {
//                    gifDrawable.stop()
//                }
//                view.setImageDrawable(gifDrawable)
//            }
//        }, modifier = modifier
//            .fillMaxSize()
//            .background(Color.Gray)
//
//    )
//
//
//    // Кнопка Play/Pause для GIF и WebP
//    if (fileName.endsWith(".gif", true) || fileName.endsWith(".webp", true)) {
//        Button(
//            onClick = {
//                isPlaying = !isPlaying
//            },
//            modifier = Modifier.padding(top = 8.dp)
//        ) {
//            Text(if (isPlaying) "⏸ Пауза" else "▶ Старт")
//        }
//    }
//
//
//}


//@Composable
//fun UrlImageLusciousGifsGlide(
//    url: String,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.FillWidth,
//    loadIndicator: Boolean = true,
//    onLoading: (Boolean) -> Unit = {},
//    onSuccess: (Boolean) -> Unit = {},
//    albumName: String
//) {
//
//    // Определяем имя файла и проверяем наличие
//    val fileName = url.substringAfterLast('/').substringBefore('?')
//    val file = when (albumName){
//        "likes", "crypto"  -> File(url)
//        else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
//    }
//
//    //val dataSource: Any = if (file.exists()) file else url
//
//    val dataSource: Any = if (file.exists()) {
//        when (albumName){
//            "likes" -> file
//            "crypto" -> EncryptedFileModel(file)
//            else -> file
//        }
//    } else {
//        url
//    }
//
//    var isPlaying by remember { mutableStateOf(false) }
//    var gifDrawable by remember { mutableStateOf<GifDrawable?>(null) }
//
//    GlideImage(
//        imageModel = { dataSource }, // локальный файл или URL
//        modifier = modifier,
//        imageOptions = ImageOptions(
//            contentScale = contentScale,
//            alignment = Alignment.Center,
//        ),
//
//        requestBuilder = {
//            Glide
//                .with(LocalContext.current)
//                .asDrawable()
//                .load(dataSource)
//                .listener(object : RequestListener<Drawable> {
//
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable?>,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        model: Any,
//                        target: Target<Drawable?>?,
//                        dataSource: DataSource,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        // Сохраняем GifDrawable для управления анимацией
//                       val gifDrawable1 = resource
//
//                       if (gifDrawable1 is GifDrawable) gifDrawable1.stop()
//
////                        if (!isPlaying) {
////                            resource.stop()
////                        }
//                        return false
//                    }
//                })
//        },
//
//
//
//        requestOptions = {
//            RequestOptions()
//                //.override(50)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .centerCrop()
//        },
//        loading = {
//            onLoading(true)
//            if (loadIndicator) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(32.dp),
//                        color = Color.Gray
//                    )
//                }
//            }
//        }
//,
//        success = { state, drawable ->
//
//            Image(
//                painter = drawable,
//                modifier = Modifier.size(128.dp), // draw a resized image.
//                contentDescription = "Image"
//            )
//
//           // (drawable as? AnimationDrawable)?.stop()
//
//
////            if (drawable is Drawable) {
////                val a =  drawable as GifDrawable
////
////                //gifDrawable = drawable
////                //if (isPlaying) drawable.start()
////                //else
////                //    drawable.stop()
////                //isPlaying = !isPlaying
////                a.stop()
////            }
//
//            onSuccess(true)
//
//        }
//
//
//        ,
//        failure = {
//            onLoading(false)
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("Ошибка загрузки", color = Color.Gray)
//            }
//        },
//
//    )
//
//
//
//    // Кнопка Play/Pause
//    if (fileName.endsWith(".gif", true) || fileName.endsWith(".webp", true)) {
//        Button(
//            onClick = {
//                isPlaying = !isPlaying
//                if (isPlaying) {
//                    gifDrawable?.start()
//                } else {
//                    gifDrawable?.stop()
//                }
//            },
//            modifier = Modifier//.align(Alignment.BottomCenter)
//        ) {
//            Text(if (isPlaying) "⏸ Пауза" else "▶ Старт")
//        }
//    }
//}

//@Composable
//fun UrlImageLusciousGifs(
//    url: String,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.FillWidth,
//    loadIndicator: Boolean = true,
//    isGrayscale: Boolean = false,
//    onLoading: (Boolean) -> Unit = {},
//    onSuccess: (Boolean) -> Unit = {},
//    albumName: String
//) {
//
//    val context = LocalContext.current
//
//    val colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.9f) })
//
//    // Определяем имя файла и проверяем наличие
//    val fileName = url.substringAfterLast('/').substringBefore('?')
//    //val file = File(AppPath.downloaded_albums_l, "$albumName/$fileName")
//
//    val file = if (albumName != "likes") {
//        File(AppPath.downloaded_albums_l, "$albumName/$fileName")
//    } else {
//        File(AppPath.likes_l, url)
//    }
//
//    val dataSource: Any = if (file.exists()) file else url  // либо локальный файл, либо сеть
//
//    val imageRequest = remember {
//        ImageRequest.Builder(context)
//            .data(dataSource)
//            .crossfade(true)
//            .apply {
//                memoryCacheKey("${url}_preview")
//                diskCacheKey("${url}_preview")
//            }
//            .diskCachePolicy(CachePolicy.ENABLED)
//            .memoryCachePolicy(CachePolicy.ENABLED)
//            .dispatcher(Dispatchers.Default)
//            .build()
//    }
//
//    val imageLoader = remember {
//        ImageLoader.Builder(context)
//            .components {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    add(ImageDecoderDecoder.Factory())
//                } // Использует modern API
//                add(GifDecoder.Factory()) // Fallback для старых API
//            }
//            .build()
//    }
//
//    CoilImage(
//        imageLoader = { imageLoader },
//        imageRequest = { imageRequest },
//        imageOptions = ImageOptions(
//            contentScale = contentScale,
//            alignment = Alignment.Center,
//            colorFilter = if (isGrayscale) colorFilter else null
//        ),
//        modifier = Modifier.then(modifier),
//
//        success = { state, painter ->
//            onSuccess(true)
//            Image(
//                painter = painter,
//                modifier = Modifier.fillMaxSize(),//.size(128.dp), // draw a resized image.
//                contentDescription = "Image"
//            )
//        },
//
//        loading = {
//            if (loadIndicator) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(32.dp)//.align(Alignment.Center)
//                        , color = Color.Gray
//                    )
//                }
//            }
//        },
//
//
//        failure = {
//            Timber.e(">>>>>>>>>>>>" + it.reason)
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("Ошибка загрузки", color = Color.Gray)
//            }
//        }
//
//
//    )
//
//}

//@Composable
//fun UrlImageLusciousGifsFull(
//    url: String,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.FillWidth,
//    onSuccess: (Boolean) -> Unit = {},
//    albumName: String
//) {
//    // Определяем имя файла и проверяем наличие
//    val fileName = url.substringAfterLast('/').substringBefore('?')
//
//    val file = when (albumName) {
//        "likes", "crypto" -> File(url)
//        else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
//    }
//
//    val dataSource: Uri = if (file.exists()) {
//        Uri.fromFile(file) // локальный файл
//    } else {
//        Uri.parse(url)
//    }// сетевой url
//
//    var isLoading by remember { mutableStateOf(true) }
//    var hasError by remember { mutableStateOf(false) }
//
//    val controllerBuilder = remember(dataSource) {
//        {
//            Fresco.newDraweeControllerBuilder()
//                .setUri(dataSource)
//                .setControllerListener(object : BaseControllerListener<ImageInfo>() {
//                    override fun onSubmit(id: String?, callerContext: Any?) {
//                        isLoading = true
//                        hasError = false
//                    }
//
//                    override fun onFinalImageSet(
//                        id: String?,
//                        imageInfo: ImageInfo?,
//                        animatable: Animatable?
//                    ) {
//                        isLoading = false
//                        hasError = false
//                        onSuccess(true)
//                    }
//
//                    override fun onFailure(id: String?, throwable: Throwable?) {
//                        isLoading = false
//                        hasError = true
//                    }
//                })
//                .setAutoPlayAnimations(true)
//        }
//    }
//
//    Box {
//        FrescoWebImage(
//            controllerBuilder = controllerBuilder,
//            modifier = Modifier.then(modifier)
//        )
//
//        if (isLoading) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(32.dp),
//                    color = Color.Gray
//                )
//            }
//        }
//
//        if (hasError) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("Ошибка загрузки", color = Color.Gray)
//            }
//        }
//    }
//}
