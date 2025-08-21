package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.AlbumType
import com.client.xvideos.l.net.graphQl.AlbumListFilter
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumFilterDisplay
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumListAlbumType
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumListContentType
import com.composeunstyled.Disclosure
import com.composeunstyled.DisclosureHeading
import com.composeunstyled.DisclosurePanel
import com.composeunstyled.rememberDisclosureState

private val style = TextStyle(
    color = ThemeL.textColor,
    fontWeight = FontWeight.Bold,
    fontFamily = ThemeL.fontFamilyKarla,
    fontSize = 18.sp
)


@Composable
private fun aLayout( contentDisclosureHeading: String, contentDisclosurePanel: @Composable () -> Unit ) {

    val state = rememberDisclosureState()

    Disclosure(state = state) {
        DisclosureHeading(backgroundColor = Color.Transparent) {
            val degrees by animateFloatAsState(if (state.expanded) -0f else -90f, tween())

            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon( imageVector = Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.rotate(degrees).size(32.dp), tint = ThemeL.textColor )
                Text(contentDisclosureHeading, style = style)
            }
        }
        DisclosurePanel(
            enter = expandVertically( spring( stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntSize.VisibilityThreshold ) ),
            exit = shrinkVertically()
        ) {
            contentDisclosurePanel.invoke()
        }
    }

}


@Composable
fun AlbumListFilter(filter: AlbumListFilter, onFilterApply: (AlbumListFilter) -> Unit) {
    Column(modifier = Modifier.background(ThemeL.greyBackground)) {

        Spacer(Modifier.height(48.dp))

        AlbumFilterDisplay(filter.display, onRequestApply = { onFilterApply(filter.copy(display = it)) })
        AlbumListAlbumType {
            val type = when (it) {
                0 -> AlbumType.All
                1 -> AlbumType.Manga
                2 -> AlbumType.Pictures
                else -> AlbumType.All
            }
            onFilterApply(filter.copy(album_type = type))
        }

        AlbumListContentType()
    }
}