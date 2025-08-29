package com.client.xvideos.l.ui.screens.screenAlbumList.atom

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.Album

//.aspectRatio(640f/935)
@Composable
fun AlbumListItem(item: Album, onClick: () -> Unit = {}) {

    Column(modifier = Modifier.padding(vertical = 2.dp).width(137.dp).border(1.dp, ThemeL.grey3).clickable(onClick = onClick)) {
        UrlImage(item.cover.url, modifier = Modifier.width(137.dp).height(200.dp), contentScale = ContentScale.Crop)

        Text(item.title.removePrefix(" "), modifier = Modifier, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp)

        Row {
            val str = StringBuilder()
            if (item.numberOfAnimatedPictures > 0){
                str.append(item.numberOfAnimatedPictures.toString() + " gifs")
                if (item.numberOfPictures > 0) str.append(" / ")
            }
            if(item.numberOfPictures > 0) {
                str.append(item.numberOfPictures.toString())
                if ( item.numberOfAnimatedPictures == 0 ) str.append(" pictures")
            }
            Text(str.toString(), modifier = Modifier, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, fontSize = 14.sp)
        }

    }

}