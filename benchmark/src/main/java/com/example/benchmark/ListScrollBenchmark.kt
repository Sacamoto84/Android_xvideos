package com.example.benchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMacrobenchmarkApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

class ListScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMacrobenchmarkApi::class)
    @Test
    fun scrollLazyColumnAfterNavigation() = benchmarkRule.measureRepeated(
        packageName = "com.client.xvideos",  // Ваш пакет
        metrics = listOf(FrameTimingMetric()),  // Можно добавить PowerMetric и др.
        iterations = 10,
        compilationMode = CompilationMode.Ignore()  // Или Partial для реальных условий
    ) {
        // setupBlock: Навигация до экрана с LazyColumn (не измеряется)
        navigateToListScreen()

//        // measureBlock: Только скролл — здесь фиксируются метрики
//        val lazyColumn = device.findObject(By.res(packageName, "lazy_column_tag"))  // Добавьте testTag на LazyColumn
//        lazyColumn.fling(Direction.DOWN)  // Быстрый скролл вниз
//        //device.wait(Until.scrollFinished(lazyColumn), 5000)  // Ждем окончания
//        lazyColumn.fling(Direction.UP)    // И обратно, если нужно
    }

    private fun MacrobenchmarkScope.navigateToListScreen() {
        // Запуск главной активности (если нужно с home)
        pressHome()
        startActivityAndWait()
//        val intent = Intent()
//        intent.setPackage(packageName)
//        intent.action = Intent.ACTION_MAIN  // Или ваш лаунчер action
//        intent.addCategory(Intent.CATEGORY_LAUNCHER)
//        startActivityAndWait(intent)

        val buttonPermission = device.findObject(By.res("bPermission"))
        if (buttonPermission != null) {
            buttonPermission.click()

            // Ждем появления системного экрана настроек (пакет settings)
            device.wait(Until.hasObject(By.pkg("com.android.settings")), 10000)

            // Ищем переключатель по тексту (может зависеть от языка устройства)
            // Вариант 1: Русский текст
            var toggle = device.findObject(By.text("Разрешить управление всеми файлами"))
            // Вариант 2: Если на английском (или для надежности)
            if (toggle == null) {
                toggle = device.findObject(By.text("Allow access to manage all files"))
            }
            // Вариант 3: Более надежный — по resource-id (стандартный в AOSP)
            if (toggle == null) {
                toggle = device.findObject(By.res("com.android.settings", "switch_widget"))  // Или "switch_bar"
            }
            // Вариант 4: Если текст не найден — ищем clickable switch в центре экрана
            if (toggle == null) {
                val switches = device.findObjects(By.clazz("android.widget.Switch"))
                if (switches.isNotEmpty()) {
                    toggle = switches[0]  // Обычно первый — нужный
                }
            }

            if (toggle != null && !toggle.isChecked) {
                toggle.click()
            }

            // Возврат назад в приложение (нажать Back)
            device.pressBack()

            // Ждем возврата в ваше приложение
            device.wait(Until.hasObject(By.pkg(packageName)), 10000)
        }

        // Шаг 1: Клик по кнопке/элементу на первом экране
        val buttonToCategories = device.findObject(By.res("buttonL"))//(By.text("Категории"))  // Или By.res("id_categories") buttonL
        buttonToCategories.click()

        val bookmarkTab = device.findObject(By.res("bBookMark"))
        bookmarkTab.click()

//        // Ждем появления следующего экрана
//        device.wait(Until.hasObject(By.res(packageName, "categories_screen_tag")), 10000)
//
//        // Шаг 2: Выбор подкатегории
//        val itemElectronics = device.findObject(By.text("Электроника"))
//        itemElectronics.click()
//
//        // Ждем списка
//        device.wait(Until.hasObject(By.res(packageName, "lazy_column_tag")), 10000)
    }
}