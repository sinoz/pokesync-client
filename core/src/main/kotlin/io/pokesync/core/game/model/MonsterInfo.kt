package io.pokesync.core.game.model

/**
 * Contains information about a monster.
 * @author Sino
 */
data class MonsterInfo(
    val id: Int,
    val name: String,
    val title: String,
    val primaryType: Type,
    val secondaryType: Type,
    val description: String
) {
    /**
     * A type of a monster.
     */
    enum class Type {
        FIRE, GRASS, WATER, ROCK, STEEL, GROUND, POISON, ELECTRIC, PSYCHIC, DARK, ICE, FLYING
    }
}