package io.pokesync.core.game.model

/**
 * The different types of movement.
 * @author Sino
 */
enum class MovementType {
    WALK, RUN, CYCLE, JUMP, SURF, DIVE, GLIDE, TELEPORT;

    companion object {
        /**
         * Translates the given [Int] value to a [MovementType].
         */
        fun fromId(value: Int): MovementType =
            when (value) {
                0 -> WALK
                1 -> RUN
                2 -> CYCLE
                else -> throw IllegalArgumentException()
            }

        /**
         * Translates the given [MovementType] to an [Int] value.
         */
        fun toId(movementType: MovementType): Int =
            when (movementType) {
                WALK -> 0
                RUN -> 1
                CYCLE -> 2
                else -> throw IllegalArgumentException()
            }
    }
}