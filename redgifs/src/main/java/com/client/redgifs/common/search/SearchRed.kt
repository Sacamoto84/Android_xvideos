package com.client.redgifs.common.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import com.client.redgifs.App
import com.client.redgifs.ThemeRed
import com.client.redgifs.common.saved.SavedRed
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

object SearchRed {


    //var searchText by mutableStateOf("")
    var searchText = MutableStateFlow("")

    var verified = MutableStateFlow(false)

    var searchTextDone = MutableStateFlow("")

    @Composable
    fun CustomBasicTextField(
        value: String,
        onValueChange: (String) -> Unit,
        onDone: (String) -> Unit = {},
        modifier: Modifier = Modifier,
    ) {

        val history1 by history.collectAsState()  // lifecycleScope под капотом

        val focusRequester = remember { FocusRequester() }

        val focusManager = LocalFocusManager.current

        var isFocused by remember { mutableStateOf(false) }

        // Отслеживаем высоту клавиатуры
        val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(imeVisible) {
            if (!imeVisible) {
                // Клавиатура скрылась, убираем фокус
                focusManager.clearFocus()
            }
        }

        Box(
            modifier = modifier
                .height(48.dp)
                .background(ThemeRed.colorCommonBackground2, RoundedCornerShape(8.dp))
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) ThemeRed.colorBorderSelect else ThemeRed.colorBorderGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(start = 4.dp, end = 0.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
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
                    ),

                    )

                if (value != "") {
                    IconButton(onClick = {
                        onDone("")
                        onValueChange("")
                    }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null,
                            tint = Color(0xFF757575)
                        )
                    }
                }

                ExpandMenuHelper()

                ExpandMenuHistory(history1)


            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ExpandMenuHelper(
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
                    .size(48.dp)
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
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
                modifier = Modifier
                    //.imePadding()
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                containerColor = ThemeRed.colorTabLevel2
            ) {
                //DropdownMenuItem_Download(item){ expanded = false }


                FlowRow(Modifier.fillMaxSize(), maxItemsInEachRow = 10) {
                    SavedRed.tagsList.sortedByDescending { it.count }.take(200).forEach {
                        Row( modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .padding(vertical = 2.dp)

                            .border(1.dp, ThemeRed.colorTextGray, RoundedCornerShape(25))
                            .background(Color.Transparent)
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
                    .size(48.dp)
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
        App.instance.db.redSearchHistoryDao().observeAllTexts()
            .stateIn(
                scope = GlobalScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    @OptIn(DelicateCoroutinesApi::class)
    fun add(text: String) = GlobalScope.launch {
        App.instance.db.redSearchHistoryDao().insertAndTrim(SearchRedHistoryEntity(text = text))
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun delete(text: String) = GlobalScope.launch {
        App.instance.db.redSearchHistoryDao().deleteByTexts(text = text)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun clear() = GlobalScope.launch { App.instance.db.redSearchHistoryDao().deleteAll() }

}

/**
 * Таблица с кешем строк ответов от сервера
 */
@Entity(tableName = "search_red_history")
data class SearchRedHistoryEntity(
    @PrimaryKey
    val text: String,
    val timeCreate: Long = System.currentTimeMillis(),
)

@Dao
interface SearchRedHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SearchRedHistoryEntity)

    @Query("SELECT text FROM search_red_history ORDER BY timeCreate DESC")
    fun observeAllTexts(): Flow<List<String>>   // <‑‑ поток изменений

    @Query("DELETE FROM search_red_history")
    suspend fun deleteAll()


    @Transaction
    suspend fun insertAndTrim(item: SearchRedHistoryEntity, limit: Int = 10) {
        insert(item)
        deleteOlderThanLimit(limit)
    }


    /**
     * Удаляем всё, что не входит в последние [limit] строк,
     * отсортированные по времени создания (DESC).
     *
     * `rowid` уникален, поэтому подходит для подзапроса.
     */
    @Query(
        """
        DELETE FROM search_red_history 
        WHERE rowid NOT IN (
            SELECT rowid 
            FROM search_red_history 
            ORDER BY timeCreate DESC 
            LIMIT :limit
        )
        """
    )
    suspend fun deleteOlderThanLimit(limit: Int)


    @Query("DELETE FROM search_red_history WHERE text = :text")
    suspend fun deleteByTexts(text: String)

}