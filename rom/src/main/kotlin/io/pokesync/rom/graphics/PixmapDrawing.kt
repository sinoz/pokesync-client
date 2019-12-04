package io.pokesync.rom.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.google.protobuf.ByteString

/**
 * Draws a [Pixmap] accordingly to the given [DrawOrder].
 */
fun drawPixmap(width: Int, height: Int, drawOrder: DrawOrder, bytes: ByteString, format: ColourFormat, palette: ColourPalette): Pixmap =
    when (drawOrder) {
        is DrawOrder.Companion.Horizontal ->
            drawHorizontalPixmap(width, height, bytes, format, palette)

        is DrawOrder.Companion.Linear ->
            drawLinearPixmap(width, height, bytes, palette)
    }

/**
 * Draws a [Pixmap] horizontally in lines of pixels.
 */
fun drawHorizontalPixmap(width: Int, height: Int, bytes: ByteString, format: ColourFormat, palette: ColourPalette): Pixmap {
    val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)

    for (y in 0 until height) {
        for (x in 0 until width) {
            try {
                when (format) {
                    ColourFormat.A5I3 -> {
                        val tilePixel = bytes.byteAt(x + y * width).toInt() and 0x7
                        val alpha = ((bytes.byteAt(x + y * width).toInt() shr 3) * 8).toFloat()

                        val pickedColour = palette.colours[tilePixel]
                        val colourAsRGBA = Color(pickedColour.r, pickedColour.g, pickedColour.b, alpha)
                        if (tilePixel != 0) {
                            pixmap.drawPixel(x, y, colourAsRGBA.toIntBits())
                        }
                    }

                    else -> {
                        val tilePixel = bytes.byteAt(y * width + x).toInt() and 0xFF
                        val colour = palette.colours[tilePixel]
                        if (tilePixel != 0) {
                            pixmap.drawPixel(x, y, colour.toIntBits())
                        }
                    }
                }
            } catch (e: Throwable) {}
        }
    }

    return pixmap
}

/**
 * Draws a [Pixmap] linearly in blocks of pixels.
 */
fun drawLinearPixmap(width: Int, height: Int, bytes: ByteString, palette: ColourPalette): Pixmap {
    val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)

    var xCoord = -1
    var yCoord = 0

    var blockX = 0
    var blockY = 0

    val size = bytes.size() shl 1
    var index = 0

    while (index < size) {
        xCoord++
        if (xCoord >= 8) {
            xCoord = 0
            yCoord++
        }

        if (yCoord >= 8) {
            yCoord = 0
            blockX++
        }

        if (blockX > ((width / 8) - 1)) {
            blockX = 0
            blockY++
        }

        var colour = bytes.byteAt(index shr 1).toInt() and 0xFF
        if ((index and 1) == 0) {
            colour = colour and 0xF
        } else {
            colour = (colour and 0xF0) shr 4
        }

        val x = xCoord + (blockX * 8)
        val y = yCoord + (blockY * 8)

        val pixelColour = palette.colours[colour]
        if (colour != 0) {
            pixmap.drawPixel(x, y, pixelColour.toIntBits())
        }

        index++
    }

    return pixmap
}