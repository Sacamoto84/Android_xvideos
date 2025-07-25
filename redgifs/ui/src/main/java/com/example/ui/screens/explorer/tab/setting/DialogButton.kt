package com.example.ui.screens.explorer.tab.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.core.HorizontalSeparator

@Composable
fun DialogButton(
    visible: Boolean,
    title: String,
    body: String,
    buttonText: String,
    onDismiss: () -> Unit,
    onBlockConfirmed: () -> Unit,
)
{

    if (visible) {

        Dialog(
            onDismissRequest = onDismiss,
        ) {

            Box(
                modifier = Modifier
                    //.displayCutoutPadding()
                    //.systemBarsPadding()
                    .widthIn(min = 280.dp, max = 560.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE4E4E4), RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {

                Column {
                    Column(Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp)) {
                        androidx.compose.material3.Text(
                            text = title,
                            style = TextStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        androidx.compose.material3.Text(
                            text = body,
                            style = TextStyle(color = Color(0xFF474747), fontSize = 14.sp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    HorizontalSeparator(Color(0xFFCCCCCC))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {

                        TextButton(
                            onClick = onDismiss
                        ) {
                            androidx.compose.material3.Text("Отмена")
                        }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            onClick = {
                                onBlockConfirmed()
                                onDismiss()
                            },
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = buttonText,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

}
