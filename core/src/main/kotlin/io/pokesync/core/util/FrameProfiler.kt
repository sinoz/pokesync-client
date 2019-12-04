package io.pokesync.core.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.TimeUtils
import java.util.concurrent.TimeUnit

/**
 * Profiles the frames-per-second.
 * @author Sino
 */
class FrameProfiler {
    private var sinceChange: Long = 0

    private var lastTimeCounted: Long = 0

    private var frameRate = 0

    /**
     * Updates the amount of time that has passed since the last update
     * and checks if the [frameRate] needs a refresh or not.
     */
    fun update() {
        val delta = TimeUtils.timeSinceMillis(lastTimeCounted)
        lastTimeCounted = TimeUtils.millis()

        sinceChange += delta
        if (sinceChange >= ONE_SECOND_IN_MS) {
            sinceChange = 0
            frameRate = Gdx.graphics.framesPerSecond
        }
    }

    /**
     * Returns the amount of frames per second.
     */
    fun getFrameRate(): Int {
        return frameRate
    }

    companion object {
        val ONE_SECOND_IN_MS = TimeUnit.SECONDS.toMillis(1)
    }
}