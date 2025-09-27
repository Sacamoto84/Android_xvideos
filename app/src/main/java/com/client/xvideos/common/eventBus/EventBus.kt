package com.client.xvideos.common.eventBus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class Event {
    data class Log(val message: String) : Event()
    data class ServerDate(val date: String) : Event()
    data class RequestCount(val count: Int) : Event()
    object ArchiveCountIncrement : Event()

    data class SnackBarText(val message: String) : Event()
    data class SnackBarRaw(val message: UiMessage) : Event()
}

object EventBus {
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

//    suspend fun postEvent(event: UpdateEvent) {
//        _events.emit(event)
//    }

    fun postEvent(event: Event) {
        _events.tryEmit(event)
    }

}
