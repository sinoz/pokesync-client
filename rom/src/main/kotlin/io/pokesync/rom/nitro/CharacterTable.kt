package io.pokesync.rom.nitro

/**
 * The character table mapping numeric character id's to actual characters.
 */
val CHARACTER_TABLE = mapOf(
    /**
     * ------ SYMBOLS
     */

    Pair(0x0112, "¥"),
    Pair(0x0120, "＆"),
    Pair(0x01AB, "!"),
    Pair(0x01AC, "?"),
    Pair(0x01AE, "."),
    Pair(0x01AD, ","),
    Pair(0x01B2, "‘"),
    Pair(0x01B3, "’"),
    Pair(0x01B4, "“"),
    Pair(0x01B5, "”"),
    Pair(0x01B9, "("),
    Pair(0x01BA, ")"),
    Pair(0x01BD, "+"),
    Pair(0x01BE, "-"),
    Pair(0x01BF, "*"),
    Pair(0x01C0, "#"),
    Pair(0x01C1, "="),
    Pair(0x01C2, "&"),
    Pair(0x01C3, "~"),
    Pair(0x01C4, ":"),
    Pair(0x01C5, ";"),
    Pair(0x01E9, "_"),
    Pair(0x01DE, " "),
    Pair(0xE000, " "),
    Pair(0x01D0, "@"),
    Pair(0x01D2, "%"),

    /**
     * ------ NUMERIC
     */

    Pair(0x0121, "0"),
    Pair(0x0122, "1"),
    Pair(0x0123, "2"),
    Pair(0x0124, "3"),
    Pair(0x0125, "4"),
    Pair(0x0126, "5"),
    Pair(0x0127, "6"),
    Pair(0x0128, "7"),
    Pair(0x0129, "8"),
    Pair(0x012A, "9"),

    /**
     * ------ UPPERCASED ALPHABET
     */

    Pair(0x012B, "A"),
    Pair(0x012C, "B"),
    Pair(0x012D, "C"),
    Pair(0x012E, "D"),
    Pair(0x012F, "E"),
    Pair(0x0130, "F"),
    Pair(0x0131, "G"),
    Pair(0x0132, "H"),
    Pair(0x0133, "I"),
    Pair(0x0134, "J"),
    Pair(0x0135, "K"),
    Pair(0x0136, "L"),
    Pair(0x0137, "M"),
    Pair(0x0138, "N"),
    Pair(0x0139, "O"),
    Pair(0x013A, "P"),
    Pair(0x013B, "Q"),
    Pair(0x013C, "R"),
    Pair(0x013D, "S"),
    Pair(0x013E, "T"),
    Pair(0x013F, "U"),
    Pair(0x0140, "V"),
    Pair(0x0141, "W"),
    Pair(0x0142, "X"),
    Pair(0x0143, "Y"),
    Pair(0x0144, "Z"),

    Pair(0x0168, "É"),

    /**
     * ------ LOWERCASED ALPHABET
     */

    Pair(0x0145, "a"),
    Pair(0x0146, "b"),
    Pair(0x0147, "c"),
    Pair(0x0148, "d"),
    Pair(0x0149, "e"),
    Pair(0x014A, "f"),
    Pair(0x014B, "g"),
    Pair(0x014C, "h"),
    Pair(0x014D, "i"),
    Pair(0x014E, "j"),
    Pair(0x014F, "k"),
    Pair(0x0150, "l"),
    Pair(0x0151, "m"),
    Pair(0x0152, "n"),
    Pair(0x0153, "o"),
    Pair(0x0154, "p"),
    Pair(0x0155, "q"),
    Pair(0x0156, "r"),
    Pair(0x0157, "s"),
    Pair(0x0158, "t"),
    Pair(0x0159, "u"),
    Pair(0x015A, "v"),
    Pair(0x015B, "w"),
    Pair(0x015C, "x"),
    Pair(0x015D, "y"),
    Pair(0x015E, "z"),

    Pair(0x0188, "é")
)