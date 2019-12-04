package io.pokesync.lib.gdx

import com.badlogic.gdx.graphics.Pixmap

/**
 * Constructs a slice of this [Pixmap].
 */
fun Pixmap.slice(xOffset: Int, yOffset: Int, width: Int, height: Int): Pixmap {
    val region = Pixmap(width, height, format)

    for (x in 0 until width) {
        for (y in 0 until height) {
            region.drawPixel(x, y, getPixel(xOffset + x, yOffset + y))
        }
    }

    return region
}