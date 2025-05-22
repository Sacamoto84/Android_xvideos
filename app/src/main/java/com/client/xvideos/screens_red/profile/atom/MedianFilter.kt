package com.client.xvideos.screens_red.profile.atom

class MedianFilter(private val windowSize: Int) {
    private val window = mutableListOf<Float>()

    init {
        require(windowSize > 0 && windowSize % 2 != 0) { "Window size must be a positive odd number." }
    }

    fun addValue(value: Float): Float {
        window.add(value)
        if (window.size > windowSize) {
            window.removeAt(0) // Удаляем самое старое значение, чтобы окно не росло бесконечно
        }

        // Если окно еще не заполнено, возвращаем текущее значение или среднее,
        // но для простоты здесь вернем текущее, пока окно не заполнится.
        // Более сложная логика может заполнять начальные значения.
        if (window.size < windowSize && window.isNotEmpty()) {
            // Чтобы фильтр начал работать быстрее, можно возвращать текущее значение, пока окно не наполнится
            return window.last()
            // Или, для более плавного старта, но с задержкой, пока окно не заполнится:
            // return if (window.size == 1) window.first() else calculateMedian() // если нужно возвращать что-то, пока окно не полное
        }

        return calculateMedian()
    }

    private fun calculateMedian(): Float {
        if (window.isEmpty()) return 0f // Или другое значение по умолчанию
        val sortedWindow = window.sorted()
        return sortedWindow[sortedWindow.size / 2]
    }

    fun clear() {
        window.clear()
    }
}