package com.client.xvideos.redgifs.ui.niche

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.model.Niche
import com.client.xvideos.redgifs.model.NichesInfo
import com.client.xvideos.redgifs.model.NichesResponse
import com.client.xvideos.redgifs.model.Order
import com.client.xvideos.redgifs.model.TopCreator
import com.client.xvideos.redgifs.model.TopCreatorsResponse
import com.client.xvideos.redgifs.ui.niche.atom.NichePreview
import com.client.xvideos.redgifs.ui.niche.atom.NicheProfileContent
import com.client.xvideos.redgifs.ui.niche.atom.NicheTopCreator
import com.client.xvideos.redgifs.ui.profile.ScreenRedProfile
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.ui.theme.XvideosTheme

class R_ScreenNiche(val nicheName: String = "pumped-pussy") : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = getScreenModel<ScreenNicheSM, ScreenNicheSM.Factory> { factory -> factory.create(nicheName) }
        val columnSelect = Settings.r_current_count_niches.field.collectAsStateWithLifecycle().value
        val sort = vm.lazyHost.sortType.collectAsStateWithLifecycle().value
        val savedRed = vm.hostDI.savedRed
        val followedList = savedRed.niches.list
        val isFollowed = followedList.any { it.id == vm.niche.id }

        LaunchedEffect(columnSelect) {
            vm.lazyHost.columns = columnSelect
        }

        ScreenNicheContent(
            niche = vm.niche,
            relatedNiches = { vm.related },
            topCreators = { vm.topCreator },
            lazyHost = vm.lazyHost,
            currentSort = sort,
            onSortChange = { vm.lazyHost.changeSortType(it) },
            onNicheClick = { id -> navigator.push(R_ScreenNiche(id)) },
            onCreatorClick = { username -> navigator.push(ScreenRedProfile(username)) },
            onUpClick = { vm.lazyHost.gotoUp() },
            isFollowed = isFollowed,
            onFollowClick = {
                val nicheInfo = NichesInfo(
                    id = vm.niche.id,
                    name = vm.niche.name,
                    subscribers = vm.niche.subscribers,
                    gifs = vm.niche.gifs,
                    thumbnail = vm.niche.thumbnail,
                )
                if (isFollowed) savedRed.niches.remove(nicheInfo)
                else savedRed.niches.add(nicheInfo)
            }
        )
    }
}

@Composable
fun ScreenNicheContent(
    niche: NichesInfo,
    relatedNiches: () -> NichesResponse,
    topCreators: () -> TopCreatorsResponse,
    lazyHost: LazyRow123Host,
    currentSort: Order,
    onSortChange: (Order) -> Unit,
    onNicheClick: (String) -> Unit,
    onCreatorClick: (String) -> Unit,
    onUpClick: () -> Unit,
    isFollowed: Boolean,
    onFollowClick: () -> Unit
) {
    StatelessScreenNicheContent(
        niche = niche,
        currentSort = currentSort,
        onSortChange = onSortChange,
        columns = lazyHost.columns,
        onUpClick = onUpClick,
        content = { padding ->
            Box(
                modifier = Modifier
                    .background(Color(0xFF303030))
                    .padding(bottom = padding.calculateBottomPadding())
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                LazyRow123(
                    host = lazyHost,
                    modifier = Modifier.fillMaxWidth(),
                    onClickOpenProfile = onCreatorClick,
                    contentBeforeList = {
                        NicheHeaderContent(
                            niche = niche,
                            relatedNiches = relatedNiches,
                            topCreators = topCreators,
                            onNicheClick = onNicheClick,
                            onCreatorClick = onCreatorClick,
                            isFollowed = isFollowed,
                            onFollowClick = onFollowClick
                        )
                    }
                )
            }
        }
    )
}

@Composable
private fun StatelessScreenNicheContent(
    niche: NichesInfo,
    currentSort: Order,
    onSortChange: (Order) -> Unit,
    columns: Int,
    onUpClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            NicheBottomBar(
                niche = niche,
                currentSort = currentSort,
                onSortChange = onSortChange,
                columns = columns,
                onUpClick = onUpClick
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0F0F0F)
    ) { padding ->
        content(padding)
    }
}

