package com.client.xvideos.redgifs.common.block

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.withTransaction
import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.room.AppDatabase
import com.client.xvideos.common.room.dao.r.R_BlockDao
import com.client.xvideos.common.room.dao.r.R_GifsInfoDao
import com.client.xvideos.common.room.entity.r.R_BlockEntity
import com.client.xvideos.common.room.entity.r.toDomain
import com.client.xvideos.common.room.entity.r.toEntity
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.model.GifsInfo
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
    private val blockDao: R_BlockDao,
    private val infoDao: R_GifsInfoDao,
    private val db: AppDatabase,
    @ApplicationScope private val scope: CoroutineScope
) {


    //Блокируемый елемент
    var blockItem : GifsInfo? = null

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
                    blockDao.insertBlock(R_BlockEntity(id = item.id, gifId = item.id))
                }
                refresh()
            }.onSuccess {
                SnackBar.success("GIFs заблокирован")
            }
                .onFailure { errorMsg ->
                    Timber.e(errorMsg, "!!! Не удалось заблокировать GIF")
                    SnackBar.error("Ошибка блокировки: $errorMsg")
                }
        }
    }


}