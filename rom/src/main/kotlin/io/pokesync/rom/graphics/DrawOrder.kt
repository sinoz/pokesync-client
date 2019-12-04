package io.pokesync.rom.graphics

/**
 * The order in which pixels are drawn.
 * @author Sino
 */
sealed class DrawOrder {
    companion object {
        object Linear : DrawOrder()
        object Horizontal : DrawOrder()
    }
}