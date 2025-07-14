package com.example.ui.screens.profile.tags

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgifs.common.ThemeRed

@Composable
fun TagsBlock(tags: List<String>, tagsSelect: List<String>, onClick: (String) -> Unit = {}) {
    var expanded by remember { mutableStateOf(false) }
    val sorted = remember(tags) { tags.sorted() }

    SubcomposeLayout { constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val maxW = constraints.maxWidth

        /* ---------- измеряем все теги ---------- */
        val tagPl = sorted.map { tag ->
            subcompose("t_$tag") { TagChip(tag, tag in tagsSelect, onClick) }.first().measure(loose)
        }

        /* ---------- одна кнопка: текст зависит от expanded ---------- */
        val btnPl = subcompose("btn") {
            ExpandCollapseButton(expanded) { expanded = !expanded }
        }.first().measure(loose)

        /* ---------- формируем список для вывода ---------- */
        val shown = if (expanded) {
            tagPl + btnPl                                // все теги + кнопка «Скрыть»
        } else {                                         // нужно уложить в 2 строки
            val list = mutableListOf<Placeable>()
            var rowW = 0
            var lines = 1
            for (p in tagPl) {
                // резервируем место под кнопку, т.к. она будет последней
                val need = p.width + if (lines == 2) btnPl.width else 0
                if (rowW + need > maxW) {                // перенос
                    lines++; if (lines > 2) break
                    rowW = 0
                }
                list += p; rowW += p.width
            }
            list + btnPl                                 // кнопка «Показать все»
        }

        /* ---------- высота ---------- */
        var height = 0
        var rowH = 0
        var rowW = 0
        shown.forEach { p ->
            if (rowW + p.width > maxW) {
                height += rowH; rowH = 0; rowW = 0
            }
            rowW += p.width
            rowH = maxOf(rowH, p.height)
        }
        height += rowH

        /* ---------- размещение ---------- */
        layout(maxW, height) {
            var x = 0
            var y = 0
            var lineH = 0
            shown.forEach { p ->
                if (x + p.width > maxW) {
                    x = 0; y += lineH; lineH = 0
                }
                p.placeRelative(x, y)
                x += p.width; lineH = maxOf(lineH, p.height)
            }
        }
    }
}

/* -------------------- CHIP -------------------- */
@Composable
private fun TagChip(text: String, select: Boolean, onClick: (String) -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val bg by animateColorAsState(
        if (pressed) Color(0xFF652E45) else Color.Transparent
    )

    Text(
        text, color =
            if (select) Color.Black else Color.White,
        fontSize = 14.sp,
        fontFamily = ThemeRed.fontFamilyPopinsRegular,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(50))
            .background(if (select) ThemeRed.colorYellow else bg)
            .border(1.dp, ThemeRed.colorYellow, RoundedCornerShape(50))
            .wrapContentWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = { onClick.invoke(text) })
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpandCollapseButton(expanded: Boolean, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .size(32.dp)
            .border(1.dp, ThemeRed.colorYellow, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (expanded) Icons.Default.Close else Icons.Filled.MoreHoriz,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .size(18.dp)
                .clickable(
                    onClick = onClick
                )
        )
    }
}

