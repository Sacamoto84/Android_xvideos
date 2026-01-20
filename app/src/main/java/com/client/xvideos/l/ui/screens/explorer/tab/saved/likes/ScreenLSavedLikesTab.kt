package com.client.xvideos.l.ui.screens.explorer.tab.saved.likes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuType
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetails
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetailsHost
import com.client.xvideos.redgifs.ui.explorer.tab.gifs.ColumnSelect
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

object ScreenLSavedLikesTab : Screen {

    private fun readResolve(): Any = ScreenLSavedLikesTab

    override val key: ScreenKey = uniqueScreenKey

    @Transient
    val columnSelect = ColumnSelect(Settings.current_count_likesTab)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedLLikesSM = getScreenModel()

        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf("All", "Image", "Gif")

        Scaffold(modifier = Modifier.fillMaxSize()) {
            LazyRowPictureDetails(
                vm.host,
                expandMenu = ExpandMenuType.LIKES,
                tag = "lLikes",
                itemBefore = {

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.displayCutoutPadding()) {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                onClick = {
                                    selectedIndex = index
                                    vm.filterSelect(selectedIndex)
                                },
                                selected = index == selectedIndex,
                                label = { Text(label) },

                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor =  Color(0xFF938F99)// ThemeL.b0
                                )
                            )
                        }
                    }


                }
            )
        }

    }

}


enum class AllImagGif {
    ALL, IMAGE, GIF
}

class ScreenSavedLLikesSM @Inject constructor(
    val savedL: SavedL
) : ScreenModel {

    val host = LazyRowPictureDetailsHost("likes")

    /**
     * Выбор типа отображаемого контента
     */
    private var selectorFilter = AllImagGif.ALL

    val original = savedL.likes.listUrl

    init {
        filterSelect(selectorFilter)
    }

    fun filterSelect(item: AllImagGif) {
        when (item) {
            AllImagGif.ALL -> {
                selectAll()
            }

            AllImagGif.IMAGE -> {
                selectImage()
            }

            AllImagGif.GIF -> {
                selectGif()
            }
        }
    }

    fun filterSelect(index: Int) {
        when (index) {
            0 -> {
                selectorFilter = AllImagGif.ALL
                selectAll()
            }
            1 -> {
                selectorFilter = AllImagGif.IMAGE
                selectImage()
            }
            2 -> {
                selectorFilter = AllImagGif.GIF
                selectGif()
            }
        }
    }

    fun delete(item: PicsDetails) {
        savedL.likes.remove(item.url_to_original!!)
    }

    fun selectGif() {
        if (host.filteredPic.isNotEmpty()) host.filteredPic.clear()
        host.filteredPic.addAll(original.filter { it.is_animated }.toList())
        host.filteredPic
    }

    fun selectImage() {
        if (host.filteredPic.isNotEmpty()) host.filteredPic.clear()
        host.filteredPic.addAll(original.filter { !it.is_animated })
    }

    fun selectAll() {
        if (host.filteredPic.isNotEmpty()) host.filteredPic.clear()
        host.filteredPic.addAll(original)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLSavedLikes {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedLLikesSM::class)
    abstract fun bindScreenLSavedLikesScreenModel(hiltListScreenModel: ScreenSavedLLikesSM): ScreenModel
}
