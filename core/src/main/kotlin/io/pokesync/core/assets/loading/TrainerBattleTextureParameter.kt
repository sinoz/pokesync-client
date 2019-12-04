package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.core.assets.BattleTextureAspect
import io.pokesync.core.assets.TrainerBattleTexture
import io.pokesync.core.game.world.component.ModelId

/**
 * TODO
 * @author Sino
 */
data class TrainerBattleTextureParameter(
    val id: ModelId,
    val aspect: BattleTextureAspect
) : AssetLoaderParameters<TrainerBattleTexture>()