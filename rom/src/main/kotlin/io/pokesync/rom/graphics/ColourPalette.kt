package io.pokesync.rom.graphics

import com.badlogic.gdx.graphics.Color
import com.google.protobuf.ByteString
import io.pokesync.lib.bytes.asReadOnlyByteBuf

/**
 * A palette of [Color]s.
 * @author Sino
 */
data class ColourPalette(val colours: List<Color>) {
    companion object {
        /**
         * Reads a [ColourPalette].
         */
        fun readNitroColourPalette(bytes: ByteString): ColourPalette {
            val iterator = bytes.asReadOnlyByteBuf()
            val colours = mutableListOf<Color>()
            while (iterator.isReadable) {
                val colourPack = iterator.readUnsignedShortLE()

                val red = (colourPack and 31) shl 3
                val green = ((colourPack shr 5) and 31) shl 3
                val blue = ((colourPack shr 10) and 31) shl 3

                colours.add(Color((255 shl 24) or (blue shl 16) or (green shl 8) or red))
            }

            return ColourPalette(colours)
        }
    }
}