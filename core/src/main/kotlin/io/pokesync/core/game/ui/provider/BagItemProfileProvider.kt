package io.pokesync.core.game.ui.provider

import io.pokesync.core.game.GameAssets
import io.pokesync.core.game.ui.element.bag.BagItemProfile
import io.pokesync.core.game.world.component.ModelId

/**
 * Provides [BagItemProfile]s.
 * @author Sino
 */
interface BagItemProfileProvider {
    /**
     * Provides a [BagItemProfile] by the specified id.
     */
    fun provide(id: ModelId): BagItemProfile

    companion object {
        /**
         * Consumes information from the given [ItemSpriteCache] to build [BagItemProfile]s.
         */
        fun fromAssets(assets: GameAssets): BagItemProfileProvider =
            object : BagItemProfileProvider {
                override fun provide(id: ModelId): BagItemProfile =
                    BagItemProfile(assets.obtainItemTexture(id), id)
            }
    }
}