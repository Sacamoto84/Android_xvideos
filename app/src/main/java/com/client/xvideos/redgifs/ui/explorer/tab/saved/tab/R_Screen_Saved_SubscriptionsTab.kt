package com.client.xvideos.redgifs.ui.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.UsersRed
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.common.saved.SelectedCreator
import com.client.xvideos.redgifs.ui.profile.ScreenRedProfile
import com.client.xvideos.redgifs.ui.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.ui.profile.rememberVisibleRangePercentIgnoringFirstNForGrid
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.ui.ui.lazyrow123.model.TypePager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

object R_Screen_Saved_SubscriptionsTab : Screen {

    private fun readResolve(): Any = R_Screen_Saved_SubscriptionsTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedSubscriptionsSM = getScreenModel()

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForGrid(
            gridState = vm.likedHost.state,
            itemsToIgnore = 0,
            numberOfColumns = 3
        )

        SubscriptionsTabContent(
            host = vm.likedHost,
            scrollPercent = scrollPercent,
            listCreatorSelectedCreator = vm.hostDI.savedRed.subscriptions.selectedListCreator,
            onOpenProfile = { navigator.push(ScreenRedProfile(it)) }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SubscriptionsTabContent(
    host: LazyRow123Host,
    scrollPercent: Pair<Float, Float>,
    listCreatorSelectedCreator: SnapshotStateList<SelectedCreator>,
    onOpenProfile: (String) -> Unit
) {
    var selectCreator by remember { mutableStateOf<String?>(null) }

    val selectedListCreator = remember(selectCreator, listCreatorSelectedCreator.toList()) {

        if (selectCreator != null) {
            for (i in listCreatorSelectedCreator.indices) {
                val a = listCreatorSelectedCreator[i]
                if (a.name == selectCreator) {
                    a.select = a.select.not()
                }
            }
            selectCreator = null
        }

        listCreatorSelectedCreator.toMutableStateList()

    }




    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF303030)
    ) { padding ->

        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        )
        {
            LazyRow123(
                host = host,
                modifier = Modifier.fillMaxSize(),
                onClickOpenProfile = onOpenProfile,
                contentPadding = PaddingValues(0.dp),
                contentBeforeList = {
                    CreatorsHeader( listCreators = selectedListCreator, onCreatorClick = { selectCreator = it } )
                },
                isRunLike = true
            )


            //---- Скролл ----
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .width(2.dp)
            ) {
                VerticalScrollbar(scrollPercent)
            }
        }
    }


}

@Composable
fun CreatorsHeader(
    listCreators: List<SelectedCreator>,
    onCreatorClick: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        listCreators.forEach { creator ->
            CreatorChip(
                creator = creator.name,
                isSelected = creator.select,
                onClick = { onCreatorClick(creator.name) }
            )
        }
    }
}

@Composable
fun CreatorChip(
    creator: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, Color.Gray, RoundedCornerShape(50))
            .background(
                if (isSelected) Color.Gray else Color.Transparent,
                RoundedCornerShape(50)
            )
            .combinedClickable(
                onClick = onClick,
                indication = null,
                interactionSource = null,
                enabled = true,
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            val url = UsersRed.listAllUsers.find { it.username == creator }?.profileImageUrl
            if (url != null) {
                UrlImage(url = url)
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        Text(
            text = creator,
            fontSize = 16.sp,
            color = Color.White,
            fontFamily = ThemeRed.fontFamilyPopinsRegular
        )
        Spacer(Modifier.width(4.dp))
    }
}

class ScreenSavedSubscriptionsSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val hostDI: HostDI
) : ScreenModel {

    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.SUBSCRIPTIONS,
        hostDI = hostDI
    )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedSubscriptions {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedSubscriptionsSM::class)
    abstract fun bindScreenRedSavedSubscriptionsScreenModel(hiltListScreenModel: ScreenSavedSubscriptionsSM): ScreenModel
}
