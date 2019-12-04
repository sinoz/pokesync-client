package io.pokesync.core.game.world.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.pokesync.core.assets.texture.ShadowTexture
import ktx.ashley.mapperFor

/**
 * A shadow sprite to draw underneath entities when outdoors.
 * @author Sino
 */
class Shadow private constructor(region: TextureRegion) : Sprite(region), Component {
    companion object {
        val MAPPER = mapperFor<Shadow>()

        fun create(worldX: Int, worldZ: Int, shadowTexture: ShadowTexture): Shadow {
            val defaultFrame = shadowTexture.region

            val sprite = Sprite(defaultFrame)
            sprite.setPosition(
                worldX * defaultFrame.regionWidth.toFloat(),
                worldZ * defaultFrame.regionHeight.toFloat()
            )

            return Shadow(sprite)
        }
    }
}