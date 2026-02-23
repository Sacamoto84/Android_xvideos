package com.client.xvideos.redgifs.common.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.tag.TagSuggestion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


abstract class ISearchTemplate(
    val scope: CoroutineScope,
    val dao : IDaoSearchTemplate
) {


    /**
     * Отображаемый текст
     */
    var searchText = MutableStateFlow("")

    /**
     * Текст по которому будет идти запрос на сервер
     */
    var searchTextDone = MutableStateFlow("")


    var searchTextSuggestions = MutableStateFlow<List<TagSuggestion>>(emptyList())

    val stack = ArrayDeque<String>()

    val focused = MutableStateFlow(false)


    @Composable
    fun ExpandMenuHistory(
        items: () -> List<String>, modifier: Modifier = Modifier,
    ) {
        ExpandMenuHistoryContent( 
            items = items, 
            modifier = modifier,  
            onClick = { 
                // Исправлено: передаем чистую строку вместо TextFieldValue.toString()
                searchText.value = it 
            }, 
            onDeleteClick = { scope.launch(Dispatchers.Main) { delete(it) } } 
        )
    }


    @Composable
    fun ExpandMenuHelper(
        modifier: Modifier = Modifier,
        savedRed: SavedRed
    ) {
        ExpandMenuHelperContent(
            tags = savedRed.tagsList,
            onTagClick = { tag ->
                searchText.value = tag.name
                searchTextDone.value = tag.name
            },
            modifier = modifier
        )
    }



    @Composable
    fun CustomBasicTextField(
        modifier: Modifier = Modifier,
    ) {
        val searchTagSuggestions by searchTextSuggestions.collectAsStateWithLifecycle()
        val historyItems by history.collectAsState()
        val text by searchText.collectAsStateWithLifecycle()

        CustomBasicTextFieldContent(
            modifier = modifier,
            value = text,
            onValueChange = { 
                searchText.value = it 
            },
            suggestions = searchTagSuggestions,
            onSuggestionClick = { suggestion ->
                searchText.value = suggestion.text
                searchTextDone.value = suggestion.text
                scope.launch(Dispatchers.Main) {
                    add(suggestion.text)
                }
            },
            onClearClick = {
                searchText.value = ""
            },
            onUndoClick = {
                if (stack.isNotEmpty()) {
                    val last = stack.removeLast()
                    searchText.value = last
                }
            },
            onDone = {
                searchTextDone.value = it
                scope.launch(Dispatchers.Main) {
                    add(it)
                }
            },
            expandMenuHistory = {
                ExpandMenuHistory(items = { historyItems })
            }
        )

    }

    val history: StateFlow<List<String>> = dao.observeAllTexts().stateIn( scope = scope, started = SharingStarted.WhileSubscribed(5_000), initialValue = emptyList() )

    suspend fun add(text: String ) = dao.insertAndTrim(text)
    suspend fun delete( text: String ) = dao.deleteByTexts(text)
    suspend fun clear() = dao.deleteAll()

}
