package io.pokesync.core.game.ui.element.bag

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.style.get

/**
 * The blue background to place the party's monsters onto
 * when applying an item onto a monster.
 * @author Sino
 */
class PartySelectionView(skin: Skin, baseSkin: Skin) : Table(skin) {
    init {
        background = skin["party-selection"]

        width = prefWidth
        height = prefHeight

        for (y in 0 until 3) {
            for (x in 0 until 2) {
                val entryCell = add(PartyMonsterEntry(skin, baseSkin))
                if (x == 1) {
                    entryCell.padLeft(50F)
                }
            }

            row()
        }
    }
}