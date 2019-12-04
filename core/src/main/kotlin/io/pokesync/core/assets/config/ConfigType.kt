package io.pokesync.core.assets.config

/**
 * A type of config.
 * @author Sino
 */
enum class ConfigType(val fileStamp: String) {
    WORLD("PSWD"),

    OBJECTS("PSOD"),

    NPCS("PSND"),

    ITEMS("PSID"),

    MONSTERS("PSMD"),

    ANIMATIONS("PSAD")
}