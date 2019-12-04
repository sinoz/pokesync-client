package io.pokesync.rom.nitro

/**
 * A type of Nitro file extension.
 * @author Sino
 */
enum class FileExtension(val nitroId: String) {
    FAT ("BTAF"),
    FILE_SYSTEM ("BTNF"),
    ARCHIVE ("NARC"),
    TEXTURE ("BTX0"),
    FRAME ("TEX0"),
    GRAPHIC ("RGCN"),
    COLOURS ("RLCN"),
    PALETTE ("PLTT"),
    CHARACTER ("CHAR")
}