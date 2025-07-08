package com.client.redgifs.common.snackBar

import kotlinx.coroutines.channels.Channel

object SnackBarEvent {
    val messages = Channel<UiMessage>(64)

    fun info(text : String) {messages.trySend(UiMessage.Info(text))}
    fun error(text : String) {messages.trySend(UiMessage.Error(text))}
    fun success(text : String) {messages.trySend(UiMessage.Success(text))}
}