@Composable
private fun NicheHeaderContent(
    niche: NichesInfo,
    relatedNiches: () -> NichesResponse,
    topCreators: () -> TopCreatorsResponse,
    onNicheClick: (String) -> Unit,
    onCreatorClick: (String) -> Unit,
    isFollowed: Boolean,
    onFollowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .displayCutoutPadding()
            .systemBarsPadding()
            .fillMaxWidth()
            .background(Color(0xFF303030))
    ) {

        NicheProfileContent(
            niche = { niche },
            isFollowed = isFollowed,
            onFollowClick = onFollowClick
        )

        if (relatedNiches().niches.isNotEmpty()) {
            Text(
                "Related Niches",
                color = Color.White,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                fontFamily = ThemeRed.fontFamilyDMsanss
            )
            LazyRow(modifier = Modifier.padding(top = 4.dp).fillMaxWidth()) {
                items(relatedNiches().niches) { item ->
                    NichePreview({item}, onClick = { onNicheClick(item.id) })
                }
            }
        }

        if (topCreators().creators.isNotEmpty()) {
            Text(
                "✨ Top Creators in ${niche.name}",
                color = Color.White,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                fontFamily = ThemeRed.fontFamilyDMsanss
            )
            LazyRow(modifier = Modifier.padding(vertical = 4.dp)) {
                items(topCreators().creators) { creator ->
                    NicheTopCreator(creator, onClick = { onCreatorClick(creator.username) })
                }
            }
        }

        Spacer(Modifier.height(2.dp))
    }
}

@Preview
@Composable
private fun ScreenNicheContentPreview() {
    XvideosTheme {
        StatelessScreenNicheContent(
            niche = sampleNicheInfo,
            currentSort = Order.LATEST,
            onSortChange = {},
            columns = 2,
            onUpClick = {},
            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(bottom = padding.calculateBottomPadding())
                        .fillMaxSize()
                ) {
                    NicheHeaderContent(
                        niche = sampleNicheInfo,
                        relatedNiches = { sampleNichesResponse },
                        topCreators = { sampleTopCreatorsResponse },
                        onNicheClick = {},
                        onCreatorClick = {},
                        isFollowed = false,
                        onFollowClick = {}
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("LazyRow123 Placeholder", color = Color.Gray)
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun NicheHeaderContentPreview() {
    XvideosTheme {
        NicheHeaderContent(
            niche = sampleNicheInfo,
            relatedNiches = { sampleNichesResponse },
            topCreators = { sampleTopCreatorsResponse },
            onNicheClick = {},
            onCreatorClick = {},
            isFollowed = true,
            onFollowClick = {}
        )
    }
}

val sampleNicheInfo = NichesInfo(
    cover = "https://userpic.redgifs.com/niches/thumbnails/female-backs-dee7838f.jpg",
    description = "A collection of beautiful female backs.",
    gifs = 245,
    id = "female-backs",
    name = "Female Backs",
    owner = "owner",
    subscribers = 914,
    thumbnail = "https://userpic.redgifs.com/niches/thumbnails/female-backs-dee7838f.jpg",
    rules = "rules"
)

val sampleNichesResponse = NichesResponse(
    niches = listOf(
        Niche(
            id = "amateur-milf",
            name = "Amateur MILF",
            gifs = 1234,
            subscribers = 5678,
            thumbnail = "https://userpic.redgifs.com/niches/thumbnails/amateur-milf-thumbnail.jpg",
            previews = listOf()
        ),
        Niche(
            id = "teen-petite",
            name = "Teen Petite",
            gifs = 4321,
            subscribers = 8765,
            thumbnail = "https://userpic.redgifs.com/niches/thumbnails/teen-petite-thumbnail.jpg",
            previews = listOf()
        )
    ),
    page = 1,
    pages = 1,
    total = 2
)

val sampleTopCreatorsResponse = TopCreatorsResponse(
    creators = listOf(
        TopCreator(
            creationtime = 1672531200,
            description = "I make videos.",
            followers = 100,
            gifs = 10,
            name = "Creator 1",
            profileImageUrl = "https://userpic.redgifs.com/users/creator1.jpg",
            username = "creator1",
            verified = true,
            studio = false,
            views = 1000
        ),
        TopCreator(
            creationtime = 1672531200,
            description = "I also make videos.",
            followers = 200,
            gifs = 20,
            name = "Creator 2",
            profileImageUrl = "https://userpic.redgifs.com/users/creator2.jpg",
            username = "creator2",
            verified = false,
            studio = true,
            views = 2000
        )
    )
)
