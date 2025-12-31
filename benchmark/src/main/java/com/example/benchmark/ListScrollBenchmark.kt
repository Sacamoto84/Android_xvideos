package com.example.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMacrobenchmarkApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

//class ListScrollBenchmark {
//
//    @get:Rule
//    val benchmarkRule = MacrobenchmarkRule()
//
//    companion object {
//        private const val PACKAGE_NAME = "com.client.xvideos"
//
//        @JvmStatic
//        lateinit var device: UiDevice
//
//        @BeforeClass
//        @JvmStatic
//        fun globalSetup() {
//            device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//
//            // Автоматически подтверждаем установку если появляется
//            handleInstallDialog()
//        }
//
//        private fun handleInstallDialog() {
//            // Пробуем найти кнопку установки (английская версия)
//            //device.wait(Until.findObject(By.text("Install")), 3000)?.click()
//
//            // Русская версия
//            device.wait(Until.findObject(By.text("Установить")), 15000)?.click()
//
////            // Или по resource id
////            device.wait(
////                Until.findObject(By.res("com.google.android.packageinstaller:id/ok_button")),
////                3000
////            )?.click()
//
//            device.waitForIdle(1000)
//        }
//    }
//
//    @OptIn(ExperimentalMacrobenchmarkApi::class)
//    @Test
//    fun scrollLikesGridAfterNavigation() = benchmarkRule.measureRepeated(
//        packageName = PACKAGE_NAME,
//        metrics = listOf(FrameTimingMetric()),
//        iterations = 5,
//        startupMode = StartupMode.WARM,
//        compilationMode = CompilationMode.Partial(),
//        setupBlock = {
//            // На всякий случай проверяем диалог установки ещё раз
//            //handleInstallDialog()
//
//            navigateToLikesScreen()
//            device.waitForIdle(2000)
//        }
//    ) {
//        val grid =
//            device.findObject(By.res("lLikes")) ?: throw IllegalStateException("Grid not found")
//
//        grid.setGestureMargin(device.displayWidth / 5)
//
//        repeat(5) {
////            grid.scroll(Direction.DOWN, 99.6f)
////            device.waitForIdle()
////            Thread.sleep(800)
////
////            grid.scroll(Direction.UP, 99.6f)
////            device.waitForIdle()
////            Thread.sleep(800)
//
//            // Скролл вниз с небольшой паузой
//
//
//            grid.fling(Direction.DOWN)
//            device.waitForIdle() // Даём отрисоваться кадрам
//
//            // Небольшая пауза между скроллами
//            Thread.sleep(500)
//
//            // Скролл вверх
//            grid.fling(Direction.UP)
//            device.waitForIdle()
//            Thread.sleep(500)
//        }
//
//        device.waitForIdle(1000)
//    }
//
//    private fun MacrobenchmarkScope.navigateToLikesScreen() {
//        pressHome()
//        Thread.sleep(500)
//
//        startActivityAndWait()
//        device.waitForIdle(1000)
//
//        // Обработка permission
//        device.findObject(By.res("bPermission"))?.let { button ->
//            button.click()
//            device.wait(Until.hasObject(By.pkg("com.android.settings")), 10000)
//
//            val toggle = device.findObject(By.text("Разрешить управление всеми файлами"))
//                ?: device.findObject(By.text("Allow access to manage all files"))
//                ?: device.findObject(By.res("com.android.settings:id/switch_widget"))
//                ?: device.findObjects(By.clazz("android.widget.Switch")).firstOrNull()
//
//            if (toggle != null && !toggle.isChecked) {
//                toggle.click()
//                Thread.sleep(500)
//            }
//
//            device.pressBack()
//            device.wait(Until.hasObject(By.pkg(packageName)), 10000)
//        }
//
//        // Навигация
//        val buttonL = device.waitForObject(By.res("buttonL"), 5000)
//        require(buttonL != null) { "buttonL not found" }
//        buttonL.click()
//        device.waitForIdle(1000)
//
//        val bookmarkTab = device.waitForObject(By.res("bBookMark"), 5000)
//        require(bookmarkTab != null) { "bBookMark not found" }
//        bookmarkTab.click()
//        device.waitForIdle(1000)
//
//        val grid = device.waitForObject(By.res("lLikes"), 15000)
//        require(grid != null) { "lLikes grid not found after navigation" }
//    }
//
//    private fun UiDevice.waitForObject(selector: BySelector, timeoutMs: Long = 10000): UiObject2? {
//        val deadline = System.currentTimeMillis() + timeoutMs
//        while (System.currentTimeMillis() < deadline) {
//            findObject(selector)?.let { return it }
//            waitForIdle(500)
//        }
//        return null
//    }
//}

class ListScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMacrobenchmarkApi::class)
    @Test
    fun scrollLikesGridAfterNavigation() = benchmarkRule.measureRepeated(
        packageName = "com.client.xvideos",
        metrics = listOf(FrameTimingMetric()),
        iterations = 3, // Уменьшил для начала
        startupMode = StartupMode.WARM, // Важно! Используем WARM режим
        compilationMode = CompilationMode.Partial(), // Partial лучше для фрейм-тайминга
        setupBlock = {
            // ВСЁ, что происходит здесь - НЕ измеряется
            navigateToLikesScreen()

            // Дайте UI стабилизироваться после навигации
            device.waitForIdle(1000)
        }
    ) {
        // ВСЁ, что происходит ЗДЕСЬ - измеряется FrameTimingMetric

        // Найдём грид (он уже должен быть на экране после setupBlock)
        val grid = device.findObject(By.res("lLikes"))
            ?: throw IllegalStateException("Grid 'lLikes' not found in measure block")

        // Выполняем скроллы - именно здесь собираются метрики кадров
        repeat(5) {
            // Скролл вниз с небольшой паузой
            grid.setGestureMargin(device.displayWidth / 5)
            repeat(5) {
                grid.fling(Direction.DOWN)
                device.waitForIdle() // Даём отрисоваться кадрам
            }
            repeat(5) {
                grid.fling(Direction.UP)
                device.waitForIdle()
            }
        }
    }

    /**
     * Навигация до экрана с гридом закладок
     * Вызывается в setupBlock - не влияет на метрики
     */
    private fun MacrobenchmarkScope.navigateToLikesScreen() {
        pressHome()
        startActivityAndWait()

        // Обработка разрешения на файлы
        device.findObject(By.res("bPermission"))?.let { button ->
            button.click()
            device.wait(Until.hasObject(By.pkg("com.android.settings")), 10000)

            val toggle = device.findObject(By.text("Разрешить управление всеми файлами"))
                ?: device.findObject(By.text("Allow access to manage all files"))
                ?: device.findObject(By.res("com.android.settings:id/switch_widget"))
                ?: device.findObjects(By.clazz("android.widget.Switch")).firstOrNull()

            if (toggle != null && !toggle.isChecked) {
                toggle.click()
            }

            device.pressBack()
            device.wait(Until.hasObject(By.pkg(packageName)), 10000)
        }

        // Навигация к экрану закладок
        device.waitForObject(By.res("buttonL"), 5000)?.click()
            ?: throw IllegalStateException("buttonL not found")

        device.waitForObject(By.res("bBookMark"), 5000)?.click()
            ?: throw IllegalStateException("bBookMark not found")

        // Ждём появления грида
        device.waitForObject(By.res("lLikes"), 15000)
            ?: throw IllegalStateException("lLikes grid not found after navigation")
    }

    private fun UiDevice.waitForObject(selector: BySelector, timeoutMs: Long = 10000): UiObject2? {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            findObject(selector)?.let { return it }
            waitForIdle(500)
        }
        return null
    }
}