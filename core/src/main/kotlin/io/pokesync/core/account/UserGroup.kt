package io.pokesync.core.account

/**
 * A group of users a user may belong to.
 * @author Sino
 */
enum class UserGroup {
    REGULAR,

    PATRON,

    MODERATOR,

    ADMINISTRATOR,

    GAME_DESIGNER,

    WEB_DEVELOPER,

    GAME_DEVELOPER;

    companion object {
        fun fromId(value: Int): UserGroup =
            when (value) {
                0 -> REGULAR
                1 -> PATRON
                2 -> MODERATOR
                3 -> ADMINISTRATOR
                4 -> GAME_DESIGNER
                5 -> WEB_DEVELOPER
                6 -> GAME_DEVELOPER
                else -> REGULAR
            }
    }
}