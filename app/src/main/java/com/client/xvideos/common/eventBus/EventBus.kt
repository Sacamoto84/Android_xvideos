package com.client.xvideos.common.eventBus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class Event {
    data class Log(val message: String) : Event()

    object ArchiveCountIncrement : Event()



    //Показ снекбара с текстом из UiMessage
    data class SnackBarRaw(val message: UiMessage) : Event()



    data class X_FullScreenExitPosition(val position: Long) : Event()


}

object EventBus {
    private val _events = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 1024
    )
    val events = _events.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun postEvent(event: Event) {
        Timber.i("!!! ~~~ EventBus.postEvent $event")
        scope.launch { _events.emit(event) }
    }

}
