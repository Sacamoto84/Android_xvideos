package com.client.xvideos.l.ui.element

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.Album






//.aspectRatio(640f/935)
@Composable
fun AlbumListItem(
    title : String,
    coverUrl : String,
    numberOfAnimatedPictures : Int,
    numberOfPictures : Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
)
{

    Column(modifier = Modifier.then(modifier).fillMaxWidth().border(1.dp, ThemeL.grey3).clickable(onClick = onClick)) {
        UrlImage(coverUrl, modifier = Modifier.fillMaxWidth().aspectRatio(137f/200)//.width(137.dp).height(200.dp)
            , contentScale = ContentScale.Crop)

        Text(title.removePrefix(" "), modifier = Modifier, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp)

        Row {
            val str = StringBuilder()
            if (numberOfAnimatedPictures > 0){
                str.append("$numberOfAnimatedPictures gifs")
                if (numberOfPictures > 0) str.append(" / ")
            }
            if(numberOfPictures > 0) {
                str.append(numberOfPictures.toString())
                if ( numberOfAnimatedPictures == 0 ) str.append(" pictures")
            }
            Text(str.toString(), modifier = Modifier, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, fontSize = 14.sp)
        }

    }

}

@Preview
@Composable
fun AlbumListItemPreview() {
    AlbumListItem(
        title = "Album Title",
        coverUrl = "https://i.pinimg.com/1200x/2c/86/8d/2c868d9ab0c4d4f3a76631c1b0077058.jpg",
        numberOfAnimatedPictures = 5,
        numberOfPictures = 10,
        onClick = {}
    )
}
