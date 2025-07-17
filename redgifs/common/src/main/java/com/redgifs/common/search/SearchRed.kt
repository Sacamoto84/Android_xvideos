package com.redgifs.common.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.common.di.ApplicationScope
import com.client.common.util.toPrettyCount2
import com.client.common.util.toTwoDecimalPlacesWithColon
import com.redgifs.common.ThemeRed
import com.redgifs.common.saved.SavedRed
import com.redgifs.db.dao.SearchRedHistoryDao
import com.redgifs.db.entity.SearchRedHistoryEntity
import com.redgifs.model.tag.TagSuggestion
import com.redgifs.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SearchRed @Inject constructor(
    val dao: SearchRedHistoryDao,
    val savedRed: SavedRed,
    val redApi: RedApi,
    @ApplicationScope val scope: CoroutineScope
) {

    var searchText = MutableStateFlow("")

    var searchTextDone = MutableStateFlow("")

    val focused = MutableStateFlow(false)

    var searchTextSuggestions = MutableStateFlow<List<TagSuggestion>>(emptyList())

    val stack = ArrayDeque<String>()

    init{
        scope.launch {
            searchText.collect {
                if (it != ""){
                    val a = redApi.getTagSuggestions(it)
                    searchTextSuggestions.value = a
                }
            }
        }
    }

    @Composable
    fun CustomBasicTextField(
        value: String,
        onValueChange: (String) -> Unit,
        onDone: (String) -> Unit = {},
        modifier: Modifier = Modifier,
    ) {

        val searchTagSuggestion = searchTextSuggestions.collectAsStateWithLifecycle().value

        val history1 by history.collectAsState()  // lifecycleScope под капотом

        val focusRequester = remember { FocusRequester() }

        val focusManager = LocalFocusManager.current

        var isFocused by remember { mutableStateOf(false) }

//        LaunchedEffect(isFocused) {
//            focused.value = isFocused
//            Timber.i("!!! >>> isFocused: $isFocused")
//        }

        // Отслеживаем высоту клавиатуры
        val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

        val keyboardController = LocalSoftwareKeyboardController.current

        val searchTextValue = searchText.collectAsStateWithLifecycle().value

        LaunchedEffect(imeVisible) {
            if (!imeVisible) {
                // Клавиатура скрылась, убираем фокус
                focusManager.clearFocus()
            }
            focused.value = imeVisible
        }

            Column (
                modifier = modifier.fillMaxWidth()
                    .background(ThemeRed.colorCommonBackground2, RoundedCornerShape(8.dp))
                    .border(
                        width = if (isFocused) 2.dp else 1.dp,
                        color = if (isFocused) ThemeRed.colorBorderSelect else ThemeRed.colorBorderGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(start = 0.dp, end = 0.dp),
            ) {


                AnimatedVisibility(isFocused) {

                    Box(Modifier.fillMaxWidth().height(126.dp)) {
                        Column {
                            LazyColumn(Modifier.fillMaxSize().weight(1f)) {
                                items(searchTagSuggestion) {

                                    val query = searchTextValue
                                    val text = it.text
                                    val startIndex = text.indexOf(query, ignoreCase = true)
                                    val annotatedString = buildAnnotatedString {
                                        if (startIndex != -1) {
                                            append(text.substring(0, startIndex))
                                            withStyle(style = SpanStyle(color = ThemeRed.colorYellow)) {
                                                append(text.substring(startIndex, startIndex + query.length))
                                            }
                                            append(text.substring(startIndex + query.length))
                                        } else { append(text) }
                                    }

                                    Box(Modifier.padding(start = 3.dp, top = 1.dp, end = 3.dp).background(ThemeRed.colorTabLevel2).clickable(onClick = {
                                        searchText.value = it.text
                                        searchTextDone.value = it.text
                                        stack.addLast(it.text)
                                    })){

                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                it.text,
                                                fontFamily = ThemeRed.fontFamilyDMsanss,
                                                fontSize = 18.sp,
                                                textAlign = TextAlign.Start,
                                                color = Color.Black,
                                                modifier = Modifier
                                                    .padding(start = 4.dp)
                                                    .height(30.dp)
                                                    .offset(1.dp, 1.dp)
                                                    .alignByBaseline()
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                annotatedString,
                                                fontFamily = ThemeRed.fontFamilyDMsanss,
                                                fontSize = 18.sp,
                                                textAlign = TextAlign.Start,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .padding(start = 4.dp)
                                                    .height(30.dp)
                                                    .alignByBaseline()
                                            )

                                            Text(
                                                it.gifs.toPrettyCount2(),
                                                fontFamily = ThemeRed.fontFamilyDMsanss,
                                                fontSize = 18.sp,
                                                textAlign = TextAlign.Start,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .padding(start = 4.dp)
                                                    .height(30.dp)
                                                    .alignByBaseline()
                                            )

                                        }

                                    }
                                }
                            }
                            HorizontalDivider(color = ThemeRed.colorBorderGray, thickness = 1.dp)
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp).height(46.dp)
                ) {

                    if ((value == "") && (!isFocused)) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF757575),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            color = Color.White,
                            fontFamily = ThemeRed.fontFamilyDMsanss,
                            textAlign = TextAlign.Left
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            },
                        cursorBrush = SolidColor(Color.Gray),

                        // 1. Говорим IME, что нам нужна кнопка «Done»
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text       // по желанию
                        ),

                        // 2. Обрабатываем её нажатие
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onDone(value)
                                add(value)                         // например, запускаем поиск
                                focusManager.clearFocus()          // убираем курсор
                                keyboardController?.hide()         // закрываем клавиатуру
                            }
                        )
                    )

                    if (value != "") {
                        Icon( Icons.Default.Clear, contentDescription = null, tint = Color(0xFF757575),
                            modifier = Modifier.width(36.dp).height(46.dp).clickable(onClick = { onDone("");onValueChange("") }))
                    }

                    Icon( Icons.Default.Undo, contentDescription = null, tint = Color(0xFF757575),
                        modifier = Modifier.width(36.dp).height(46.dp).clickable(onClick = {
                            if (!stack.isEmpty()){
                                val s = stack.removeLast()
                                searchText.value = s
                                searchTextDone.value = s
                            }
                        }))

                    //ExpandMenuHelper(savedRed = savedRed)
                    //ExpandMenuHistory(history1)
                }
            }
        //}
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ExpandMenuHelper(
        modifier: Modifier = Modifier,
        savedRed: SavedRed
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.then(modifier)
        )
        {
            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                Icon(
                    if (expanded) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "",
                    tint = Color(0xFF757575),
                    modifier = Modifier
                        .width(24.dp)
                        .height(46.dp)
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    //.imePadding()
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    //.clip(RoundedCornerShape(16.dp))
                    .border(1.dp, ThemeRed.colorTabLevel3, RoundedCornerShape(16.dp))
                ,
                containerColor = ThemeRed.colorTabLevel2,
                shape = RoundedCornerShape(16.dp)
            ) {

                FlowRow(Modifier.padding(4.dp).fillMaxSize(), maxItemsInEachRow = 10) {
                    savedRed.tagsList.sortedByDescending { it.count }.take(200).forEach {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(25))
                                .border(1.dp, ThemeRed.colorTextGray, RoundedCornerShape(25))
                                .background(ThemeRed.colorTabLevel1)
                                .padding(4.dp)
                                .clickable(onClick = {
                                    searchText.value = it.name
                                    searchTextDone.value = it.name
                                    expanded = false
                                })

                        ) {

                            Text(
                                it.name, //+" " + it.count.toPrettyCountInt(),
                                color = Color.White,
                                modifier = Modifier,
                                fontSize = 12.sp
                            )

                        }
                    }
                }


            }
        }

    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ExpandMenuHistory(
        items: List<String>,
        modifier: Modifier = Modifier,
    ) {

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.then(modifier)
        )
        {
            IconButton(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .height(46.dp)
                    .width(24.dp)
                    .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                onClick = {}) {
                Icon(
                    if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "",
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(24.dp)
                )
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(IntrinsicSize.Min),
                containerColor = ThemeRed.colorBottomBarDivider
            ) {
                //DropdownMenuItem_Download(item){ expanded = false }

                items.reversed().forEach {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickable(onClick = { searchText.value = it }),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            it,
                            color = Color.White,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .padding(start = 16.dp),
                            fontFamily = ThemeRed.fontFamilyDMsanss
                        )

                        IconButton(onClick = { delete(it) }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }
                    }

                }

            }
        }
    }

    //Dao
    @OptIn(DelicateCoroutinesApi::class)
    val history: StateFlow<List<String>> =
        dao.observeAllTexts()
            .stateIn(
                scope = GlobalScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    @OptIn(DelicateCoroutinesApi::class)
    fun add(text: String) = GlobalScope.launch {
        dao.insertAndTrim(SearchRedHistoryEntity(text = text))
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun delete(text: String) = GlobalScope.launch {
        dao.deleteByTexts(text = text)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun clear() = GlobalScope.launch { dao.deleteAll() }

}


