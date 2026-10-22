package com.client.xvideos.common.applock

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Состояние разблокировки на время жизни процесса.
 *
 * Хранится только в памяти: после смерти процесса приложение снова заблокировано,
 * независимо от того, что восстановил `savedInstanceState`.
 */
object AppLockSession {

    /** Через сколько миллисекунд в фоне приложение снова блокируется. */
    const val RELOCK_TIMEOUT_MS = 30_000L

    private val _unlocked = MutableStateFlow(false)
    val unlocked: StateFlow<Boolean> = _unlocked.asStateFlow()

    /** `elapsedRealtime()` момента ухода в фон, 0 — приложение на переднем плане. */
    @Volatile
    private var backgroundSinceMs = 0L

    fun isUnlocked(): Boolean = _unlocked.value

    fun unlock() {
        _unlocked.value = true
    }

    fun lock() {
        _unlocked.value = false
    }

    /** Всё приложение ушло в фон (ProcessLifecycleOwner ON_STOP). */
    fun onAppBackground(nowMs: Long = SystemClock.elapsedRealtime()) {
        backgroundSinceMs = nowMs
    }

    /**
     * Приложение вернулось на передний план (ProcessLifecycleOwner ON_START).
     * Блокирует, если оно пробыло в фоне не меньше [RELOCK_TIMEOUT_MS]. Короткие
     * уходы (выбор файла, шаринг) замок не включают.
     */
    fun onAppForeground(nowMs: Long = SystemClock.elapsedRealtime()) {
        val since = backgroundSinceMs
        backgroundSinceMs = 0L
        if (since != 0L && nowMs - since >= RELOCK_TIMEOUT_MS) {
            lock()
        }
    }
}
