package io.pokesync.core.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.TimeUtils
import java.util.concurrent.TimeUnit

/**
 * Profiles the heap usage.
 * @author Sino
 */
class HeapProfiler {
    private var sinceChange: Long = 0

    private var lastTimeCounted: Long = 0

    private var heapInBytes: Long = 0

    /**
     * Updates the amount of time that has passed since the last update
     * and checks if the [heapInBytes] needs a refresh or not.
     */
    fun update() {
        val delta = TimeUtils.timeSinceMillis(lastTimeCounted)
        lastTimeCounted = TimeUtils.millis()

        sinceChange += delta
        if (sinceChange >= ONE_SECOND_IN_MS) {
            sinceChange = 0
            heapInBytes = Gdx.app.javaHeap
        }
    }

    /**
     * Returns the amount of bytes of heap that is being used.
     */
    fun getHeapInBytes(): Long {
        return heapInBytes
    }

    companion object {
        val ONE_SECOND_IN_MS = TimeUnit.SECONDS.toMillis(1)
    }
}