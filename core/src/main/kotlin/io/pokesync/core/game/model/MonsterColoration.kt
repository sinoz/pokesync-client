package io.pokesync.core.game.model

/**
 * A type of coloration of a monster, which is used to visualize
 * whether a monster is shiny or not.
 * @author Sino
 */
enum class MonsterColoration {
    REGULAR, SHINY;

    companion object {
        fun fromId(value: Int): MonsterColoration =
            when (value) {
                0 -> REGULAR
                1 -> SHINY
                else -> throw IllegalArgumentException()
            }
    }
}