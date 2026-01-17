package com.client.xvideos.common.snackbar

sealed interface UiMessage {
    val text: String

    data class Info(override val text: String): UiMessage
    data class Success(override val text: String): UiMessage
    data class Error(override val text: String): UiMessage
    data class Warning(override val text: String): UiMessage
}