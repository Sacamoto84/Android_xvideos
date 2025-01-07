package com.client.xvideos.screens.dashboards

import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.common.bottomKeyboard.BottomListDashBoardNavigationButtons2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScreenDashBoards : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        //val vm = getScreenModel<ScreenDashBoardsScreenModel>()
        val vm: ScreenDashBoardsScreenModel = getScreenModel()
        //val pagerState = rememberPagerState(1) { 20000 }
        // ...
        Scaffold(
            topBar = {
//                ComposeCountry(
//                    modifier =Modifier,
//                    onClick = {}
//                )
            }
            ,
            bottomBar = {
                val job = rememberCoroutineScope()
                BottomListDashBoardNavigationButtons2(
                    vm.pagerState.currentPage,
                    onChange = {
                        job.launch(Dispatchers.Main) {
                            vm.pagerState.scrollToPage((it).coerceAtLeast(0))
                        }
                    },
                    max = vm.pagerState.pageCount,
                )
            },
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Black,
        ) { innerPadding ->

            HorizontalPager(
                state = vm.pagerState,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                beyondViewportPageCount = 1,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = vm.pagerState,
                    snapPositionalThreshold = 0.15f,
                    snapAnimationSpec = spring(
                        stiffness = 600f,
                        visibilityThreshold = Int.VisibilityThreshold.toFloat()
                    )
                )
            ) { pageIndex ->
                DashboardsPaginatedListScreen(pageIndex, vm)
            }

        }
    }
}