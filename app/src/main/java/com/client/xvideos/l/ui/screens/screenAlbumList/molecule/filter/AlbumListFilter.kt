package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.net.graphQl.AlbumListFilter
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumFilterDisplay
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
fun AlbumListFilter(filter: AlbumListFilter, onFilterApply: (AlbumListFilter) -> Unit) {
    Column() {

        Spacer(Modifier.height(48.dp))

        val state = rememberDisclosureState()

        Disclosure(state = state) {
            DisclosureHeading {
                val degrees by animateFloatAsState(if (state.expanded) -180f else 0f, tween())


                Text("Display", style = style)

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(degrees), tint = ThemeL.textColor
                )

            }
            DisclosurePanel(
                enter = expandVertically(
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = IntSize.VisibilityThreshold
                    )
                ),
                exit = shrinkVertically()
            ) {
                AlbumFilterDisplay("date_newest", onRequestApply = {
                    onFilterApply(filter.copy(display = it))
                })
            }
        }


    }
}