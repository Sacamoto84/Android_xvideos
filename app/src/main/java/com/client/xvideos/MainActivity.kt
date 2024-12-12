package com.client.xvideos

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cafe.adriel.voyager.navigator.Navigator
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.client.xvideos.screens.tags.ScreenTags
import com.client.xvideos.search.getSearchResults
import com.client.xvideos.search.parseJson
import com.client.xvideos.sign_in.domen.GoogleAuthUiClient
import com.client.xvideos.ui.theme.XvideosTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import io.sanghun.compose.video.cache.VideoPlayerCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant

val urlStart = "https://www.xv-ru.com"

private lateinit var auth: FirebaseAuth

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ImageLoaderFactory {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(1000 * 1024 * 1024)
                    .build()
            }
            .respectCacheHeaders(false)
            .allowHardware(true)
            .allowRgb565(true)
            .interceptorDispatcher(Dispatchers.IO)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .bitmapFactoryMaxParallelism(8)
            .build()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        var currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)

        val idToken = googleCredential.googleIdToken

        when {
            idToken != null -> {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.tag("onActivityResult").d("signInWithCredential:success")
                            val user = auth.currentUser
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.tag("onActivityResult")
                                .w(task.exception, "signInWithCredential:failure")
                            //updateUI(null)
                        }
                    }
            }

            else -> {
                // Shouldn't happen.
                Timber.tag("onActivityResult").d("No ID token!")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        plant(DebugTree())

        Timber.i("!!! Hello")

        //val a =1 /0

        oneTapClient = Identity.getSignInClient(this)

        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()
//
//        oneTapClient.beginSignIn(signUpRequest)
//            .addOnSuccessListener(this) { result ->
//                try {
//                    startIntentSenderForResult(
//                        result.pendingIntent.intentSender, REQ_ONE_TAP,
//                        null, 0, 0, 0)
//                } catch (e: IntentSender.SendIntentException) {
//                    Timber.tag("oneTapClient.beginSignIn")
//                        .e("Couldn't start One Tap UI: ${e.localizedMessage}")
//                }
//            }
//            .addOnFailureListener(this) { e ->
//                // No Google Accounts found. Just continue presenting the signed-out UI.
//                Timber.tag("oneTapClient.beginSignIn").d(e.localizedMessage)
//            }


        auth = Firebase.auth

        val userId = auth.currentUser?.uid
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        if (userId == null) {
            // Ссылка на документ пользователя
            val userDocRef = db.collection("users").document("userId")

            // Добавление данных в подколлекцию "posts"
            val post = hashMapOf(
                "title" to "Первый пост",
                "content" to "Это содержимое первого поста"
            )

            userDocRef.collection("posts").add(post)
                .addOnSuccessListener { documentReference ->
                    Timber.tag("Firestore").d("Пост добавлен с ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Ошибка добавления поста", e)
                }

            // Добавление данных в подколлекцию "messages"
            val message = hashMapOf(
                "sender" to "Друг пользователя",
                "text" to "Привет!"
            )

            userDocRef.collection("messages").add(message)
                .addOnSuccessListener { documentReference ->
                    Timber.tag("Firestore").d("Сообщение добавлено с ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Timber.tag("Firestore").w(e, "Ошибка добавления сообщения")
                }

        } else {
            Timber.tag("Firestore").w("!!! Пользователь не аутентифицирован")
        }


//        val solarSystem = db.collection("solar_system")
//        // Add a document
//        solarSystem.add(
//            mapOf(
//                "name" to "Mercury",
//                "number" to 1,
//                "gravity" to 3.7
//            )
//        )
//        // Add another document
//        solarSystem.add(
//            mapOf(
//                "name" to "Venus",
//                "number" to 2,
//                "gravity" to 8.87
//            )
//        )
//
//        solarSystem.document("PLANET_EARTH")
//            .set(mapOf(
//                "name" to "Earth",
//                "number" to 3,
//                "gravity" to 9.807
//            ))

        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)    // 1GB

        runBlocking {
            try {
                val a = getSearchResults("sist")
                a
                val b = a?.let { parseJson(it) }
                b
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }
            //b
        }





        setContent {
            XvideosTheme(darkTheme = true) {
                Navigator(ScreenTags("blonde"))
            }
        }




    }
}
