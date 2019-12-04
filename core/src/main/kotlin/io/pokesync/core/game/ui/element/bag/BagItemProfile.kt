package io.pokesync.core.game.ui.element.bag

import io.pokesync.core.assets.ItemTexture
import io.pokesync.core.game.world.component.ModelId

/**
 * A profile of an item which consists of everything the item bag
 * needs to present an item to the user.
 * @author Sino
 */
data class BagItemProfile(
    val texture: ItemTexture,
    val id: ModelId
)