package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.core.assets.ItemTexture
import io.pokesync.core.game.world.component.ModelId

/**
 * TODO
 * @author Sino
 */
data class ItemTextureParameter(val id: ModelId) : AssetLoaderParameters<ItemTexture>()