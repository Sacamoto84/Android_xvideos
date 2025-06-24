package com.client.xvideos.red.common.snackBar

import kotlinx.coroutines.channels.Channel

object SnackBarEvent {
    val infoChannel = Channel<UiMessage>(64)
}