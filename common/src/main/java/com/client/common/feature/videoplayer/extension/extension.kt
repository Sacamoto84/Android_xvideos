package com.client.common.feature.videoplayer.extension

import com.client.common.feature.videoplayer.util.formatMinSec

fun Int.formatMinSec(): String {
    return formatMinSec(this)
}
