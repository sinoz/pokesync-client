package io.pokesync.core.game.model

/**
 * A type of gender.
 * @author Sino
 */
enum class Gender {
    MALE, FEMALE;

    companion object {
        /**
         * Translates the given [id] to a [Gender].
         */
        fun fromId(id: Int): Gender? =
            when (id) {
                0 -> MALE
                1 -> FEMALE
                else -> null
            }
    }
}