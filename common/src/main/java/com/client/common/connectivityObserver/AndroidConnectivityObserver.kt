package com.client.common.connectivityObserver

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import com.client.common.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Singleton


//@Module
//@InstallIn(SingletonComponent::class)
//object modileConnectivityObserver {
//
//    @Provides
//    @Singleton
//    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
//        println("!!! DI ConnectivityObserver")
//        return AndroidConnectivityObserver(context = context)
//    }
//
//}

interface ConnectivityObserver {
    val isConnected: StateFlow<Boolean>
}

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {

    @Provides
    @Singleton
    fun provideConnectivityObserver(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope
    ): ConnectivityObserver {
        return AndroidConnectivityObserver(context, scope)
    }
}



class AndroidConnectivityObserver(
    context: Context,
    scope: CoroutineScope // <-- нужно передавать извне, например, ApplicationScope
) : ConnectivityObserver {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected

    init {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.w("!!! 999 onAvailable")
                _isConnected.value = true
            }

            override fun onLost(network: Network) {
                Timber.w("!!! 999 onLost")
                _isConnected.value = false
            }

            override fun onUnavailable() {
                Timber.w("!!! 999 onUnavailable")
                _isConnected.value = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Timber.w("!!! 999 onCapabilitiesChanged")
                val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                _isConnected.value = isValidated
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Timber.w("!!! 999 registerDefaultNetworkCallback")
                connectivityManager.registerDefaultNetworkCallback(callback)
            } else {
                Timber.w("!!! 999 registerNetworkCallback (legacy)")
                val request = NetworkRequest.Builder().build()
                connectivityManager.registerNetworkCallback(request, callback)
            }
        } catch (e: Exception) {
            Timber.e(e, "!!! 999 Failed to register network callback")
        }
    }
}
