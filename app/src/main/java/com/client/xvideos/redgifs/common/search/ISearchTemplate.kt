package com.client.xvideos.redgifs.common.search

import com.client.xvideos.redgifs.model.tag.TagSuggestion
import kotlinx.coroutines.flow.MutableStateFlow

abstract class ISearchTemplate {


    /**
     * Отображаемый текст
     */
    var searchText = MutableStateFlow("")

    /**
     * Текст по которому будет идти запрос на сервер
     */
    var searchTextDone = MutableStateFlow("")


    var searchTextSuggestions = MutableStateFlow<List<TagSuggestion>>(emptyList())


}