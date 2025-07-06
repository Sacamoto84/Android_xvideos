package com.client.xvideos.redgifs.common.block

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.withTransaction
import com.client.xvideos.di.ApplicationScope
import com.client.xvideos.redgifs.common.block.useCase.blockGetAllBlockedGifs
import com.client.xvideos.redgifs.common.block.useCase.blockGetAllBlockedGifsInfo
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.db.AppRedGifsDatabase
import com.client.xvideos.redgifs.db.dao.BlockDao
import com.client.xvideos.redgifs.db.dao.GifsInfoDao
import com.client.xvideos.redgifs.db.entity.BlockEntity
import com.client.xvideos.redgifs.db.entity.toEntity
import com.client.xvideos.redgifs.network.types.GifsInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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

    //══════════ Блокировка ═════════════════════════════════════════╦════════════════════════════════════════════════╗
    var blockVisibleDialog by mutableStateOf(false)                //║ Показ диалога на добавление в блок лист        ║
    var blockList = MutableStateFlow<List<GifsInfo>>(emptyList())  //║                                                ║
    //════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣

    //══════════ Блокировка ═════════════════════════════════════════════════════════════╗
    fun refreshListAndBlock(list: MutableStateFlow<List<GifsInfo>>) {                   //║
        val blockList1 = mutableStateListOf<String>()
        blockList1.clear()                                                              //║
        blockList1.addAll(blockGetAllBlockedGifs())                                     //║
        val blockedSet = blockList1.toSet()                                             //║
        list.value = list.value.filterNot { it.id in blockedSet }                      //║
    }                                                                                  //║
    //═══════════════════════════════════════════════════════════════════════════════════╝

    //═══════════════════════════════════════════════════════╗
    fun refreshBlockList() {                               //║
        val blockList1 = mutableStateListOf<GifsInfo>()    //║
        blockList1.clear()                                 //║
        blockList1.addAll(blockGetAllBlockedGifsInfo())    //║
        val blockedSet = blockList1.toSet()                //║
        blockList.value = blockedSet.toList()              //║
    }                                                      //║
    //═══════════════════════════════════════════════════════╝

    fun blockItem(item: GifsInfo) {
        scope.launch {
            runCatching {
                val item = item.toEntity()
                db.withTransaction {
                    infoDao.insert(item)
                    blockDao.insertBlock(BlockEntity(id = item.id, gifId = item.id))
                }
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