package com.client.xvideos.redgifs.common.saved.collection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.client.xvideos.redgifs.ThemeRed
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.composables.core.HorizontalSeparator

@Composable
fun DialogCollection(
    visible: Boolean,
    onDismiss: () -> Unit,
    onBlockConfirmed: () -> Unit,

    onSelectCollection: (String) -> Unit = {}
) {

    if (visible) {

        Dialog(
            onDismissRequest = onDismiss,
        ) {

            Column(
                modifier = Modifier
                    //.displayCutoutPadding()
                    //.systemBarsPadding()

                    //.widthIn(min = 280.dp, max = 560.dp)
                    //.padding(16.dp)

                    .heightIn(min = 280.dp, max = 560.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF3F3F3F), RoundedCornerShape(12.dp))
                    .background(Color(0xFF090909))
            ) {


                Text(
                    text = "Добавить в коллекцию", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp)
                )

                //Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        //.fillMaxHeight()
                        .fillMaxWidth()
                        .weight(1f)
                        //.background(Color.Blue)
                ){

                    LazyColumn(state = rememberLazyListState()) {
                        items(SavedRed.collectionList){
                              Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(vertical = 4.dp)
                                  .clickable(onClick = { onSelectCollection(it.collection) })
                                  , verticalAlignment = Alignment.CenterVertically){
                                  if(it.list.isNotEmpty()) {UrlImage(url = it.list.last().urls.thumbnail, modifier = Modifier.clip(RoundedCornerShape(25)).size(72.dp))}
                                  else{Box(modifier = Modifier.size(72.dp).background(Color.Gray))}
                                  Spacer(Modifier.width(8.dp))
                                  Text(it.collection, color = Color.White, fontFamily = ThemeRed.fontFamilyDMsanss)
                              }
                        }
                    }


                }


                //Spacer(Modifier.height(16.dp))

                HorizontalSeparator(Color(0xFF363636))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color(0xFF232323))
                        //.padding(vertical = 8.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Отмена")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = ThemeRed.colorYellow),
                        onClick = {
                            onBlockConfirmed()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "New Collection",
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

}