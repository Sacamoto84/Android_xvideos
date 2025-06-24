package com.client.xvideos.red.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.red.screens.explorer.ScreenRedExplorer
import com.client.xvideos.red.screens.top_this_week.ScreenRedTopThisWeekSM
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

val LocalRootScreenModel = staticCompositionLocalOf<ScreenRedRootSM> {
    error("No ScreenRedRootSM provided")
}

class ScreenRedRoot() : Screen {

    override val key: ScreenKey = "ScreenRedTopThisWeek"

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val root: ScreenRedRootSM = getScreenModel()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            root.snackbarEvents.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }

        CompositionLocalProvider(LocalRootScreenModel provides root) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }) {
                Navigator(ScreenRedExplorer())
            }
        }
    }

}

class ScreenRedRootSM @Inject constructor(
) : ScreenModel {
    private val _snackbarEvents = Channel<String>(64)
    val snackbarEvents = _snackbarEvents.receiveAsFlow()

    @OptIn(DelicateCoroutinesApi::class)
    fun showSnackbar(message: String) {
        GlobalScope.launch {
            _snackbarEvents.send(message)
        }
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedRootBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedRootSM::class)
    abstract fun bindScreenRedRootScreenModel(hiltListScreenModel: ScreenRedRootSM): ScreenModel
}
