package io.pokesync.core.login.lobby

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * The state of the queue.
 * @author Sino
 */
class QueueState(skin: Skin) : Label("", skin, "queue-state") {
    private var currentSpot = 0

    private var queueSize = 0

    init {
        refresh()
    }

    fun setCurrentSpot(value: Int) {
        currentSpot = value
        refresh()
    }

    fun setQueueSize(value: Int) {
        queueSize = value
        refresh()
    }

    /**
     * Refreshes this label.
     */
    private fun refresh() {
        setText("$currentSpot/$queueSize")
    }
}