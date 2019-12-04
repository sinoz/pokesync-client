package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.core.assets.OverworldMonsterTexture
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.world.component.ModelId

/**
 * TODO
 * @author Sino
 */
data class OverworldMonsterTextureParameter(val id: ModelId, val coloration: MonsterColoration) :
    AssetLoaderParameters<OverworldMonsterTexture>()