package com.client.xvideos.redgifs.common.di

import com.client.xvideos.common.connectivityObserver.ConnectivityObserver
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.common.downloader.DownloadRed
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.common.search.R_SearchNiches
import com.client.xvideos.redgifs.common.search.R_SearchExplorer
import com.client.xvideos.redgifs.network.api.RedApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Контейнер общих зависимостей R-раздела.
 *
 * Используется главным образом в [com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123Host]
 * и фабрике `createPager`, которым нужно одновременно несколько компонентов
 * (`block`, `redApi`, `savedRed`, `search`, `searchNiches`). Передавать всю
 * пятёрку отдельными параметрами слишком громоздко, поэтому они собраны здесь.
 *
 * Анти-паттерн: использовать `HostDI` как сервис-локатор внутри ScreenModel,
 * которым нужна только одна-две конкретные зависимости. В таких случаях
 * предпочтительно инжектить нужное напрямую (`SavedRed`, `DownloadRed`, …).
 */
@Singleton
class HostDI @Inject constructor (
    val connectivityObserver: ConnectivityObserver,
    val block: BlockRed,
    val redApi : RedApi,
    val savedRed: SavedRed,
    val downloadRed: DownloadRed,
    val search : R_SearchExplorer,
    val searchNiches : R_SearchNiches,
)
