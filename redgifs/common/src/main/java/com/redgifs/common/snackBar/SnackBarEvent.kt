package com.redgifs.common.snackBar

import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackBarEvent @Inject constructor(){
    val messages = Channel<UiMessage>(64)

    fun info(text : String) {messages.trySend(UiMessage.Info(text))}
    fun error(text : String) {messages.trySend(UiMessage.Error(text))}
    fun success(text : String) {messages.trySend(UiMessage.Success(text))}
}