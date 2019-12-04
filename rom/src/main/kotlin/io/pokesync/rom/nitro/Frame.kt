package io.pokesync.rom.nitro

import com.google.protobuf.ByteString
import io.pokesync.rom.graphics.ColourFormat
import io.pokesync.rom.graphics.DrawOrder

/**
 * A single frame of a sprite image.
 * @author Sino
 */
class Frame(
    val width: Int,
    val height: Int,
    val vramMode: Int,
    val colourFormat: ColourFormat,
    val drawOrder: DrawOrder,
    val bytes: ByteString
)