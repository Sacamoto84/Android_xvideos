package com.client.xvideos.red.common.search

import kotlinx.coroutines.flow.MutableStateFlow

object SearchRed {

    //var searchText by mutableStateOf("")
    var searchText = MutableStateFlow("")

    var verified = MutableStateFlow(false)

}