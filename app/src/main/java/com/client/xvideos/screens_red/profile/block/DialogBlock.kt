package com.client.xvideos.screens_red.profile.block

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.rememberDialogState


@Composable
fun DialogBlock(vm: ScreenRedProfileSM) {

    val dialogState = rememberDialogState(false)

    if (vm.blockVisibleDialog) {

        Dialog(state = dialogState) {
            DialogPanel(
                modifier = Modifier
                    .displayCutoutPadding()
                    .systemBarsPadding()
                    .widthIn(min = 280.dp, max = 560.dp)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE4E4E4), RoundedCornerShape(12.dp))
                    .background(Color.White),
            ) {
                Column {
                    Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
                        Text(
                            text = "Update Available",
                            style = TextStyle(fontWeight = FontWeight.Medium)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "A new version of the app is available. Please update to the latest version.",
                            style = TextStyle(color = Color(0xFF474747))
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.padding(12.dp).align(Alignment.End),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Update",
                            color = Color(0xFF6699FF)
                        )
                    }
                }
            }

        }



    }

}