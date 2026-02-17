package com.client.xvideos.redgifs.ui.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.common.connectivityObserver.ConnectivityObserver
import com.client.xvideos.redgifs.ui.profile.ScreenRedProfile
import com.client.xvideos.redgifs.ui.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.ui.profile.rememberVisibleRangePercentIgnoringFirstNForLazyColumn
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.ui.ui.lazyrow123.TypePager
import com.composeunstyled.Text
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.model.Order
import com.client.xvideos.redgifs.model.UserInfo
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.ui.theme.XvideosTheme
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

object R_SavedCreatorsTab : Screen {

    private fun readResolve(): Any = R_SavedCreatorsTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = getScreenModel<ScreenSavedCreatorSM>()
        val state = rememberLazyListState()
        val savedRed = vm.hostDI.savedRed

        var itemPendingDelete by remember { mutableStateOf<UserInfo?>(null) }

        val onCreatorClick = remember(navigator) {
            { username: String -> navigator.push(ScreenRedProfile(username)) }
        }
        val onDeleteRequest = remember {
            { user: UserInfo -> itemPendingDelete = user }
        }

        DeleteCreatorDialog(
            item = itemPendingDelete,
            onDismiss = { itemPendingDelete = null },
            onConfirm = { pending ->
                savedRed.creators.remove(pending.username)
                itemPendingDelete = null
            }
        )

        Scaffold(topBar = {
            Text(
                ">Авторы",
                modifier = Modifier.padding(start = 8.dp),
                color = ThemeRed.colorYellow,
                fontSize = 18.sp,
                fontFamily = ThemeRed.fontFamilyPopinsRegular
            )
        }) { padding ->
            Box(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(savedRed.creators.list, key = { it.username }) { item ->
                        CreatorListItem(
                            item = item,
                            onClick = onCreatorClick,
                            onDelete = onDeleteRequest
                        )
                    }
                }

                CreatorsScrollbar(state)
            }
        }
    }
}

@Composable
private fun BoxScope.CreatorsScrollbar(state: LazyListState) {
    val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyColumn(
        gridState = state, itemsToIgnore = 0
    )
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .align(Alignment.CenterEnd)
            .width(2.dp)
    ) {
        VerticalScrollbar(scrollPercent)
    }
}

@Composable
private fun CreatorListItem(
    item: UserInfo,
    onClick: (String) -> Unit,
    onDelete: (UserInfo) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(ThemeRed.colorTabLevel3)
            .clickable { onClick(item.username) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (item.profileImageUrl != null) {
            UrlImage(
                item.profileImageUrl,
                modifier = Modifier.size(96.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            item.name,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = ThemeRed.fontFamilyDMsanss,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .width(96.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                .background(Color.Black)
                .clickable { onDelete(item) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Выйти",
                fontFamily = ThemeRed.fontFamilyDMsanss,
                fontSize = 18.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun DeleteCreatorDialog(
    item: UserInfo?,
    onDismiss: () -> Unit,
    onConfirm: (UserInfo) -> Unit
) {
    item?.let { pending ->
        AlertDialog(
            icon = {
                pending.profileImageUrl?.let {
                    UrlImage(it, modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(96.dp))
                }
            },
            onDismissRequest = onDismiss,
            title = { Text("Удалить автора?", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            text = {
                Text(buildAnnotatedString {
                    append("Удалить «")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(pending.name) }
                    append("» из сохранённых?")
                }, fontSize = 16.sp)
            },
            confirmButton = {
                TextButton(onClick = { onConfirm(pending) }) {
                    Text("Удалить", fontSize = 16.sp, color = Color(0xFF6552A5))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Отмена", fontSize = 16.sp, color = Color(0xFF6552A5))
                }
            },
            containerColor = Color(0xFFEBE6EE)
        )
    }
}

class ScreenSavedCreatorSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    hostDIin: javax.inject.Provider<HostDI>
) : ScreenModel {

    val hostDI = hostDIin.get()

    val gridState = LazyGridState()

    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.SAVED_COLLECTION,
        extraString = "",
        startOrder = Order.LATEST,
        hostDI = hostDI
    )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedCreator {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedCreatorSM::class)
    abstract fun bindScreenRedSavedCreatorScreenModel(hiltListScreenModel: ScreenSavedCreatorSM): ScreenModel
}

@Preview
@Composable
private fun CreatorListItemPreview() {
    val sampleUser = UserInfo(
        name = "Sample Creator",
        username = "samplecreator",
        profileImageUrl = "https://via.placeholder.com/96",
        url = "https://example.com/samplecreator"
    )
    CreatorListItem(
        item = sampleUser,
        onClick = {},
        onDelete = {}
    )
}

@Preview
@Composable
private fun DeleteCreatorDialogPreview() {
    XvideosTheme {
        val sampleUser = UserInfo(
            name = "Sample Creator",
            username = "samplecreator",
            profileImageUrl = "https://via.placeholder.com/96",
            url = "https://example.com/samplecreator"
        )
        DeleteCreatorDialog(
            item = sampleUser,
            onDismiss = {},
            onConfirm = {}
        )
    }
}
