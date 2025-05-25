package com.client.xvideos.screens_red.profile.block

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.DialogState
import com.composables.core.HorizontalSeparator
import com.composables.core.rememberDialogState

@Composable
fun DialogBlock(vm: ScreenRedProfileSM, onBlockConfirmed: () -> Unit) {

    var dialogState = rememberDialogState(vm.blockVisibleDialog)

    LaunchedEffect(vm.blockVisibleDialog) {
        dialogState.visible = vm.blockVisibleDialog
    }

    if (vm.blockVisibleDialog) {

        Dialog(
            onDismiss = { vm.blockVisibleDialog = false },
            state = dialogState,

        ) {
            DialogPanel(
                modifier = Modifier
                    .displayCutoutPadding()
                    .systemBarsPadding()
                    .widthIn(min = 280.dp, max = 560.dp)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE4E4E4), RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                Column {
                    Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
                        Text(
                            text = "Подтвердите блокировку",
                            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Вы уверены, что хотите заблокировать этот GIFs?",
                            style = TextStyle(color = Color(0xFF474747), fontSize = 14.sp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    HorizontalSeparator(Color(0xFF9E9E9E))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {

                        TextButton(
                            onClick = { vm.blockVisibleDialog = false }
                        ) {
                            Text("Отмена")
                        }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onBlockConfirmed()
                                vm.blockVisibleDialog = false
                            },
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Блокировать",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

}