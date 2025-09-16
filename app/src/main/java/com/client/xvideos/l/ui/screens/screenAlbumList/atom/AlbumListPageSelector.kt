package com.client.xvideos.l.ui.screens.screenAlbumList.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.client.xvideos.l.theme.ThemeL

@Preview
@Composable
private fun preview() {

    AlbumListPageSelector(1, 199)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListPageSelector(
    page: Int,
    pageMax: Int,
    haptic: () -> Unit = {},
    onChange: (Int) -> Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) { haptic.invoke() }

    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box( modifier = Modifier.fillMaxHeight().weight(1f).background(ThemeL.red)
                .clickable( onClick = { onChange((page - 1).coerceAtLeast(1)) } ),  contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.KeyboardArrowLeft, tint = Color.White, contentDescription = null) }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val color = ThemeL.grey3.toArgb()

                    // верхняя линия
                    drawLine(
                        color = ThemeL.grey3,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )
                    // нижняя линия
                    drawLine(
                        color = ThemeL.grey3,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                .clickable(onClick = { expanded = true }),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Page ${page + 1} of $pageMax",
                color = ThemeL.textColor,
                fontFamily = ThemeL.fontFamilyKarla,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(ThemeL.red)
                .clickable(
                    onClick = { onChange((page + 1).coerceAtMost(pageMax)) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                tint = Color.White,
                contentDescription = null
            )
        }
    }


    var number by remember { mutableStateOf((page+1).toString()) }

    //-- Диалог --
    if (expanded) {

        Dialog(onDismissRequest = { expanded = false }) {

            Box( Modifier.background(ThemeL.grey4) )
            {

                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                        TextField(
                            number, onValueChange = { number = it },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword
                            ), singleLine = true, maxLines = 1, modifier = Modifier.padding(end = 8.dp).width(96.dp)
                            , colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF3B3B3B),
                                unfocusedContainerColor = Color(0xFF3B3B3B),
                                disabledContainerColor = Color(0xFF3B3B3B),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedTextColor = ThemeL.textColor,
                                unfocusedTextColor = ThemeL.textColor,
                                disabledTextColor = ThemeL.textColor,
                            ), textStyle = TextStyle(
                                color = ThemeL.textColor,
                                fontFamily = ThemeL.fontFamilyKarla,
                                fontSize = 20.sp
                            ), keyboardActions = KeyboardActions(
                                onDone = {
                                    expanded = false
                                    onChange(number.toInt() - 1)
                                }
                            )
                        )
                        Text("of ${pageMax}",
                            color = ThemeL.textColor,
                            fontFamily = ThemeL.fontFamilyKarla, fontSize = 20.sp)
                    }



//                    Row(Modifier.padding(bottom = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround)
//                    {
//                        Spacer(Modifier.weight(0.25f))
//                        Box(
//                            modifier = Modifier
//                                .height(48.dp)
//                                .weight(1f)
//                                .clip(RoundedCornerShape(4.dp))
//                                .border(1.dp, ThemeL.grey2, RoundedCornerShape(4.dp)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("Cancel", color = ThemeL.textColor)
//                        }
//
//                        Spacer(Modifier.weight(0.5f))
//
//                        Box(
//                            modifier = Modifier
//                                .height(48.dp)
//                                .weight(1f)
//                                .clip(RoundedCornerShape(4.dp))
//                                .background(ThemeL.red)
//                                .clickable( onClick = {
//                                        expanded = false
//                                        onChange(number.toInt() - 1)
//                                    }
//                                )
//                            , contentAlignment = Alignment.Center
//                        ) {
//                            Text("Go", color = Color.White)
//                        }
//                        Spacer(Modifier.weight(0.25f))
//
//                    }


            }
        }
    }

}




