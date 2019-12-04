package io.pokesync.core.game.ui.element.bag

/**
 * A type of bag pocket.
 * @author Sino
 */
enum class PocketType(val styleName: String) {
    MISC("misc"),

    MEDICINE("medicine"),

    MONSTER_BALLS("pokeballs"),

    TECHNICAL_MACHINES("tms"),

    BERRIES("berries"),

    MAILS("mails"),

    BATTLE("battle"),

    KEYS("keys"),

    CUSTOMIZATION("customization")
}