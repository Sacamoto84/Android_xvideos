package com.client.common.videoplayer.extension

import com.client.common.videoplayer.util.formatMinSec

fun Int.formatMinSec(): String {
    return formatMinSec(this)
}
