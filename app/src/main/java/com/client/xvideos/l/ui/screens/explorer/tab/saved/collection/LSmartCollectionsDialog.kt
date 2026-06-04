package com.client.xvideos.l.ui.screens.explorer.tab.saved.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.featured.saved.LSmartCollectionCandidate
import com.client.xvideos.l.featured.saved.LSmartCollectionKind
import com.client.xvideos.l.theme.ThemeL
import com.composeunstyled.Text

@Composable
fun LSmartCollectionsDialog(
    candidates: List<LSmartCollectionCandidate>,
    onDismiss: () -> Unit,
    onCreate: (LSmartCollectionCandidate) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Smart collections",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = ThemeL.textColor
            )
        },
        text = {
            if (candidates.isEmpty()) {
                Text(
                    "Пока мало метаданных для авто-коллекций. Добавь несколько элементов из альбомов, где есть теги, авторы или общий album id.",
                    color = ThemeL.grey2,
                    style = ThemeL.Type.body
                )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.heightIn(max = 420.dp)
                ) {
                    items(candidates) { candidate ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onCreate(candidate) }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ThemeL.primaryColor.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(candidate.count.toString(), color = ThemeL.primaryColor, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(candidate.title, color = Color.White, style = ThemeL.Type.rowTitle)
                                Text(candidate.subtitle, color = ThemeL.grey2, style = ThemeL.Type.rowSubtitle)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть", style = ThemeL.Type.button.copy(color = ThemeL.primaryColor))
            }
        },
        containerColor = ThemeL.grey5,
        titleContentColor = ThemeL.textColor,
        textContentColor = ThemeL.textColor
    )
}

/** Диалог с найденными авто-коллекциями. */
@Preview
@Composable
private fun LSmartCollectionsDialogPreview() {
    LSmartCollectionsDialog(
        candidates = listOf(
            LSmartCollectionCandidate(
                kind = LSmartCollectionKind.TAG,
                key = "anime",
                title = "anime",
                subtitle = "Тег • 12 элементов",
                count = 12
            ),
            LSmartCollectionCandidate(
                kind = LSmartCollectionKind.AUTHOR,
                key = "author1",
                title = "SomeAuthor",
                subtitle = "Автор • 7 элементов",
                count = 7
            ),
            LSmartCollectionCandidate(
                kind = LSmartCollectionKind.ALBUM,
                key = "12345",
                title = "Big Album",
                subtitle = "Альбом • 5 элементов",
                count = 5
            ),
        ),
        onDismiss = {},
        onCreate = {}
    )
}

/** Пустое состояние: метаданных пока недостаточно. */
@Preview
@Composable
private fun LSmartCollectionsDialogEmptyPreview() {
    LSmartCollectionsDialog(
        candidates = emptyList(),
        onDismiss = {},
        onCreate = {}
    )
}
