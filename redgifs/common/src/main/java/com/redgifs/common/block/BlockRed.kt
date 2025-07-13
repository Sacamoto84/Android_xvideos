package com.redgifs.common.block

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.withTransaction
import com.client.common.di.ApplicationScope
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.db.AppRedGifsDatabase
import com.redgifs.db.dao.BlockDao
import com.redgifs.db.dao.GifsInfoDao
import com.redgifs.db.entity.BlockEntity
import com.redgifs.db.entity.toDomain
import com.redgifs.db.entity.toEntity
import com.redgifs.model.GifsInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockRed @Inject constructor(
    private val blockDao: BlockDao,
    private val infoDao: GifsInfoDao,
    private val db: AppRedGifsDatabase,
    val snackBarEvent: SnackBarEvent,
    @ApplicationScope private val scope: CoroutineScope
) {

    //══════════ Блокировка ═══════════════════════════════════════════╦══════════════════════════════════════════════╗
    var blockVisibleDialog by mutableStateOf(false)                  //║ Показ диалога на добавление в блок лист      ║
    //var blockList = MutableStateFlow<List<GifsInfo>>(emptyList())  //║                                              ║

    private val _blockList = MutableStateFlow<List<GifsInfo>>(emptyList())
    val blockList: StateFlow<List<GifsInfo>> get() = _blockList

    //════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣

    init {
        refresh()
    }

    //══════════ Блокировка ═════════════════════════════════════════════════════════════╗

    fun refresh() {                                                                    //║
        scope.launch {
            val snapshot = blockDao.observeBlocksWithGif().first()   // suspend до 1‑го emission
            _blockList.value = snapshot.mapNotNull { it.gif?.toDomain() }
        }
    }                                                                                  //║
    //═══════════════════════════════════════════════════════════════════════════════════╝

    //══════════ Блокировка ═════════════════════════════════════════════════════════════╗

    fun refreshListAndBlock(list: MutableStateFlow<List<GifsInfo>>) {                  //║
        val blockedSet = blockList.value.map { it.id }
        list.value = list.value.filterNot { it.id in blockedSet }                      //║
    }                                                                                  //║
    //═══════════════════════════════════════════════════════════════════════════════════╝


    fun blockItem(item: GifsInfo) {
        scope.launch {
            runCatching {
                val item = item.toEntity()
                db.withTransaction {
                    infoDao.insert(item)
                    blockDao.insertBlock(BlockEntity(id = item.id, gifId = item.id))
                }
                refresh()
            }.onSuccess {
                snackBarEvent.success("GIFs заблокирован\"")
            }
                .onFailure { errorMsg ->
                    Timber.e(errorMsg, "!!! Не удалось заблокировать GIF")
                    snackBarEvent.error("Ошибка блокировки: $errorMsg")
                }
        }
    }


}