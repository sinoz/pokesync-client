package io.pokesync.core.game.ui.provider

import io.pokesync.core.assets.OverworldPlayerTexture
import io.pokesync.core.game.GameAssets
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.ui.element.bag.BagItemProfile

/**
 * Provides player character sprites.
 * @author Sino
 */
interface PlayerSpriteProvider {
    /**
     * Provides a [BagItemProfile] by the specified id.
     */
    fun provide(gender: Gender): OverworldPlayerTexture

    companion object {
        /**
         * Consumes player character sprites from the [OverworldPlayerSpriteCache].
         */
        fun fromAssets(assets: GameAssets): PlayerSpriteProvider =
            object : PlayerSpriteProvider {
                override fun provide(gender: Gender): OverworldPlayerTexture =
                    assets.obtainOverworldPlayerTexture(gender)
            }
    }
}