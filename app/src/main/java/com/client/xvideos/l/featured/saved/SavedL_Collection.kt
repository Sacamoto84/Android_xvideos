package com.client.xvideos.l.featured.saved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.lDownloadUrl
import com.client.xvideos.l.net.Luscious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * Тонкий holder состояния для раздела «Collection» в L.
 *
 * Файловая система выведена в [LCollectionFs.lReadCollections] / [lReadCollectionItems] /
 * [lFindCollectionItemFolder], сетевая часть — в [lPersistPicsDetailsToFolder].
 * Этот класс держит только public API + Compose state и оркестрирует вызовы.
 */
class SavedL_Collection(
    private val scope: CoroutineScope,
    private val luscious: Luscious
) {

    val listUrl = mutableStateListOf<PicsDetails>()
    val collectionList = mutableStateListOf<LCollectionEntity>()

    private val progress = LDownloadProgress(scope)
    val percentDownload: StateFlow<Float> = progress.percentDownload

    var currentCollectionName by mutableStateOf<String?>(null)

    //----- Dialogs -----
    var visibleDialog by mutableStateOf(false)
    var visibleDialogCreateNew by mutableStateOf(false)
    var collectionItemGifInfo by mutableStateOf<PicsDetails?>(null)
    //-------------------

    init {
        refreshCollectionList()
    }

    /* ---------- Список коллекций ---------- */

    fun refreshCollectionList() {
        try {
            Timber.i("SavedL_Collection refreshCollectionList()")
            val items = lReadCollections(File(AppPath.l_collection))
            collectionList.clear()
            collectionList.addAll(items)
            Timber.i("SavedL_Collection refreshCollectionList() collections:${collectionList.size}")
        } catch (e: Exception) {
            Timber.e(e, "SavedL_Collection refreshCollectionList() Ошибка получения списка коллекций")
            SnackBar.error("Ошибка получения списка коллекций")
        }
    }

    fun createCollection(collectionName: String) {
        Timber.i("SavedL_Collection createCollection() collectionName:$collectionName")
        val collectionRoot = File(AppPath.l_collection, collectionName)
        if (collectionRoot.exists()) {
            SnackBar.error("Коллекция уже существует")
            return
        }
        collectionRoot.mkdirs()
        SnackBar.success("Коллекция $collectionName создана")
        refreshCollectionList()
    }

    fun deleteCollection(collectionName: String) {
        Timber.i("SavedL_Collection deleteCollection() collectionName:$collectionName")
        val collectionRoot = File(AppPath.l_collection, collectionName)
        if (collectionRoot.deleteRecursively()) {
            if (currentCollectionName == collectionName) {
                currentCollectionName = null
                listUrl.clear()
            }
            SnackBar.success("Коллекция $collectionName удалена")
            refreshCollectionList()
        } else {
            SnackBar.error("Ошибка удаления коллекции $collectionName")
        }
    }

    fun renameCollection(oldName: String, newName: String): Boolean {
        Timber.i("SavedL_Collection renameCollection() oldName:$oldName newName:$newName")
        val trimmedNewName = newName.trim()
        if (trimmedNewName.isBlank()) {
            SnackBar.error("Название коллекции не может быть пустым")
            return false
        }
        if (oldName == trimmedNewName) {
            return true
        }

        val oldRoot = File(AppPath.l_collection, oldName)
        val newRoot = File(AppPath.l_collection, trimmedNewName)
        if (!oldRoot.exists()) {
            SnackBar.error("Коллекция не найдена")
            return false
        }
        if (newRoot.exists()) {
            SnackBar.error("Коллекция уже существует")
            return false
        }

        val renamed = oldRoot.renameTo(newRoot)
        if (renamed) {
            if (currentCollectionName == oldName) {
                currentCollectionName = trimmedNewName
                refresh()
            }
            SnackBar.success("Коллекция переименована")
            refreshCollectionList()
        } else {
            SnackBar.error("Ошибка переименования коллекции")
        }
        return renamed
    }

    /* ---------- Текущая коллекция ---------- */

    fun setCollection(collectionName: String) {
        currentCollectionName = collectionName
        refresh()
    }

    fun refresh() {
        val collectionName = currentCollectionName ?: return
        try {
            Timber.i("SavedL_Collection refresh() collection:$collectionName")
            val collectionRoot = File(AppPath.l_collection, collectionName)
            val items = lReadCollectionItems(collectionRoot)
            listUrl.clear()
            listUrl.addAll(items)
            Timber.i("SavedL_Collection refresh() files:${listUrl.size}")
        } catch (e: Exception) {
            Timber.e(e, "SavedL_Collection refresh() Ошибка получения списка коллекции")
            SnackBar.error("Ошибка получения списка коллекции")
        }
    }

    /* ---------- Элементы ---------- */

    fun add(item: PicsDetails, collectionName: String) {
        Timber.i("SavedL_Collection add() item:${item.url_to_original} collection:$collectionName")

        scope.launch(Dispatchers.IO) {
            val result = lPersistPicsDetailsToFolder(
                item = item,
                root = File(AppPath.l_collection, collectionName),
                luscious = luscious,
                progress = progress
            )
            withContext(Dispatchers.Main) {
                result
                    .onSuccess {
                        SnackBar.success("Added to collection")
                        refreshCollectionList()
                        if (currentCollectionName == collectionName) {
                            refresh()
                        }
                    }
                    .onFailure {
                        Timber.e(it, "SavedL_Collection add() error")
                        SnackBar.error("Ошибка добавления в коллекцию")
                    }
            }
        }
    }

    fun remove(item: PicsDetails, collectionName: String) {
        remove(
            identifiers = listOfNotNull(
                item.url_to_original,
                item.url_to_video,
                item.lDownloadUrl()
            ) + (item.thumbnails?.mapNotNull { it.url } ?: emptyList()),
            collectionName = collectionName
        )
    }

    fun remove(url: String, collectionName: String) {
        remove(listOf(url), collectionName)
    }

    private fun remove(identifiers: List<String>, collectionName: String) {
        Timber.i("SavedL_Collection remove() identifiers:$identifiers collection:$collectionName")
        val collectionRoot = File(AppPath.l_collection, collectionName)
        val folder = lFindCollectionItemFolder(collectionRoot, identifiers)
        val file = identifiers.firstOrNull()?.lToFilePath()?.let { File(it) }

        val removed = when {
            folder != null -> folder.deleteRecursively()
            file != null && lIsInside(collectionRoot, file) && file.exists() -> file.delete()
            else -> false
        }

        if (removed) {
            SnackBar.info("Removed from collection")
            refreshCollectionList()
        } else {
            SnackBar.error("Файл не найден")
        }
        if (currentCollectionName == collectionName) {
            refresh()
        }
    }
}
