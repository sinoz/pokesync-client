package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.core.assets.ItemTexture
import io.pokesync.core.assets.OverworldNpcTexture
import io.pokesync.core.game.world.component.ModelId

/**
 * TODO
 * @author Sino
 */
data class OverworldNpcTextureParameter(val id: ModelId) : AssetLoaderParameters<OverworldNpcTexture>()