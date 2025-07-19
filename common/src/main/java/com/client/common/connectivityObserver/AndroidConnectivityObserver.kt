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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object modileConnectivityObserver {

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        println("!!! DI ConnectivityObserver")
        return AndroidConnectivityObserver(context = context)
    }

}


interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
}

class AndroidConnectivityObserver(
    context: Context,
) : ConnectivityObserver {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!

    override val isConnected: Flow<Boolean>
        get() = callbackFlow {
            val callback = object : NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                    trySend(connected)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(false)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(true)
                }
            }

            var callbackRegistered = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(callback)
                callbackRegistered = true
            } else {
                // На Android < 7 можно использовать старый способ:
                val request = NetworkRequest.Builder().build()
                connectivityManager.registerNetworkCallback(request, callback)
                callbackRegistered = true
            }

            awaitClose {
                if (callbackRegistered) {
                    try {
                        connectivityManager.unregisterNetworkCallback(callback)
                    } catch (e: Exception) {
                        Timber.tag("ConnectivityObserver").w(e, "Callback already unregistered")
                    }
                }
            }
        }


//    override val isConnected: Flow<Boolean>
//        get() = callbackFlow {
//            val callback = object : NetworkCallback() {
//                override fun onCapabilitiesChanged(
//                    network: Network,
//                    networkCapabilities: NetworkCapabilities,
//                ) {
//                    super.onCapabilitiesChanged(network, networkCapabilities)
//                    val connected = networkCapabilities.hasCapability(
//                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
//                    )
//                    trySend(connected)
//                }
//
//                override fun onUnavailable() {
//                    super.onUnavailable()
//                    trySend(false)
//                }
//
//                override fun onLost(network: Network) {
//                    super.onLost(network)
//                    trySend(false)
//                }
//
//                override fun onAvailable(network: Network) {
//                    super.onAvailable(network)
//                    trySend(true)
//                }
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                connectivityManager.registerDefaultNetworkCallback(callback)
//            }
//
//            awaitClose {
//                connectivityManager.unregisterNetworkCallback(callback)
//            }
//        }
}
