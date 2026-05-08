package com.client.xvideos.common.videoplayer.util

object VideoCacheSettings {
    const val MIN_SIZE_MB = 128
    const val MAX_SIZE_MB = 4_096
    const val STEP_SIZE_MB = 128
    const val DEFAULT_SIZE_MB = 1_024

    private const val BYTES_IN_MB = 1024L * 1024L

    fun normalizedSizeMb(value: Int): Int {
        return value.coerceIn(MIN_SIZE_MB, MAX_SIZE_MB)
    }

    fun maxSizeBytes(value: Int): Long {
        return normalizedSizeMb(value).toLong() * BYTES_IN_MB
    }
}
