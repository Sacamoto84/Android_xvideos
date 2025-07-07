package com.client.xvideos.redgifs.common.block

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.withTransaction
import com.client.xvideos.App
import com.client.xvideos.di.ApplicationScope
import com.client.xvideos.redgifs.common.block.useCase.blockGetAllBlockedGifs
import com.client.xvideos.redgifs.common.block.useCase.blockGetAllBlockedGifsInfo
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.db.AppRedGifsDatabase
import com.client.xvideos.redgifs.db.dao.BlockDao
import com.client.xvideos.redgifs.db.dao.GifsInfoDao
import com.client.xvideos.redgifs.db.entity.BlockEntity
import com.client.xvideos.redgifs.db.entity.BlockWithGif
import com.client.xvideos.redgifs.db.entity.toDomain
import com.client.xvideos.redgifs.db.entity.toEntity
import com.client.xvideos.redgifs.network.types.GifsInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockRed @Inject constructor(
    private val blockDao: BlockDao,
    private val infoDao: GifsInfoDao,
    private val db: AppRedGifsDatabase,
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
                SnackBarEvent.success("GIFs заблокирован\"")
            }
                .onFailure { errorMsg ->
                    Timber.e(errorMsg, "!!! Не удалось заблокировать GIF")
                    SnackBarEvent.error("Ошибка блокировки: $errorMsg")
                }
        }
    }


}