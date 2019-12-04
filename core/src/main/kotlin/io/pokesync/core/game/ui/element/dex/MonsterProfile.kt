package io.pokesync.core.game.ui.element.dex

import io.pokesync.core.assets.MonsterBattleTexture
import io.pokesync.core.assets.OverworldMonsterTexture
import io.pokesync.core.game.world.component.ModelId

/**
 * A profile of a monster which consists of everything a dex
 * or a party needs to present a monster to the user.
 * @author Sino
 */
data class MonsterProfile(
    val overworldTexture: OverworldMonsterTexture,
    val battleTexture: MonsterBattleTexture,

    val id: ModelId,
    val name: String,
    val title: String,
    val description: String,

    val height: Int,
    val weight: Int,

    val primaryType: String,
    val secondaryType: String
)