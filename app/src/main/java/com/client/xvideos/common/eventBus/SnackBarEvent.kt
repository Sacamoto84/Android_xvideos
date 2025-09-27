package com.client.xvideos.common.eventBus

sealed interface UiMessage {
    val text: String
    data class Info(override val text: String): UiMessage
    data class Success(override val text: String): UiMessage
    data class Error(override val text: String): UiMessage
}

fun snackBarInfo(message: String) {
    EventBus.postEvent( Event.SnackBarRaw(UiMessage.Info(message)))
}

fun snackBarError(message: String) {
    EventBus.postEvent( Event.SnackBarRaw(UiMessage.Error(message)))
}

fun snackBarSuccess(message: String) {
    EventBus.postEvent( Event.SnackBarRaw(UiMessage.Success(message)))
}
