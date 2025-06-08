package com.client.xvideos.screens_red.common.expand_menu_video

import androidx.compose.ui.graphics.vector.ImageVector
import com.client.xvideos.feature.redgifs.types.GifsInfo

data class ExpandMenuVideoModel(val text : String = "", val leadingIconVector : ImageVector? = null, val onClick :(GifsInfo?)->Unit ={})
