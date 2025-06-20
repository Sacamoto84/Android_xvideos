package com.client.xvideos.red.screens.profile.atom

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.R
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.red.ThemeRed
import com.composeunstyled.Text
import java.util.Locale

@Composable
fun RedProfileCreaterInfo(item: CreatorResponse) {

    if (item.users.isEmpty()) return

    Column(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .fillMaxWidth()
    ) {

        //Top info
        Row(
            modifier = Modifier
                .padding(top = 2.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {


            UrlImage( item.users[0].profileImageUrl.toString(), modifier = Modifier.size(128.dp) )

            Row(
                modifier = Modifier.wrapContentHeight(), verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(8.dp))
                Text(
                    item.users[0].username,
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsMedium,
                    fontSize = 28.sp,
                    modifier = Modifier
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.verificed),
                    contentDescription = stringResource(id = R.string.dog_content_description),
                    modifier = Modifier
                        .size(26.dp)
                )
            }


        }

        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Text(
                    item.users[0].followers.toPrettyCount().toString(),
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsMedium
                )

                Text(
                    "Подписчиков",
                    color = Color(0xFF9E9DA9),
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )

            }

            Box(Modifier.width(1.dp).height(24.dp).background(Color(0xFF3D3C53)))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    item.users[0].views.toPrettyCount().toString(),
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsMedium
                )

                Text(
                    "Просмотров",
                    color = Color(0xFF9E9DA9),
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )
            }

            Box(Modifier.width(1.dp).height(24.dp).background(Color(0xFF3D3C53)))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                Text(
                    item.users[0].publishedGifs.toPrettyCount().toString(),
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsMedium
                )

                Text(
                    "Постов",
                    color = Color(0xFF9E9DA9),
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )

            }
        }

        Text(
            "About ${item.users[0].username}:",
            color = ThemeRed.colorTextGray,
            fontSize = 14.sp,
            fontFamily = ThemeRed.fontFamilyPopinsRegular
        )

        Spacer(Modifier.height(4.dp))

        Text(
            item.users[0].description.toString().trimMargin(),
            color = Color.White,
            fontSize = 14.sp, fontFamily = ThemeRed.fontFamilyPopinsRegular
        )

        Spacer(Modifier.width(8.dp))

    }


}


/**
 * 1_250   -> "1.2k"
 * 68_500  -> "68.5k"
 * 1_000_000 -> "1M"
 * 1_450_000 -> "1.4M"
 * 900     -> "900"
 */
@SuppressLint("DefaultLocale")
fun Long.toPrettyCount(): String {
    val absValue = kotlin.math.abs(this)

    return when {
        absValue < 1_000 -> "$absValue"                       // 0-999
        absValue < 1_000_000 -> {                                   // 1.0k-999.9k
            val value = absValue / 1_000.0
            String.format(Locale.US, "%.1fk", value)
        }

        absValue < 1_000_000_000 -> {                               // 1.0M-999.9M
            val value = absValue / 1_000_000.0
            String.format(Locale.US, "%.1fM", value)
        }

        else -> {                                                   // 1.0B+
            val value = absValue / 1_000_000_000.0
            String.format(Locale.US, "%.1fB", value)
        }
    }
}

/**
 *  68.7   → "01:08"
 * 134.0   → "02:14"
 *  9.2    → "00:09"
 */
fun Double.toMinSec(): String {
    val totalSec = this.toInt()                     // отбрасываем дробную часть
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return "%02d:%02d".format(minutes, seconds)     // ведущие нули
}