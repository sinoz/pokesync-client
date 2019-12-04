package io.pokesync.core.assets.loading

import com.badlogic.gdx.assets.AssetLoaderParameters
import io.pokesync.core.assets.BattleTextureAspect
import io.pokesync.core.assets.MonsterBattleTexture
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.world.component.ModelId

/**
 * TODO
 * @author Sino
 */
data class MonsterBattleTextureParameter(
    val id: ModelId,
    val gender: Gender?,
    val aspect: BattleTextureAspect,
    val coloration: MonsterColoration
) : AssetLoaderParameters<MonsterBattleTexture>()