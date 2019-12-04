package io.pokesync.core.game

import com.badlogic.gdx.assets.AssetManager
import io.pokesync.core.assets.*
import io.pokesync.core.assets.config.*
import io.pokesync.core.assets.loading.*
import io.pokesync.core.assets.texture.ShadowTexture
import io.pokesync.core.assets.texture.TextureList
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.world.WorldGrid
import io.pokesync.core.game.world.component.ModelId
import io.pokesync.lib.gdx.getOrLoad

/**
 * A store of game assets.
 * @author Sino
 */
class GameAssets(val assetManager: AssetManager) {
    /**
     * Obtains an [AnimSeqConfig] from the [assetManager].
     */
    fun obtainAnimationConfig(): AnimSeqConfig {
        return assetManager.getOrLoad(AnimSeqConfig::class.java.simpleName)
    }

    /**
     * Obtains a [ObjectConfig] from the [assetManager].
     */
    fun obtainObjectConfig(): ObjectConfig {
        return assetManager.getOrLoad(ObjectConfig::class.java.simpleName)
    }

    /**
     * Obtains a [ItemConfig] from the [assetManager].
     */
    fun obtainItemConfig(): ItemConfig {
        return assetManager.getOrLoad(ItemConfig::class.java.simpleName)
    }

    /**
     * Obtains a [MonsterConfig] from the [assetManager].
     */
    fun obtainMonsterConfig(): MonsterConfig {
        return assetManager.getOrLoad(MonsterConfig::class.java.simpleName)
    }

    /**
     * Obtains a [NpcConfig] from the [assetManager].
     */
    fun obtainNpcConfig(): NpcConfig {
        return assetManager.getOrLoad(NpcConfig::class.java.simpleName)
    }

    /**
     * Obtains a [WorldConfig] from the [assetManager].
     */
    fun obtainWorldConfig(): WorldConfig {
        return assetManager.getOrLoad(WorldConfig::class.java.simpleName)
    }

    /**
     * Obtains a [WorldGrid] from the [assetManager].
     */
    fun obtainWorldGrid(): WorldGrid {
        return assetManager.getOrLoad(WorldGrid::class.java.simpleName)
    }

    /**
     * Obtains an [ItemTexture] from the [assetManager] by the
     * specified [ModelId].
     */
    fun obtainItemTexture(id: ModelId): ItemTexture {
        return assetManager.getOrLoad("item-${id.value}", ItemTextureParameter(id))
    }

    /**
     * Obtains an [OverworldPlayerTexture] from the [assetManager] by the
     * specified [Gender].
     */
    fun obtainOverworldPlayerTexture(gender: Gender): OverworldPlayerTexture {
        return assetManager.getOrLoad("plr-$gender", OverworldPlayerTextureParameter(gender))
    }

    /**
     * Obtains an [OverworldNpcTexture] from the [assetManager] by the
     * specified [ModelId].
     */
    fun obtainOverworldNpcTexture(id: ModelId): OverworldNpcTexture {
        return assetManager.getOrLoad("npc-${id.value}", OverworldNpcTextureParameter(id))
    }

    /**
     * Obtains an [OverworldMonsterTexture] from the [assetManager] by the
     * specified [ModelId] and [MonsterColoration].
     */
    fun obtainOverworldMonsterTexture(id: ModelId, coloration: MonsterColoration): OverworldMonsterTexture {
        return assetManager.getOrLoad("mon-${id.value}-${coloration}", OverworldMonsterTextureParameter(id, coloration))
    }

    /**
     * Obtains a [ShadowTexture] from the [assetManager].
     */
    fun obtainShadowTexture(): ShadowTexture {
        return ShadowTexture.fromList(assetManager.getOrLoad(ShadowTexture::class.java.simpleName))
    }

    /**
     * Obtains a [TrainerBattleTexture] from the [assetManager] by the
     * specified [ModelId] and [BattleTextureAspect].
     */
    fun obtainTrainerBattleTexture(id: ModelId, aspect: BattleTextureAspect): TrainerBattleTexture {
        return assetManager.getOrLoad("trainer-battle-${id.value}-${aspect}", TrainerBattleTextureParameter(id, aspect))
    }

    /**
     * Obtains a [MonsterBattleTexture] from the [assetManager] by the
     * specified [ModelId], [Gender], [BattleTextureAspect] and
     * [MonsterColoration].
     */
    fun obtainMonsterBattleTexture(
        id: ModelId,
        gender: Gender?,
        aspect: BattleTextureAspect,
        coloration: MonsterColoration
    ): MonsterBattleTexture {
        return assetManager.getOrLoad("mon-battle-${id.value}-${gender}-${aspect}-${coloration}", MonsterBattleTextureParameter(id, gender, aspect, coloration))
    }

    /**
     * Obtains a [TextLabelBank] by the specified [TextBankType].
     */
    fun obtainTextLabelBank(textBankType: TextBankType): TextLabelBank {
        return assetManager.getOrLoad("text-bank-$textBankType", TextLabelParameter(textBankType))
    }
}