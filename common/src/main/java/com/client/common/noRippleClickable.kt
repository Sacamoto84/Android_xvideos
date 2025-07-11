package com.client.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

//Version 1.0.0 Занесена в гримуар 31.01.2024

/**
 * Отключение риплы на кликах
 * Версия 1.0.0 31.01.2024
 */
fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit = {}
): Modifier = composed {
    clickable(
        enabled = enabled,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}