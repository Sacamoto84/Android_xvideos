package com.client.xvideos.redgifs.screens.profile

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.common.block.ui.DialogBlock
import com.client.xvideos.redgifs.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.redgifs.screens.profile.atom.RedProfileCreaterInfo
import com.client.xvideos.redgifs.screens.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.screens.profile.tags.TagsBlock
import com.composeunstyled.rememberDisclosureState
import com.redgifs.model.GifsInfo
import timber.log.Timber

class ScreenRedProfile(val profileName: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenRedProfileSM, ScreenRedProfileSM.Factory> { factory ->
            factory.create(profileName)
        }

        val isLoading = vm.isLoading.collectAsState().value

        val selector = vm.selector.collectAsStateWithLifecycle().value

        val tags = vm.tags.collectAsStateWithLifecycle().value

        val tagsSelect = vm.tagsSelect.collectAsStateWithLifecycle().value

        //Расчет процентов для скролл
        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForGrid(
            gridState = vm.likedHost.state, itemsToIgnore = 3, numberOfColumns = 2
        )

        val block = vm.block

        //🟨🟨🟨🟨🟨🟨🟨🟨🎨🎨🎨🟨🟨🟨🟨🟨🟨🟨🟨
        //╭┈┈ Диалог блокировки ┈┈╮
        //│ Отмена    Блокировать │
        //╰┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈╯
        DialogBlock(
            visible = block.blockVisibleDialog,
            onDismiss = { block.blockVisibleDialog = false }) {
            val a = vm.currentTikTokGifInfo; if (a != null) {
                block.blockItem(a)
            }
        }
        //🟨🟨🟨🟨🟨🟨🟨🟨⬆️⬆️⬆️⬆️⬆️❗

        Scaffold(containerColor = ThemeRed.colorCommonBackground2) {
            //Box(Modifier.padding(bottom = it.calculateBottomPadding())) {

                Box(modifier = Modifier.fillMaxSize()) {

                    LazyRow123(
                        host = vm.likedHost,
                        modifier = Modifier.fillMaxSize(),
                        contentBeforeList = {
                            Column(modifier = Modifier.fillMaxWidth()) {

                                if (vm.creator != null) {
                                    RedProfileCreaterInfo(vm.creator!!, vm.savedRed)
                                }

                                if ((vm.creator != null) && (tags.isNotEmpty())) {
                                    TagsBlock(tags.toList(), tagsSelect.toList(), {
                                             vm.toggleSelectTag(it)
                                    })
                                    Spacer(modifier = Modifier.height(4.dp))
                                }


                            }
                        },
                        onAppendLoaded = { pager ->
                            Timber.tag("Paging").d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Произошла загрузка следующей страницы!")
                            pager.itemSnapshotList.let { it1 ->
                                it1.items.forEach { it2 ->
                                    val t = it2.tags
                                    vm.tagsAdd(t)
                                }
                            }
                        },
                    )

                    //Индикатор загрузки
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(56.dp),
                                strokeWidth = 8.dp
                            )
                        }
                    }

                    //Text("      " + visibleItems.toString(), color = Color.White)

                    //---- Скролл ----
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd)
                            .width(2.dp)
                    ) { VerticalScrollbar(scrollPercent) }

                    //VerticalScrollbar1(scrollPercent)

                }


            //}


        }
    }

}

//---- Скролл ----
@Composable
fun VerticalScrollbar1(scrollPercent:  Pair<Float, Float>) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            //.align(Alignment.CenterEnd)
            .width(2.dp)
    ) { VerticalScrollbar(scrollPercent) }
}

