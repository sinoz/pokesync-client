package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component

/**
 * A component that suggests that an entity has the ability to jump.
 * @author Sino
 */
class CanJump : Component {
    /**
     * The current stage of the jumping process.
     */
    private var stage = 0

    /**
     * Returns the current stage.
     */
    fun getStage(): Int =
        stage

    /**
     * Updates the stage.
     */
    fun proceedToStage(next: Int) {
        stage = next
    }

    /**
     * Clears the stage.
     */
    fun resetStage() {
        stage = 0
    }

    companion object {
        /**
         * The stage of jumping up into the air.
         */
        const val JUMP_UP = 0

        /**
         * The stage of falling down after jumping up.
         */
        const val FALL_DOWN = 1
    }
}