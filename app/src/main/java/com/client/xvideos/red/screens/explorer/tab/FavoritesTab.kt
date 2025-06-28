package com.client.xvideos.red.screens.explorer.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.red.screens.explorer.ScreenRedExplorerSM

object  FavoritesTab : Screen {

    private fun readResolve(): Any = FavoritesTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerSM = getScreenModel()

        val haptic = LocalHapticFeedback.current

        Column {
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)}) { Text("TextHandleMove") }
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.ContextClick)}) { Text("ContextClick") }
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.Reject)}) { Text("Reject") }
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)}) { Text("ToggleOn") }

            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.Confirm)}) { Text("Confirm") }
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)}) { Text("GestureEnd") }
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)}) { Text("KeyboardTap") }
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)}) { Text("GestureThresholdActivate") }

            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)}) { Text("SegmentTicktivate") }
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress)}) { Text("LongPress") }
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)}) { Text("ToggleOff") }


        }
    }
}

