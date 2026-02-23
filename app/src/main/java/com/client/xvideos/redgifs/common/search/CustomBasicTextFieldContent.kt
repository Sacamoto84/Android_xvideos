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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.common.util.toPrettyCount2
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.model.tag.TagSuggestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun CustomBasicTextFieldContent(
    searchText: MutableStateFlow<String>,
    searchTextSuggestions: MutableStateFlow<List<TagSuggestion>>,
    searchTextDone: MutableStateFlow<String>,
    stack: ArrayDeque<String>,
    modifier: Modifier = Modifier,
    expandMenuHistory: @Composable () -> Unit,
    onDone: (String) -> Unit
) {
    val value = searchText.collectAsStateWithLifecycle().value
    val searchTagSuggestion = searchTextSuggestions.collectAsStateWithLifecycle().value

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val keyboardController = LocalSoftwareKeyboardController.current

    val searchTextValue = searchText.collectAsStateWithLifecycle().value

    LaunchedEffect(imeVisible) {
        if (!imeVisible) {
            focusManager.clearFocus()
        }
    }

    var delayFocus by remember { mutableStateOf(false) }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            delay(500)
        } else {
            delay(1)
        }
        delayFocus = isFocused
    }

    Column(
        modifier = modifier
            .padding(top = if (isFocused) 4.dp else 0.dp)
            .fillMaxWidth()
            .background(ThemeRed.colorCommonBackground2, RoundedCornerShape(8.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) ThemeRed.colorBorderSelect else ThemeRed.colorBorderGray,
                shape = RoundedCornerShape(8.dp)
            ),
    ) {
        AnimatedVisibility(
            delayFocus,
            enter = expandVertically(animationSpec = tween(durationMillis = 500)) + fadeIn(
                animationSpec = tween(durationMillis = 500)
            ),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 500)) + fadeOut(
                animationSpec = tween(durationMillis = 500)
            ),
        ) {
            Box(
                Modifier
                    .padding(top = 1.dp)
                    .fillMaxWidth()
                    .height(126.dp)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
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
                                } else {
                                    append(text)
                                }
                            }

                            Box(
                                Modifier
                                    .padding(start = 3.dp, top = 1.dp, end = 3.dp)
                                    .background(ThemeRed.colorTabLevel2)
                                    .clickable(onClick = {
                                        searchText.value = it.text
                                        searchTextDone.value = it.text
                                        stack.addLast(it.text)
                                    })
                            ) {
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
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
            modifier = Modifier
                .padding(start = 12.dp, end = 4.dp)
                .height(46.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = { searchText.value = it },
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
                    .onFocusChanged { focusState -> isFocused = focusState.isFocused },
                cursorBrush = SolidColor(Color.Gray),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        searchTextDone.value = value
                        onDone(value)
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            )

            if (value != "") {
                Icon(
                    Icons.Default.Clear, contentDescription = null, tint = Color(0xFF757575),
                    modifier = Modifier
                        .size(36.dp)
                        .padding(4.dp)
                        .clickable(onClick = {
                            searchTextDone.value = ""
                            searchText.value = ""
                        })
                )
            }

            Icon(
                Icons.Default.Undo, contentDescription = null, tint = Color(0xFF757575),
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
                    .clickable(onClick = {
                        if (!stack.isEmpty()) {
                            val s = stack.removeLast()
                            searchText.value = s
                            searchTextDone.value = s
                        }
                    })
            )

            expandMenuHistory()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF303030)
@Composable
fun PreviewCustomBasicTextFieldContent() {
    val searchText = remember { MutableStateFlow("big t") }
    val searchTextSuggestions = remember {
        MutableStateFlow(
            listOf(
                TagSuggestion(123456, "big tits", "tag"),
                TagSuggestion(789, "big toys", "tag"),
                TagSuggestion(4567, "big thighs", "tag")
            )
        )
    }
    val searchTextDone = remember { MutableStateFlow("") }
    val stack = remember { ArrayDeque<String>() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CustomBasicTextFieldContent(
            searchText = searchText,
            searchTextSuggestions = searchTextSuggestions,
            searchTextDone = searchTextDone,
            stack = stack,
            expandMenuHistory = {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = Color(0xFF757575),
                    modifier = Modifier
                        .size(36.dp)
                        .padding(4.dp)
                )
            },
            onDone = {}
        )
    }
}
