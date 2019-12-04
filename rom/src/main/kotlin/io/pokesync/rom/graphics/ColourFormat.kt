package io.pokesync.rom.graphics

/**
 * A kind of colour format.
 * @author Sino
 */
enum class ColourFormat(val bits: Int) {
    /**
     * 2 bits for 4 different colours within a single palette.
     */
    BIT2 (2),

    /**
     * 4 bits for 16 different colours within a single palette.
     */
    BIT4 (4),

    /**
     * 6 bits for 64 different colours within a single palette.
     */
    A5I3 (6),

    /**
     * 8 bits for 256 different colours within a single palette.
     */
    BIT8 (8);

    companion object {
        /**
         * Returns a [ColourFormat] by the given ordinal returned by Nitro.
         */
        fun getByNitroOrdinal(ordinal: Int): ColourFormat? =
            when (ordinal) {
                2 -> BIT2
                3 -> BIT4
                6 -> A5I3
                4 -> BIT8
                else -> throw IllegalArgumentException("Unsupported colour format of $ordinal")
            }
    }

    /**
     * Returns the amount of colours the [ColourFormat] consists of.
     */
    fun colourCount(): Int =
        1 shl bits
}