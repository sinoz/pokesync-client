package io.pokesync.core.game.model

import com.badlogic.gdx.Input

/**
 * A compass direction.
 * @author Sino
 */
enum class Direction {
    NORTH, SOUTH, EAST, WEST;

    companion object {
        /**
         * Creates a [Direction] from the given [Int] value.
         */
        fun fromId(value: Int): Direction =
            when (value) {
                0 -> SOUTH
                1 -> NORTH
                2 -> WEST
                3 -> EAST
                else -> throw Exception()
            }

        /**
         * Returns an id for the given [Direction].
         */
        fun toId(direction: Direction): Int =
            when (direction) {
                SOUTH -> 0
                NORTH -> 1
                WEST -> 2
                EAST -> 3
            }

        /**
         * Returns the [Direction] that is associated with the given key code.
         */
        fun getByKeyCode(keyCode: Int): Direction? =
            when (keyCode) {
                Input.Keys.LEFT -> WEST
                Input.Keys.DOWN -> SOUTH
                Input.Keys.RIGHT -> EAST
                Input.Keys.UP -> NORTH
                else -> null
            }
    }
}