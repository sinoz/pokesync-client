package io.pokesync.core.assets

/**
 * A type of text label bank.
 */
enum class TextBankType(val nitroFileId: Int) {
    NATURES(nitroFileId = 34),
    STATUS_CONDITIONS(nitroFileId = 44),
    ITEM_NAMES(nitroFileId = 222),
    LOCATION_NAMES(nitroFileId = 279),
    MOVE_TYPES(nitroFileId = 735),
    MOVE_NAMES(nitroFileId = 751),
    MONSTER_DESCRIPTIONS(nitroFileId = 803),
    MONSTER_NAMES(nitroFileId = 820),
    MONSTER_TITLES(nitroFileId = 823)
}