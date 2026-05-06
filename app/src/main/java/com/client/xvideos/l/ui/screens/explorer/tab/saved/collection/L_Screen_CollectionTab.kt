package com.client.xvideos.l.ui.screens.explorer.tab.saved.collection

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.common.collectionDB.ui.CollectionGridItem
import com.client.xvideos.common.collectionDB.ui.CollectionsGrid
import com.client.xvideos.common.collectionDB.ui.CollectionsGridStyle
import com.client.xvideos.l.featured.saved.LCollectionEntity
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.theme.ThemeL
import com.composeunstyled.Text
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

object L_Screen_CollectionTab : Screen {

    private fun readResolve(): Any = L_Screen_CollectionTab

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val vm = getScreenModel<ScreenSavedCollectionSM>()

        val savedL = vm.savedL

        val selectedCollection = savedL.collection.currentCollectionName

        BackHandler {
            Timber.i("iii BackHandler SavedCollectionTab")
            savedL.collection.currentCollectionName = null
        }

        var itemPendingAction by remember { mutableStateOf<String?>(null) }
        var itemPendingRename by remember { mutableStateOf<String?>(null) }
        var itemPendingDelete by remember { mutableStateOf<String?>(null) }
        var renameValue by remember { mutableStateOf("") }

        itemPendingAction?.let { pending ->
            AlertDialog(
                onDismissRequest = { itemPendingAction = null },
                title = {
                    Text(
                        "Действие с коллекцией",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ThemeL.textColor
                    )
                },
                text = {
                    Column {
                        Text(pending, fontSize = 16.sp, color = ThemeL.grey2)
                        DropdownMenuItem(
                            text = { Text("Переименовать", style = ThemeL.Type.menuItem) },
                            onClick = {
                                renameValue = pending
                                itemPendingRename = pending
                                itemPendingAction = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Удалить коллекцию", style = ThemeL.Type.menuItem.copy(color = ThemeL.red)) },
                            onClick = {
                                itemPendingDelete = pending
                                itemPendingAction = null
                            }
                        )
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = { itemPendingAction = null }
                    ) { Text("Отмена", style = ThemeL.Type.button.copy(color = ThemeL.primaryColor)) }
                },
                containerColor = ThemeL.grey5,
                titleContentColor = ThemeL.textColor,
                textContentColor = ThemeL.textColor
            )
        }

        itemPendingRename?.let { pending ->
            AlertDialog(
                onDismissRequest = { itemPendingRename = null },
                title = {
                    Text(
                        "Переименовать коллекцию",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ThemeL.textColor
                    )
                },
                text = {
                    OutlinedTextField(
                        value = renameValue,
                        onValueChange = { renameValue = it },
                        singleLine = true,
                        textStyle = ThemeL.Type.body
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (savedL.collection.renameCollection(pending, renameValue)) {
                                itemPendingRename = null
                            }
                        }
                    ) { Text("Сохранить", style = ThemeL.Type.button.copy(color = ThemeL.primaryColor)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { itemPendingRename = null }
                    ) { Text("Отмена", style = ThemeL.Type.button.copy(color = ThemeL.primaryColor)) }
                },
                containerColor = ThemeL.grey5,
                titleContentColor = ThemeL.textColor,
                textContentColor = ThemeL.textColor
            )
        }

        /* ---------- Диалог подтверждения ---------- */
        itemPendingDelete?.let { pending ->
            AlertDialog(

                onDismissRequest = { itemPendingDelete = null },

                title = {
                    Text(
                        "Удалить коллекцию?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ThemeL.textColor
                    )
                },

                text = {
                    Text(buildAnnotatedString {
                        append("Удалить «")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(pending) }
                        append("» из коллекции")
                    }, fontSize = 16.sp, color = ThemeL.textColor)
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            savedL.collection.deleteCollection(pending)
                            itemPendingDelete = null
                        }
                    ) { Text("Удалить", style = ThemeL.Type.button.copy(color = ThemeL.red)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { itemPendingDelete = null }
                    ) { Text("Отмена", style = ThemeL.Type.button.copy(color = ThemeL.primaryColor)) }
                },

                containerColor = ThemeL.grey5,
                titleContentColor = ThemeL.textColor,
                textContentColor = ThemeL.textColor
            )
        }

        L_SavedCollectionTabContent(
            selectedCollection = selectedCollection,
            collectionList = savedL.collection.collectionList,
            gridState = vm.gridState,
            onCollectionClick = { savedL.collection.setCollection(it) },
            onCollectionLongClick = { itemPendingAction = it },
            onCreateNewCollectionClick = { savedL.collection.visibleDialogCreateNew = true },
            navigationContent = {
                if (selectedCollection != null) {
                    L_CollectionNameContent(
                        collectionName = selectedCollection,
                        savedL = savedL
                    )
                }
            }
        )
    }
}

@Composable
fun L_SavedCollectionTabContent(
    selectedCollection: String?,
    collectionList: List<LCollectionEntity>,
    gridState: LazyGridState,
    onCollectionClick: (String) -> Unit,
    onCollectionLongClick: (String) -> Unit,
    onCreateNewCollectionClick: () -> Unit,
    navigationContent: @Composable () -> Unit
) {
    CollectionsGrid(
        selectedCollection = selectedCollection,
        collections = collectionList.map {
            CollectionGridItem(
                name = it.collection,
                previewUrl = it.previewUrl,
                itemsCount = it.itemsCount
            )
        },
        gridState = gridState,
        style = CollectionsGridStyle(
            backgroundColor = ThemeL.greyBackground,
            titleColor = ThemeL.primaryColor,
            titleFontFamily = ThemeL.fontFamilyPopinsRegular,
            itemNameColor = androidx.compose.ui.graphics.Color.White,
            itemSecondaryColor = androidx.compose.ui.graphics.Color.LightGray,
            itemFontFamily = ThemeL.fontFamilyDMsanss,
            addButtonBackground = ThemeL.primaryColor
        ),
        onCollectionClick = onCollectionClick,
        onCollectionLongClick = onCollectionLongClick,
        onCreateNewCollectionClick = onCreateNewCollectionClick,
        navigationContent = navigationContent
    )
}


class ScreenSavedCollectionSM @Inject constructor(
    val savedL: SavedL,
) : ScreenModel {

    val gridState = LazyGridState()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLSavedCollection {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedCollectionSM::class)
    abstract fun bindScreenLSavedCollectionScreenModel(hiltListScreenModel: ScreenSavedCollectionSM): ScreenModel
}
