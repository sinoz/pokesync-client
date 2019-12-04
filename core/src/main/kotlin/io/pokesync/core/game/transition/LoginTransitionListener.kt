package io.pokesync.core.game.transition

import io.pokesync.core.ScreenStack
import io.pokesync.lib.gdx.onRenderingThread

/**
 * Listens for a transition call from the game screen back to
 * the login screen.
 * @author Sino
 */
interface LoginTransitionListener {
    /**
     * Transitions into the login screen.
     */
    suspend fun transition()

    companion object {
        /**
         * A default implementation of a [LoginTransitionListener].
         */
        fun impl(screenStack: ScreenStack): LoginTransitionListener =
            object : LoginTransitionListener {
                override suspend fun transition() {
                    // ensuring that the screen mutation doesn't happen in a separate thread
                    onRenderingThread {
                        screenStack.pop()
                    }
                }
            }
    }
}