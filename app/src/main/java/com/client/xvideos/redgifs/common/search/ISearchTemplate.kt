package com.client.xvideos.redgifs.common.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.common.util.toPrettyCount2
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.tag.TagSuggestion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        ExpandMenuHistoryContent( items = items, modifier = modifier,  onClick = { searchText.value = TextFieldValue( text = it,  selection = TextRange(it.length) ).toString() }, onDeleteClick = { scope.launch(Dispatchers.Main) { delete(it) } } )
    }


    @OptIn(ExperimentalMaterial3Api::class)
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
        value: String,
        onValueChange: (String) -> Unit,
        onDone: (String) -> Unit = {},
        modifier: Modifier = Modifier,
    ) {
        val searchTagSuggestion by searchTextSuggestions.collectAsStateWithLifecycle()
        val historyItems by history.collectAsState()
        val searchTextValue by searchText.collectAsStateWithLifecycle()



        CustomBasicTextFieldContent(
//            value = value,
//            onValueChange = onValueChange,
//            onDone = {
//                onDone(it)
//                add(it)
//            },
//            searchTagSuggestion = searchTagSuggestion,
//            searchTextValue = searchTextValue,
//            onSuggestionClick = { suggestion ->
//                searchText.value = suggestion.name
//                searchTextDone.value = suggestion.name
//                stack.addLast(suggestion.name)
//            },
//            onUndoClick = {
//                if (stack.isNotEmpty()){
//                    val s = stack.removeLast()
//                    searchText.value = s
//                    searchTextDone.value = s
//                }
//            },
//            onClearClick = {
//                onDone("")
//                onValueChange("")
//            },
//            onFocusChange = { focused.value = it },

            modifier = modifier,

            expandMenuHistory = {
                ExpandMenuHistory(items = {historyItems})
            },
            searchText = searchText,
            searchTextSuggestions = searchTextSuggestions,
            searchTextDone = searchTextDone,
            stack = stack,

            onDone = {
                scope.launch(Dispatchers.Main) {
                    add(it)
                }
            }


        )

    }

    val history: StateFlow<List<String>> = dao.observeAllTexts().stateIn( scope = scope, started = SharingStarted.WhileSubscribed(5_000), initialValue = emptyList() )

    suspend fun add(text: String ) = dao.insertAndTrim(text)
    suspend fun delete( text: String ) = dao.deleteByTexts(text)
    suspend fun clear() = dao.deleteAll()

}