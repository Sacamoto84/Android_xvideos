package com.client.xvideos.redgifs.common.snackBar

sealed interface UiMessage {
    val text: String
    data class Info(override val text: String): UiMessage
    data class Success(override val text: String): UiMessage
    data class Error(override val text: String): UiMessage
}