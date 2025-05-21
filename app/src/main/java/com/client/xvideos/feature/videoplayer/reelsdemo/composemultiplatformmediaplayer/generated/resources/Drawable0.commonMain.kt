@file:OptIn(InternalResourceApi::class)

package com.client.xvideos.feature.videoplayer.reelsdemo.composemultiplatformmediaplayer.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceItem

private object CommonMainDrawable0 {
  public val resize_fill: DrawableResource by 
      lazy { init_resize_fill() }

  public val resize_fit: DrawableResource by 
      lazy { init_resize_fit() }
}

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("resize_fill", CommonMainDrawable0.resize_fill)
  map.put("resize_fit", CommonMainDrawable0.resize_fit)
}

internal val Res.drawable.resize_fill: DrawableResource
  get() = CommonMainDrawable0.resize_fill

private fun init_resize_fill(): DrawableResource = DrawableResource(
  "drawable:resize_fill",
    setOf(
      ResourceItem(setOf(),
    "composeResources/reelsdemo.composemultiplatformmediaplayer.generated.resources/drawable/resize_fill.png", -1, -1),
    )
)

internal val Res.drawable.resize_fit: DrawableResource
  get() = CommonMainDrawable0.resize_fit

private fun init_resize_fit(): DrawableResource = DrawableResource(
  "drawable:resize_fit",
    setOf(
      ResourceItem(setOf(),
    "composeResources/reelsdemo.composemultiplatformmediaplayer.generated.resources/drawable/resize_fit.png", -1, -1),
    )
)
