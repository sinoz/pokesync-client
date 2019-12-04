package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * A single entry in the user's pokemon party where a
 * selected item from the bag can be applied onto.
 * @author Sino
 */
class PartyMonsterEntry(skin: Skin, baseSkin: Skin) : Table(skin) {
    init {
        background = skin["monster-entry"]

        add(HealthBar(skin)).padLeft(43F).padTop(20F).row()
        add(ExperienceBar(skin)).padTop(2F).padLeft(43F).row()
    }
}