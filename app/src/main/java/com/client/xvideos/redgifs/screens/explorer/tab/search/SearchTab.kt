package com.client.xvideos.redgifs.screens.explorer.tab.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.redgifs.network.api.RedApi_Search
import com.redgifs.model.search.SearchItemCreatorsResponse
import com.redgifs.model.search.SearchItemNichesResponse
import com.redgifs.model.search.SearchItemTagsResponse
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.google.gson.annotations.Since
import com.redgifs.network.api.RedApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

object SearchTab : Screen {

    private fun readResolve(): Any = SearchTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint(
        "UnusedMaterialScaffoldPaddingParameter",
        "UnusedMaterial3ScaffoldPaddingParameter"
    )
    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerSearchSM = getScreenModel()

        val navigator = LocalNavigator.currentOrThrow

        //val state = vm.lazyHost.stateColumn

        //val listNiche = vm.lazyHost.pager.collectAsLazyPagingItems() as LazyPagingItems<Niche>

        val searchText = vm.searchText.collectAsStateWithLifecycle().value

        Scaffold(

            modifier = Modifier,//.imePadding(),          // <-- волшебство здесь.verticalScroll(rememberScrollState()), // чтобы всё можно было проскроллить,
            bottomBar = {

            OutlinedTextField(searchText, onValueChange = { vm.searchText.value = it })


        }) { paddingValues ->


            LazyColumn(modifier = Modifier.padding(paddingValues)) {

                items(vm.creatorsList) {

                    Row(modifier = Modifier.border(1.dp, Color.White)) {
                        if (it.image != null) {
                            UrlImage(it.image!!, modifier = Modifier
                                .clip(CircleShape)
                                .size(64.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(64.dp)
                                    .background(Color.DarkGray), contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PersonOutline,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Column {
                            Text(it.name, color = Color.White)
                            Text("Followers:" + it.followers.toString(), color = Color.White)
                        }
                    }

                }


            }
        }

    }

}


class ScreenRedExplorerSearchSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val redApi: RedApi
) : ScreenModel {

    val searchText = MutableStateFlow<String>("Ana")

    val creatorsList = mutableStateListOf<SearchItemCreatorsResponse>()
    val nichesList = mutableStateListOf<SearchItemNichesResponse>()
    val tagsList = mutableStateListOf<SearchItemTagsResponse>()

    init {


        screenModelScope.launch {

            searchText.collect { text ->

                SnackBarEvent.info(text)

                if (text == "") {
                    creatorsList.clear()
                    return@collect
                }

                val creator = redApi.search.searchCreatorsShort(text)

                creatorsList.clear()
                creatorsList.addAll(creator.items)

            }
//            val niches = RedGifs.searchNiches("Ana")
//            nichesList.clear()
//            nichesList.addAll(niches)

//            val tags = RedGifs.searchTags("Ana")
//            tagsList.clear()
//            tagsList.addAll(tags)
        }

    }



}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedExplorerSearch {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedExplorerSearchSM::class)
    abstract fun bindScreenRedExplorerSearchSreenModel(hiltListScreenModel: ScreenRedExplorerSearchSM): ScreenModel
}