package io.pokesync.core.game.ui.provider

import io.pokesync.core.assets.*
import io.pokesync.core.game.GameAssets
import io.pokesync.core.game.model.Gender
import io.pokesync.core.game.model.MonsterColoration
import io.pokesync.core.game.ui.element.dex.MonsterProfile
import io.pokesync.core.game.world.component.ModelId

/**
 * Provides [MonsterProfile]s.
 * @author Sino
 */
interface MonsterProfileProvider {
    /**
     * Provides [MonsterProfile]s by the given information.
     */
    fun provide(
        id: ModelId,
        gender: Gender?,
        coloration: MonsterColoration,
        aspect: BattleTextureAspect
    ): MonsterProfile

    companion object {
        /**
         * Consumes information from the given caches to build [MonsterProfile]s.
         */
        fun fromAssets(assets: GameAssets): MonsterProfileProvider =
            object : MonsterProfileProvider {
                override fun provide(
                    id: ModelId,
                    gender: Gender?,
                    coloration: MonsterColoration,
                    aspect: BattleTextureAspect
                ): MonsterProfile {
                    val overworldTexture = assets.obtainOverworldMonsterTexture(id, coloration)
                    val battleTexture = assets.obtainMonsterBattleTexture(id, gender, aspect, coloration)

                    val name = assets.obtainTextLabelBank(TextBankType.MONSTER_NAMES).labels[id.value].value
                    val title = assets.obtainTextLabelBank(TextBankType.MONSTER_TITLES).labels[id.value].value
                    val description =
                        assets.obtainTextLabelBank(TextBankType.MONSTER_DESCRIPTIONS).labels[id.value].value

                    val config = assets.obtainMonsterConfig()

                    val height = config.entries[id.value].height
                    val weight = config.entries[id.value].weight

                    val primaryType = config.entries[id.value].primaryType
                    val secondaryType = config.entries[id.value].secondaryType

                    return MonsterProfile(
                        overworldTexture,
                        battleTexture,
                        id,
                        name,
                        title,
                        description,
                        height,
                        weight,
                        primaryType,
                        secondaryType
                    )
                }
            }
    }
}