package io.pokesync.core.game.model

/**
 * A status condition of a monster.
 * @author Sino
 */
enum class StatusCondition {
    PARALYZED, POISONED, ASLEEP, FROZEN, BURNT;

    companion object {
        /**
         * Translates the given [id] to a [StatusCondition].
         */
        fun fromId(id: Int): StatusCondition? =
            when (id) {
                1 -> PARALYZED
                2 -> POISONED
                3 -> ASLEEP
                4 -> FROZEN
                5 -> BURNT
                else -> null
            }
    }
}