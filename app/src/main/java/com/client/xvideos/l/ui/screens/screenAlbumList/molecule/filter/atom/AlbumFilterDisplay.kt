package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.ThemeL



@Preview(showSystemUi = false, showBackground = false)
@Composable
fun PreviewAlbumFilterDisplay() {
    AlbumFilterDisplay()
}

@Composable
fun AlbumFilterDisplay() {

    Column(){
        Box(modifier = Modifier.height(46.dp).width(64.dp).background(ThemeL.grey3).border(1.dp, ThemeL.grey5,RoundedCornerShape(4.dp))) {

        }
        Box(modifier = Modifier.height(46.dp).width(64.dp).background(ThemeL.grey3).border(1.dp, ThemeL.grey5,RoundedCornerShape(4.dp))) {

        }
    }

}