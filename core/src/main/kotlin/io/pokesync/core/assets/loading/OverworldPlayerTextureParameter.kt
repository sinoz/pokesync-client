package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.core.assets.OverworldPlayerTexture
import io.pokesync.core.game.model.Gender

/**
 * TODO
 * @author Sino
 */
data class OverworldPlayerTextureParameter(val gender: Gender) : AssetLoaderParameters<OverworldPlayerTexture>()