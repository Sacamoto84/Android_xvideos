package com.client.xvideos.common.applock

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AppLockSessionTest {

    @Before
    fun setUp() {
        AppLockSession.onAppForeground(nowMs = 0L) // сбрасываем отметку ухода в фон
        AppLockSession.unlock()
    }

    @Test
    fun `short background keeps session unlocked`() {
        AppLockSession.onAppBackground(nowMs = 1_000L)
        AppLockSession.onAppForeground(nowMs = 1_000L + AppLockSession.RELOCK_TIMEOUT_MS - 1)
        assertTrue(AppLockSession.isUnlocked())
    }

    @Test
    fun `long background locks session`() {
        AppLockSession.onAppBackground(nowMs = 1_000L)
        AppLockSession.onAppForeground(nowMs = 1_000L + AppLockSession.RELOCK_TIMEOUT_MS)
        assertFalse(AppLockSession.isUnlocked())
    }

    @Test
    fun `foreground without background does not lock`() {
        AppLockSession.onAppForeground(nowMs = 10 * AppLockSession.RELOCK_TIMEOUT_MS)
        assertTrue(AppLockSession.isUnlocked())
    }
}
