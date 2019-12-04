package io.pokesync.rom

/**
 * A type of game release.
 * @author Sino
 */
enum class GameCode(val romId: String) {
    SOULSILVER ("ipge"),

    HEARTGOLD ("ipke");

    companion object {
        @JvmStatic
        internal fun fromGameCode(code: String): GameCode? {
            for (gameCode in values()) {
                if (gameCode.romId == code.toLowerCase()) {
                    return gameCode
                }
            }

            return null
        }
    }
}