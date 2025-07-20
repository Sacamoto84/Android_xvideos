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

    override val isConnected: StateFlow<Boolean> = callbackFlow {
        val callback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onUnavailable() {
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                trySend(
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(callback)
        } else {
            val request = NetworkRequest.Builder().build()
            connectivityManager.registerNetworkCallback(request, callback)
        }

        awaitClose {
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                Timber.tag("ConnectivityObserver").w(e, "Callback already unregistered")
            }
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
}
