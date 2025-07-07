package com.client.redgifs.common.block

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.App
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.client.xvideos.redgifs.common.block.useCase.blockGetAllBlockedGifs
import com.client.xvideos.redgifs.common.block.useCase.blockGetAllBlockedGifsInfo
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber


object BlockRed {

    //══════════ Блокировка ═══════════════════════════╦══════════════════════════════════════════════════════════════╗
    var blockVisibleDialog by mutableStateOf(false)                //║ Показ диалога на добавление в блок лист        ║
    var blockList = MutableStateFlow<List<GifsInfo>>(emptyList())  //║                                                ║
    //═════════════════════════════════════════════════╬══════════════════════════════════════════════════════════════╣

    //══════════ Блокировка ═════════════════════════════════════════════════════════════╗
    fun refreshListAndBlock(list: MutableStateFlow<List<GifsInfo>>){                   //║
        val blockList1 = mutableStateListOf<String>()
        blockList1.clear()                                                              //║
        blockList1.addAll(blockGetAllBlockedGifs())                                     //║
        val blockedSet = blockList1.toSet()                                             //║
        list.value = list.value.filterNot { it.id in blockedSet }                      //║
    }                                                                                  //║
    //═══════════════════════════════════════════════════════════════════════════════════╝

    //═══════════════════════════════════════════════════════╗
    fun refreshBlockList(){                                //║
        val blockList1 = mutableStateListOf<GifsInfo>()    //║
        blockList1.clear()                                 //║
        blockList1.addAll(blockGetAllBlockedGifsInfo())    //║
        val blockedSet = blockList1.toSet()                //║
        blockList.value = blockedSet.toList()              //║
    }                                                      //║
    //═══════════════════════════════════════════════════════╝

    fun blockItem(item: GifsInfo) {
        val result = com.client.xvideos.redgifs.common.block.useCase.blockItem(item)
        if (result.isSuccess) {
            Timber.i("!!! GIF успешно заблокирован")
            Toast.makeText(App.instance.applicationContext, "GIFs заблокирован", Toast.LENGTH_SHORT).show()
        } else {
            val exception = result.exceptionOrNull()
            val errorMsg = exception?.localizedMessage ?: "Неизвестная ошибка"
            Timber.e(exception, "Не удалось заблокировать GIF")
            Toast.makeText(App.instance.applicationContext, "Ошибка блокировки: $errorMsg", Toast.LENGTH_SHORT).show()
        }
        refreshBlockList()
        //refreshListAndBlock(list)
    }

}