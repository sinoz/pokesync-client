package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component

/**
 * The kind of the entity.
 * @author Sino
 */
enum class Kind : Component {
    PLAYER, NPC, OBJECT, MONSTER;

    companion object {
        /**
         * Translates the given [Kind] to an [Int] value.
         */
        fun toId(kind: Kind): Int =
            when (kind) {
                PLAYER -> 0
                NPC -> 1
                OBJECT -> 2
                MONSTER -> 3
            }

        /**
         * Translates the given [Int] value to a [Kind].
         */
        fun fromId(value: Int) =
            when (value) {
                0 -> PLAYER
                1 -> NPC
                2 -> OBJECT
                3 -> MONSTER
                else -> throw Exception()
            }
    }
}