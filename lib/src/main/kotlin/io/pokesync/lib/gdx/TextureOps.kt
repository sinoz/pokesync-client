package io.pokesync.lib.gdx

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Slices the [Texture] horizontally into separate [TextureRegion]s.
 */
fun Texture.horizontalSheetSlice(): List<TextureRegion> {
    val regions = mutableListOf<TextureRegion>()
    for (x in 0 until width / height) {
        regions.add(TextureRegion(this, x * height, 0, height, height))
    }

    return regions
}

/**
 * Slices the [Texture] vertically into separate [TextureRegion]s.
 */
fun Texture.verticalSheetSlice(): List<TextureRegion> {
    val regions = mutableListOf<TextureRegion>()
    for (y in 0 until height / width) {
        regions.add(TextureRegion(this, 0, y * width, width, width))
    }

    return regions
}