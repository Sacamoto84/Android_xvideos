package com.client.xvideos.screens_red

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.feature.redgifs.types.UserInfo

object GlobalRed {

    val listAllUsers = mutableListOf<UserInfo>()

    //Region══════════ Блокировка ═════════════════════╦══════════════════════════════════════════════════════════════╗
    var blockVisibleDialog by mutableStateOf(false)  //║ Показ диалога на добавление в блок лист                      ║
    var blockList =
        mutableStateListOf<String>()                 //║                                                              ║
    //═════════════════════════════════════════════════╬══════════════════════════════════════════════════════════════╣

}