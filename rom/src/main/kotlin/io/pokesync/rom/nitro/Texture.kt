package io.pokesync.rom.nitro

import io.pokesync.rom.graphics.ColourPalette

/**
 * A texture contains a collection of [Frame]s and [ColourPalette]s.
 * @author Sino
 */
class Texture(
    val frames: List<Frame>,
    val frameLabels: List<String>,
    val palettes: List<ColourPalette>,
    val paletteLabels: List<String>
)