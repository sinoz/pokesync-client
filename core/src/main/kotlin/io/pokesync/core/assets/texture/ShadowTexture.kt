package io.pokesync.core.assets.texture

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * A shadow texture.
 * @author Sino
 */
class ShadowTexture(val region: TextureRegion) {
    companion object {
        /**
         * Creates a [ShadowTexture] from the given [TextureList].
         */
        fun fromList(textureList: TextureList): ShadowTexture {
            val pixmap = textureList.list[0]
            val texture = Texture(pixmap)

            return ShadowTexture(TextureRegion(texture))
        }
    }
}