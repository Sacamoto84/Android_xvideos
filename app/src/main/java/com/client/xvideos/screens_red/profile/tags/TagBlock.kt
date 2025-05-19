package com.client.xvideos.screens_red.profile.tags

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.screens_red.ThemeRed

@Composable
fun TagsBlock(tags: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    val sorted = remember(tags) { tags.sorted() }

    SubcomposeLayout { constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val maxW  = constraints.maxWidth

        /* ---------- измеряем все теги ---------- */
        val tagPl = sorted.map { tag ->
            subcompose("t_$tag") { TagChip(tag) }.first().measure(loose)
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
            var rowW = 0; var lines = 1
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
        var height = 0; var rowH = 0; var rowW = 0
        shown.forEach { p ->
            if (rowW + p.width > maxW) { height += rowH; rowH = 0; rowW = 0 }
            rowW += p.width; rowH = maxOf(rowH, p.height)
        }
        height += rowH

        /* ---------- размещение ---------- */
        layout(maxW, height) {
            var x = 0; var y = 0; var lineH = 0
            shown.forEach { p ->
                if (x + p.width > maxW) { x = 0; y += lineH; lineH = 0 }
                p.placeRelative(x, y)
                x += p.width; lineH = maxOf(lineH, p.height)
            }
        }
    }
}

/* -------------------- CHIP -------------------- */
@Composable
private fun TagChip(text: String) {
    var pressed by remember { mutableStateOf(false) }
    val bg by animateColorAsState(
        if (pressed) Color(0xFF652E45) else Color.Transparent
    )

    Text(
        text, color = Color.White, fontSize = 14.sp,
        fontFamily = ThemeRed.fontFamilyPopinsRegular,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, Color(0xFFEA616F), RoundedCornerShape(50))
            .wrapContentWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

/* ------------- BUTTON («Показать все» / «Скрыть») ------------- */
@Composable
private fun ExpandCollapseButton(expanded: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, Color(0xFFEA616F)),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .wrapContentWidth()
    ) {
        Text(
            if (expanded) "Скрыть" else "Показать все",
            fontSize = 14.sp,
            fontFamily = ThemeRed.fontFamilyPopinsRegular,
            color = Color(0xFFEA616F)
        )
    }
}

