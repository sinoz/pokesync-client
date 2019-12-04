package io.pokesync.rom.nitro

/**
 * A type of nitro archive.
 * @author Sino
 */
enum class ArchiveType(val nitroId: Int) {
    /**
     * The archive of front-and back sprites for monsters.
     */
    MONSTERS(133),

    /**
     * The archive of front-and back sprites for bag items.
     */
    BAG_ITEMS(147),

    /**
     * The archive of text labels.
     */
    TEXT_LABELS (156),

    /**
     * The archive that contains all of the map matrices.
     */
    MAP_MATRICES (170),

    /**
     * The archive that contains all of the tilesets.
     */
    TILESETS (173),

    /**
     * The archive of trainer front-and back sprites.
     */
    TRAINERS(187),

    /**
     * The archive that contains all of the map structures.
     */
    MAP_STRUCTS (194),

    /**
     * The archive of overworld entity sprites.
     */
    OVERWORLD_ENTITIES(210);
}